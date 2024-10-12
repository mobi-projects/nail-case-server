package com.nailcase.config.batch.hourlyConfirm;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.nailcase.model.dto.ReservationDetailProjection;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.repository.ReservationDetailRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationCompleteItemProcessor
	implements ItemProcessor<ReservationDetailProjection, ReservationDetailProjection> {
	private final ReservationDetailRepository reservationDetailRepository;

	@Override
	public ReservationDetailProjection process(ReservationDetailProjection item) throws Exception {
		reservationDetailRepository.updateReservationDetailStatus(item.getReservationDetailId(),
			ReservationStatus.COMPLETED);
		return item;
	}
}

