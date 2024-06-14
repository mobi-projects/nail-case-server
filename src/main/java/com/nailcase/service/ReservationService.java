package com.nailcase.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ReservationErrorCode;
import com.nailcase.mapper.ReservationMapper;
import com.nailcase.model.dto.ReservationDetailDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.NailArtists;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.repository.ReservationDetailRepository;
import com.nailcase.repository.ReservationRepository;
import com.nailcase.util.DateUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationMapper reservationMapper;
	private final ReservationRepository reservationRepository;
	private final ReservationDetailRepository reservationDetailRepository;

	@Transactional
	public ReservationDto.Response createReservation(Long shopId, ReservationDto.Post dto) {
		// startTime, endTime 비교 유효성 검사
		LocalDateTime startTime = DateUtils.unixTimeStampToLocalDateTime(dto.getStartTime());
		LocalDateTime endTime = DateUtils.unixTimeStampToLocalDateTime(dto.getEndTime());
		if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
			throw new BusinessException(ReservationErrorCode.INVALID_TIME_RANGE);
		}

		// 예약 초과인지 여부 검토 => 시간이 reservationDetail 에 저장된 경우
		List<ReservationDetail> reservationDetailList =
			reservationDetailRepository.findOngoingReservationDetailList(shopId, startTime, endTime);
		if (!reservationDetailList.isEmpty()) {
			Integer availableSeats = reservationDetailList.getFirst().getShop().getAvailableSeats();
			int reservationHour = endTime.getHour() - startTime.getHour();
			if (checkReservationAvailability(reservationDetailList, availableSeats, reservationHour, startTime)) {
				throw new BusinessException(ReservationErrorCode.RESERVATION_OVERBOOKED);
			}
		}

		// 예약 생성
		Reservation reservation = reservationMapper.toEntity(shopId, dto);
		reservation.associateDown();
		Reservation savedReservation = reservationRepository.save(reservation);
		return reservationMapper.toResponse(savedReservation);
	}

	@Transactional
	public ReservationDto.Response updateReservation(Long shopId, Long reservationId, ReservationDto.Patch dto) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new RuntimeException("reservation not found"));
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
					.orElseThrow();
				targetReservationDetail.updateArtist(NailArtists.builder().nailArtistId(nailArtistId).build());
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

	private boolean checkReservationAvailability(
		List<ReservationDetail> reservationDetailList,
		Integer availableSeats,
		Integer reservationHour,
		LocalDateTime startTime
	) {
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
}
