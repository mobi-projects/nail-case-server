package com.nailcase.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;
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
		LocalDateTime endTime = DateUtils.unixTimeStampToLocalDateTime(dto.getEndTime());
		if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
			throw new BusinessException(ReservationErrorCode.INVALID_TIME_RANGE);
		}

		// 예약 초과인지 여부 검토
		validateReservationAvailability(shopId, startTime, endTime);

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

	private void validateReservationAvailability(Long shopId, LocalDateTime startTime, LocalDateTime endTime) {
		List<ReservationDetail> reservationDetailList =
			reservationDetailRepository.findOngoingReservationDetailList(shopId, startTime, endTime);
		if (!reservationDetailList.isEmpty()) {
			Integer availableSeats = reservationDetailList.getFirst().getShop().getAvailableSeats();
			int reservationHour = endTime.getHour() - startTime.getHour();
			if (checkReservationAvailability(reservationDetailList, availableSeats, reservationHour, startTime)) {
				throw new BusinessException(ReservationErrorCode.RESERVATION_OVERBOOKED);
			}
		}
	}

	private boolean checkReservationAvailability(
		List<ReservationDetail> reservationDetailList,
		int availableSeats,
		int reservationHour,
		LocalDateTime startTime
	) {
		// TODO: intervalUnit
		return IntStream.range(0, reservationHour)
			.mapToObj(startTime::plusHours)
			.anyMatch(reservationTime ->
				reservationDetailList.stream()
					.filter(reservationDetail ->
						(reservationDetail.getStartTime().isBefore(reservationTime)
							|| reservationDetail.getStartTime().isEqual(reservationTime))
							&& (reservationDetail.getEndTime().isAfter(reservationTime)
							|| reservationDetail.getEndTime().isEqual(reservationTime)))
					.count() >= availableSeats);
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
		List<ReservationDetail> reservationDetails = reservationDetailRepository.findReservationByShopIdAndOnDate(
			shop.getShopId(), time);

		int availableSeats = shop.getAvailableSeats();
		long openTime = DateUtils.localDateTimeToUnixTimeStamp(workHour.getOpenTime());
		long closeTime = DateUtils.localDateTimeToUnixTimeStamp(workHour.getCloseTime());
		Set<NailArtist> nailArtists = shop.getNailArtists();
		List<NailArtist> requestedArtists = nailArtists.stream()
			.filter(nailArtist -> Arrays.stream(artistIds)
				.anyMatch(artistId -> nailArtist.getNailArtistId().equals(artistId)))
			.toList();

		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId = reservationDetails.stream()
			.collect(Collectors.groupingBy(
				r -> r.getNailArtist() == null ? 0L : r.getNailArtist().getNailArtistId()));

		// TODO: intervalUnit이 WorkHour에 들어가거나 Shop에 들어가거나 결정되면 수정
		int intervalUnit = 60 * 30;
		int timeTableCount = (int)((closeTime - openTime) / intervalUnit);

		// 영업시간을 시간 단위로 나누어 예약 시간 생성
		List<Long> times = LongStream.range(0, timeTableCount)
			.mapToObj(i -> openTime + (i * intervalUnit))
			.toList();

		return getAvailables(times, reservationDetails, availableSeats, requestedArtists,
			reservationDetailsOrderByArtistId);
	}

	private List<ReservationDto.Available> getAvailables(List<Long> times,
		List<ReservationDetail> reservationDetails,
		int availableSeats,
		List<NailArtist> requestedArtists,
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId
	) {
		List<ReservationDto.Available> availables = new ArrayList<>();

		int timeIdx = 0;
		int reservationIdx = 0;
		while (timeIdx < times.size() && reservationIdx < reservationDetails.size()) {
			Long time = times.get(timeIdx);
			if (isBeforeReservationStartTime(time, reservationDetails.get(reservationIdx).getStartTime())) {
				ReservationDto.Available available = getAvailableWithArtistNear(availableSeats, requestedArtists,
					reservationDetailsOrderByArtistId, time);
				availables.add(available);
				timeIdx++;
				continue;
			}
			if (isAfterReservationEndTime(time, reservationDetails.get(reservationIdx).getEndTime())) {
				reservationIdx++;
				continue;
			}
			// startTime <= time < endTime 시작
			List<NailArtistDto.Response> artistResponses = getNailArtistResponses(requestedArtists);
			int tempIdx = reservationIdx;
			int tempAvailableSeats = availableSeats;
			long endTime = DateUtils.localDateTimeToUnixTimeStamp(reservationDetails.get(tempIdx).getEndTime());
			while (tempIdx < reservationDetails.size() && time < endTime) {
				ReservationDetail reservationDetail = reservationDetails.get(tempIdx);
				endTime = DateUtils.localDateTimeToUnixTimeStamp(reservationDetail.getEndTime());
				setNearToArtistResponses(reservationDetailsOrderByArtistId, artistResponses, reservationDetail, time);
				tempAvailableSeats--;
				tempIdx++;
			}

			ReservationDto.Available available = getAvailable(tempAvailableSeats, time, artistResponses);
			availables.add(available);

			timeIdx++;
			reservationIdx++;
		}

		while (timeIdx < times.size()) {
			Long time = times.get(timeIdx);
			List<NailArtistDto.Response> responses = getNailArtistResponses(requestedArtists);
			ReservationDto.Available available = getAvailable(availableSeats, time, responses);
			availables.add(available);
			timeIdx++;
		}

		return availables;
	}

	private void setNearToArtistResponses(Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		List<NailArtistDto.Response> artistResponses, ReservationDetail reservationDetail, Long time) {
		for (NailArtistDto.Response artist : artistResponses) {
			if (isArtistAlreadyReserved(artist, reservationDetail)) {
				Long near = getNear(reservationDetailsOrderByArtistId, artist.getId(), time);
				artist.setEnable(false);
				artist.setNear(near);
			}
		}
	}

	private boolean isAfterReservationEndTime(Long timeUnixTimeStamp, LocalDateTime reservationEndTime) {
		return timeUnixTimeStamp >= DateUtils.localDateTimeToUnixTimeStamp(reservationEndTime);
	}

	private boolean isBeforeReservationStartTime(Long timeUnixTimeStamp, LocalDateTime reservationStartTimeUnixTime) {
		return timeUnixTimeStamp < DateUtils.localDateTimeToUnixTimeStamp(reservationStartTimeUnixTime);
	}

	private boolean isArtistAlreadyReserved(NailArtistDto.Response artist, ReservationDetail reservationDetail) {
		return reservationDetail.getNailArtist() != null
			&& artist.getId().equals(reservationDetail.getNailArtist().getNailArtistId());
	}

	private ReservationDto.Available getAvailable(
		int tempAvailableSeats,
		long time,
		List<NailArtistDto.Response> artistResponses
	) {
		ReservationDto.Available available = new ReservationDto.Available();
		available.setAvailableSeats(tempAvailableSeats);
		available.setStartTime(time);
		available.setArtists(artistResponses);
		return available;
	}

	private List<NailArtistDto.Response> getNailArtistResponses(List<NailArtist> requestedArtists) {
		return requestedArtists.stream()
			.map(NailArtistDto.Response::fromEntity)
			.toList();
	}

	private ReservationDto.Available getAvailableWithArtistNear(
		int availableSeats,
		List<NailArtist> requestedArtists,
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		long startTime
	) {
		ReservationDto.Available available = new ReservationDto.Available();

		List<NailArtistDto.Response> artistResponses = requestedArtists.stream()
			.map(NailArtistDto.Response::fromEntity)
			.map(response -> response.setNear(
				getNear(reservationDetailsOrderByArtistId, response.getId(), startTime)))
			.toList();

		available.setStartTime(startTime);
		available.setAvailableSeats(availableSeats);
		available.setArtists(artistResponses);

		return available;
	}

	private @Nullable Long getNear(
		Map<Long, List<ReservationDetail>> reservationDetailsOrderByArtistId,
		long nailArtistId,
		long timeUnixTimeStamp
	) {
		return reservationDetailsOrderByArtistId.getOrDefault(nailArtistId, List.of())
			.stream()
			.filter(rd -> DateUtils.localDateTimeToUnixTimeStamp(rd.getStartTime()) > timeUnixTimeStamp)
			.map(rd -> DateUtils.localDateTimeToUnixTimeStamp(rd.getStartTime()) - timeUnixTimeStamp)
			.findFirst()
			.orElse(null);
	}
}
