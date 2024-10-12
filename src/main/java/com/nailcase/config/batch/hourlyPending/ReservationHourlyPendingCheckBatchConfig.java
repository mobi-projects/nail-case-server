package com.nailcase.config.batch.hourlyPending;

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
import com.nailcase.repository.NotificationRepository;
import com.nailcase.service.NotificationService;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ReservationHourlyPendingCheckBatchConfig {

	private final NotificationService notificationService;
	private final NotificationRepository notificationRepository;
	private final EntityManagerFactory entityManagerFactory;

	@Bean(name = "pendingReservationJob")
	public Job notificationJob(JobRepository jobRepository,
		@Qualifier("pendingReservationStep") Step notificationStep) {
		return new JobBuilder("notificationHourlyJob", jobRepository)
			.start(notificationStep)
			.build();
	}

	@Bean(name = "pendingReservationStep")
	public Step notificationStep(JobRepository jobRepository,
		PlatformTransactionManager transactionManager) {
		return new StepBuilder("notificationHourlyStep", jobRepository)
			.<ReservationDetailProjection, ReservationDetailProjection>chunk(10, transactionManager)
			.reader(reservationReader())
			.processor(reservationProcessor())
			.writer(reservationWriter())
			.build();
	}

	@Bean
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
					"WHERE rd.status = 'PENDING' " +
					"AND rd.startTime < :now")
			.parameterValues(Map.of("now", now))
			.pageSize(10)
			.build();
	}

	@Bean(name = "pendingReservationProcessor")
	public ReservationItemProcessor reservationProcessor() {
		return new ReservationItemProcessor(notificationService);
	}

	@Bean(name = "pendingReservationWriter")
	public ReservationItemWriter reservationWriter() {
		return new ReservationItemWriter(notificationRepository);
	}
}
