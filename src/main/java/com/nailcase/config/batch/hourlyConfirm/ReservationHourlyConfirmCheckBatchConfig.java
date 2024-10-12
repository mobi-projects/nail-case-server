package com.nailcase.config.batch.hourlyConfirm;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.nailcase.model.dto.ReservationDetailProjection;
import com.nailcase.repository.ReservationDetailRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ReservationHourlyConfirmCheckBatchConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final ReservationDetailRepository reservationDetailRepository;

	@Bean(name = "confirmReservationJob")
	public Job reservationCompleteJob(JobRepository jobRepository,
		@Qualifier("confirmReservationStep") Step reservationCompleteStep) {
		return new JobBuilder("reservationCompleteHourlyJob", jobRepository)
			.start(reservationCompleteStep)
			.build();
	}

	@Bean(name = "confirmReservationStep")
	public Step reservationCompleteStep(JobRepository jobRepository,
		PlatformTransactionManager transactionManager) {
		return new StepBuilder("reservationCompleteHourlyStep", jobRepository)
			.<ReservationDetailProjection, ReservationDetailProjection>chunk(10, transactionManager)
			.reader(reservationReader())
			.processor(reservationCompleteItemProcessor())
			.writer(reservationCompleteItemWriter())
			.build();
	}

	@Bean(name = "confirmReservationReader")
	public JpaPagingItemReader<ReservationDetailProjection> reservationReader() {
		ZoneId zoneId = ZoneId.of("Asia/Seoul");
		LocalDateTime now = LocalDateTime.now(zoneId);

		return new JpaPagingItemReaderBuilder<ReservationDetailProjection>()
			.name("reservationReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString(
				"SELECT NEW com.nailcase.model.dto.ReservationDetailProjection(" +
					"rd.reservationDetailId, s.shopId, s.shopName, r.customer.memberId) " +
					"FROM ReservationDetail rd " +
					"JOIN rd.shop s " +
					"JOIN Reservation r ON r.reservationDetail = rd " +
					"WHERE rd.status = 'CONFIRMED' " +
					"AND rd.endTime IS NOT NULL " +
					"AND rd.endTime < :now")
			.parameterValues(Map.of("now", now))
			.pageSize(10)
			.build();
	}

	@Bean(name = "confirmReservationProcessor")
	public ReservationCompleteItemProcessor reservationCompleteItemProcessor() {
		return new ReservationCompleteItemProcessor(reservationDetailRepository);
	}

	@Bean(name = "confirmReservationWriter")
	public ReservationCompleteItemWriter reservationCompleteItemWriter() {
		return new ReservationCompleteItemWriter();
	}
}
