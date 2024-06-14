package com.nailcase.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ReservationErrorCode;
import com.nailcase.mapper.ReservationMapper;
import com.nailcase.model.dto.ReservationDto;
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
}
