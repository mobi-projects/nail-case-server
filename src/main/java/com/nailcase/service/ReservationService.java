package com.nailcase.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ReservationErrorCode;
import com.nailcase.mapper.ReservationMapper;
import com.nailcase.model.dto.NailArtistDto;
import com.nailcase.model.dto.ReservationDetailDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.ReservationDto.Available.TimeStatus;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.repository.ReservationDetailRepository;
import com.nailcase.repository.ReservationRepository;
import com.nailcase.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReservationService {

	private final ReservationMapper reservationMapper;
	private final ReservationRepository reservationRepository;
	private final ReservationDetailRepository reservationDetailRepository;

	@Transactional
	public ReservationDto.Response createReservation(Long shopId, Long memberId, ReservationDto.Post dto) {
		// startTime, endTime 비교 유효성 검사
		LocalDateTime startTime = DateUtils.unixTimeStampToLocalDateTime(dto.getStartTime());
		// 예약 초과인지 여부 검토
		validateReservationAvailability(shopId, startTime);

		// 예약 생성
		Reservation reservation = reservationMapper.toEntity(shopId, memberId, dto);
		reservation.associateDown();
		Reservation savedReservation = reservationRepository.save(reservation);
		return reservationMapper.toResponse(savedReservation);
	}

	@Transactional
	public ReservationDto.Response updateReservation(
		Long shopId,
		Long reservationId,
		Long memberId,
		ReservationDto.Patch dto
	) {
		// TODO: memberId 가 shop에 권한이 있는 사람이거나 예약자 본인이어야 함 -> or 조건 쿼리, 어플리케이션 처리 선택
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));
		if (!reservation.getShop().getShopId().equals(shopId)) {
			throw new BusinessException(CommonErrorCode.NOT_FOUND);
		}

		// TODO: api 분리 status / nailArtist
		if (dto.getStatus() != null) {
			if (!isUpdatable(dto, reservation)) {
				throw new BusinessException(ReservationErrorCode.STATUS_NOT_UPDATABLE);
			}
			reservation.getReservationDetailList()
				.forEach(reservationDetail -> reservationDetail.updateStatus(dto.getStatus()));
		}

		// TODO: api 분리 status / nailArtist
		// TODO: status 가 canceled, rejected, complete 라면 변경 불가능
		if (!dto.getReservationDetailDtoList().isEmpty()) {
			for (ReservationDetailDto.Patch detailDto : dto.getReservationDetailDtoList()) {
				Long targetReservationDetailId = detailDto.getReservationDetailId();
				Long nailArtistId = detailDto.getNailArtistId();
				ReservationDetail targetReservationDetail = reservation.getReservationDetailList().stream()
					.filter(reservationDetail ->
						reservationDetail.getReservationDetailId().equals(targetReservationDetailId))
					.findAny()
					.orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));
				targetReservationDetail.updateArtist(NailArtist.builder().nailArtistId(nailArtistId).build());
			}
		}

		return reservationMapper.toResponse(reservation);
	}

	public List<ReservationDto.Response> listReservation(Long shopId, Long startUnixTimeStamp, Long endUnixTimeStamp) {
		LocalDateTime startDate = DateUtils.unixTimeStampToLocalDateTime(startUnixTimeStamp);
		LocalDateTime endDate = DateUtils.unixTimeStampToLocalDateTime(endUnixTimeStamp);
		List<Reservation> reservationList =
			reservationRepository.findReservationListWithinDateRange(shopId, startDate, endDate);
		return reservationList.stream()
			.map(reservationMapper::toResponse)
			.toList();
	}

	public ReservationDto.Response viewReservation(Long shopId, Long reservationId) {
		return reservationRepository.findById(reservationId)
			.filter(reservation -> reservation.getShop().getShopId().equals(shopId))
			.map(reservationMapper::toResponse)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
	}

	private void validateReservationAvailability(Long shopId, LocalDateTime startTime) {
		// eq(shopId), eq(startTime), eq(PENDING), eq(CONFIRMED)
		List<ReservationDetail> reservationDetails =
			reservationDetailRepository.findOngoingReservationDetailList(shopId, startTime);
		if (!reservationDetails.isEmpty() && checkReservationAvailability(reservationDetails)) {
			throw new BusinessException(ReservationErrorCode.RESERVATION_OVERBOOKED);
		}
	}

	private boolean checkReservationAvailability(
		List<ReservationDetail> reservationDetails
	) {
		int pendingMultiplier = 3;
		int tempAvailableSeats = reservationDetails.getFirst().getShop().getAvailableSeats();

		Map<ReservationStatus, List<ReservationDetail>> groupByStatus = reservationDetails.stream()
			.collect(Collectors.groupingBy(ReservationDetail::getStatus));
		List<ReservationDetail> confirmed = groupByStatus.get(ReservationStatus.CONFIRMED);
		List<ReservationDetail> pending = groupByStatus.get(ReservationStatus.PENDING);
		tempAvailableSeats -= confirmed.size();
		tempAvailableSeats *= pendingMultiplier;
		tempAvailableSeats -= pending.size();
		return tempAvailableSeats > 0;
	}

	private boolean isUpdatable(ReservationDto.Patch dto, Reservation reservation) {
		return reservation.getReservationDetailList().stream()
			.anyMatch(reservationDetail -> reservationDetail.isStatusUpdatable(dto.getStatus()));
	}

	public List<Reservation> findReservationsByCustomer(Member member) {
		return reservationRepository.findByCustomer(member);
	}

	public List<ReservationDto.Available> listAvailableTime(Shop shop, Long[] artistIds, WorkHour workHour, Long date) {
		LocalDateTime time = DateUtils.unixTimeStampToLocalDateTime(date);
		getReservationTimeIfBeforeEnableTime(time);

		List<ReservationDetail> reservationDetails =
			reservationDetailRepository.findReservationByShopIdAndOnDate(shop.getShopId(), time);

		int availableSeats = Math.min(shop.getAvailableSeats(), shop.getNailArtists().size());
		long openTime = DateUtils.combineLocalDateTimeToUnixTimeStamp(time, workHour.getOpenTime());
		long closeTime = DateUtils.combineLocalDateTimeToUnixTimeStamp(time, workHour.getCloseTime());
		List<NailArtist> requestedArtists = shop.getNailArtists().stream()
			.filter(nailArtist -> Arrays.stream(artistIds)
				.anyMatch(artistId -> nailArtist.getNailArtistId().equals(artistId)))
			.toList();

		Map<Long, List<ReservationDetail>> reservationDetailsGroupByArtistId =
			reservationDetails.stream()
				.collect(Collectors.groupingBy(ReservationDetail::getNailArtistIdOrZero));

		// TODO: intervalUnit이 WorkHour에 들어가거나 Shop에 들어가거나 결정되면 수정
		int intervalUnit = 60 * 30;
		int numSlotInBusinessHour = (int)((closeTime - openTime) / intervalUnit);

		List<Long> reservationStartTimes = LongStream.range(0, numSlotInBusinessHour)
			.mapToObj(i -> openTime + (i * intervalUnit))
			.toList();

		return generateAvailableReservations(reservationStartTimes, reservationDetails, availableSeats,
			requestedArtists,
			reservationDetailsGroupByArtistId);
	}

	private void getReservationTimeIfBeforeEnableTime(LocalDateTime date) {
		long reservationPeriod = 1L;
		LocalDateTime today = LocalDate.now().atTime(LocalTime.MIN);
		if (date.isAfter(today.plusMonths(reservationPeriod)) && date.isBefore(today)) {
			throw new BusinessException(ReservationErrorCode.INVALID_TIME);
		}
	}

	private List<ReservationDto.Available> generateAvailableReservations(List<Long> reservationStartTimes,
		List<ReservationDetail> reservationDetails, int availableSeats, List<NailArtist> requestedArtists,
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId) {
		List<ReservationDto.Available> availables = new ArrayList<>();
		long currentUnixTime = DateUtils.localDateTimeToUnixTimeStamp(LocalDateTime.now());

		int timeIdx = 0; // reservationStartTimesIdx
		int reservationIdx = 0; // reservationDetailsIdx
		while (timeIdx < reservationStartTimes.size() && reservationIdx < reservationDetails.size()) {
			long startTime = reservationStartTimes.get(timeIdx);
			long reservationStartTime =
				DateUtils.localDateTimeToUnixTimeStamp(reservationDetails.get(reservationIdx).getStartTime());
			long reservationEndTime =
				DateUtils.localDateTimeToUnixTimeStamp(reservationDetails.get(reservationIdx).getEndTime());
			TimeStatus timeStatus =
				evaluationTimeStatus(startTime, currentUnixTime, reservationStartTime, reservationEndTime);

			IndexChanges indexChanges = switch (timeStatus) {
				case TimeStatus.BEFORE_NOW -> processBeforeNow(availableSeats, startTime, availables);
				case TimeStatus.BEFORE_START ->
					processBeforeStartTime(availableSeats, startTime, requestedArtists, availables,
						reservationDetailsOrderByArtistId);
				case TimeStatus.AFTER_END -> processAfterEndTime();
				case TimeStatus.BETWEEN_RESERVATION ->
					betweenReservationTime(reservationIdx, availableSeats, startTime, requestedArtists,
						reservationDetails, reservationDetailsOrderByArtistId, availables);
			};

			timeIdx = timeIdx + indexChanges.itemIdx();
			reservationIdx = reservationIdx + indexChanges.itemIdx();
		}

		finalizeProcessing(reservationStartTimes, availableSeats, requestedArtists, timeIdx, currentUnixTime,
			availables);

		return availables;
	}

	private boolean isArtistAlreadyReserved(NailArtistDto.Response artist, ReservationDetail reservationDetail) {
		return reservationDetail.getNailArtist() != null
			&& artist.getId().equals(reservationDetail.getNailArtist().getNailArtistId());
	}

	private @Nullable Long findNextTimeForArtist(Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		long nailArtistId, long timeUnixTimeStamp) {
		return reservationDetailsOrderByArtistId.getOrDefault(nailArtistId, List.of())
			.stream()
			.filter(rd -> DateUtils.localDateTimeToUnixTimeStamp(rd.getStartTime()) > timeUnixTimeStamp)
			.map(rd -> DateUtils.localDateTimeToUnixTimeStamp(rd.getStartTime()) - timeUnixTimeStamp)
			.findFirst()
			.orElse(null);
	}

	private void finalizeProcessing(List<Long> reservationStartTimes, int availableSeats,
		List<NailArtist> requestedArtists, int timeIdx, Long currentUnixTime,
		List<ReservationDto.Available> availables) {
		while (timeIdx < reservationStartTimes.size()) {
			Long startTime = reservationStartTimes.get(timeIdx);
			if (startTime < currentUnixTime) {
				processBeforeNow(availableSeats, startTime, availables);
			} else {
				processAfterAllEndTime(availableSeats, startTime, requestedArtists, availables);
			}
			timeIdx++;
		}
	}

	private TimeStatus evaluationTimeStatus(long time, long now, long startTime, long endTime) {
		if (time < now) {
			return TimeStatus.BEFORE_NOW;
		}
		if (time < startTime) {
			return TimeStatus.BEFORE_START;
		}
		if (time >= endTime) {
			return TimeStatus.AFTER_END;
		}
		return TimeStatus.BETWEEN_RESERVATION;
	}

	private IndexChanges processBeforeNow(int availableSeats, Long time, List<ReservationDto.Available> availables) {
		ReservationDto.Available available = new ReservationDto.Available();
		available.setEnable(false);
		available.setAvailableSeats(availableSeats);
		available.setStartTime(time);

		availables.add(available);

		return new IndexChanges(1, 0);
	}

	private IndexChanges processBeforeStartTime(int availableSeats, long time, List<NailArtist> requestedArtists,
		List<ReservationDto.Available> availables,
		Map<Long, List<ReservationDetail>> reservationDetailsGroupByArtistId) {
		ReservationDto.Available available = new ReservationDto.Available();

		List<NailArtistDto.Response> artistResponses = toResponses(requestedArtists)
			.stream()
			.map(response -> response.setNear(
				findNextTimeForArtist(reservationDetailsGroupByArtistId, response.getId(), time)))
			.toList();

		available.setStartTime(time);
		available.setAvailableSeats(availableSeats);
		available.setEnable(true);
		available.setArtists(artistResponses);

		availables.add(available);

		return new IndexChanges(1, 0);
	}

	private IndexChanges processAfterEndTime() {
		return new IndexChanges(0, 1);
	}

	private IndexChanges betweenReservationTime(int reservationIdx, int availableSeats, long time,
		List<NailArtist> requestedArtists, List<ReservationDetail> reservationDetails,
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		List<ReservationDto.Available> availables) {
		List<NailArtistDto.Response> artistResponses = toResponses(requestedArtists);

		long endTime = DateUtils.localDateTimeToUnixTimeStamp(reservationDetails.get(reservationIdx).getEndTime());
		boolean successSetNearTimes = true;
		while (reservationIdx < reservationDetails.size() && time < endTime && successSetNearTimes) {
			ReservationDetail reservationDetail = reservationDetails.get(reservationIdx);
			successSetNearTimes = setNearTimes(reservationDetailsOrderByArtistId, artistResponses, reservationDetail,
				time);

			endTime = DateUtils.localDateTimeToUnixTimeStamp(reservationDetail.getEndTime());
			availableSeats--;
			reservationIdx++;
		}

		if (successSetNearTimes) {
			artistResponses = new ArrayList<>();
		}

		ReservationDto.Available available = new ReservationDto.Available();
		available.setAvailableSeats(availableSeats);
		available.setStartTime(time);
		available.setArtists(artistResponses);
		available.setEnable(successSetNearTimes);

		availables.add(available);

		return new IndexChanges(1, 1);
	}

	private void processAfterAllEndTime(int availableSeats, long time, List<NailArtist> requestedArtists,
		List<ReservationDto.Available> availables) {
		List<NailArtistDto.Response> responses = toResponses(requestedArtists);

		ReservationDto.Available available = new ReservationDto.Available();
		available.setAvailableSeats(availableSeats);
		available.setStartTime(time);
		available.setEnable(true);
		available.setArtists(responses);

		availables.add(available);
	}

	private boolean setNearTimes(Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		List<NailArtistDto.Response> artistResponses, ReservationDetail reservationDetail, Long time) {
		for (NailArtistDto.Response artist : artistResponses) {
			if (isArtistAlreadyReserved(artist, reservationDetail)) {
				return false;
			}
			Long near = findNextTimeForArtist(reservationDetailsOrderByArtistId, artist.getId(), time);
			artist.setNear(near);
		}
		return true;
	}

	private List<NailArtistDto.Response> toResponses(List<NailArtist> requestedArtists) {
		return requestedArtists.stream()
			.map(NailArtistDto.Response::fromEntity)
			.toList();
	}

	record IndexChanges(int itemIdx, int reservationIdx) {

	}
}
