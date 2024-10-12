package com.nailcase.config.batch.hourlyConfirm;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.nailcase.model.dto.ReservationDetailProjection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCompleteItemWriter implements ItemWriter<ReservationDetailProjection> {
	@Override
	public void write(Chunk<? extends ReservationDetailProjection> chunk) throws Exception {
		log.info("Processed {} reservations", chunk.size());
		// 추가적인 로깅이나 후처리 로직을 여기에 구현할 수 있습니다.
	}
}
