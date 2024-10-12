package com.nailcase.config.batch;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class BatchSchedulerConfig {

	private final JobLauncher jobLauncher;
	private final Job reservationPendingJob;
	private final Job reservationConfirmJob;

	public BatchSchedulerConfig(
		JobLauncher jobLauncher,
		@Qualifier("pendingReservationJob") Job reservationPendingJob,
		@Qualifier("confirmReservationJob") Job reservationConfirmJob) {
		this.jobLauncher = jobLauncher;
		this.reservationPendingJob = reservationPendingJob;
		this.reservationConfirmJob = reservationConfirmJob;
	}

	@Scheduled(cron = "0 */30 * * * *")  // 30분마다 실행
	public void runPendingBatch() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("date", new Date())
			.toJobParameters();
		jobLauncher.run(reservationPendingJob, jobParameters);
	}

	@Scheduled(cron = "0 15 * * * *")  // 매시 15분에 실행
	public void runConfirmBatch() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("date", new Date())
			.toJobParameters();
		jobLauncher.run(reservationConfirmJob, jobParameters);
	}
}