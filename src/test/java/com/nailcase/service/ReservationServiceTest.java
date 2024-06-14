package com.nailcase.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nailcase.mapper.ConditionMapperImpl;
import com.nailcase.mapper.ReservationDetailMapperImpl;
import com.nailcase.mapper.ReservationMapper;
import com.nailcase.mapper.ReservationMapperImpl;
import com.nailcase.mapper.TreatmentMapperImpl;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.enums.ConditionOption;
import com.nailcase.model.enums.RemoveOption;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.model.enums.TreatmentOption;
import com.nailcase.repository.ReservationDetailRepository;
import com.nailcase.repository.ReservationRepository;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.util.DateUtils;

@ExtendWith(SpringExtension.class)
class ReservationServiceTest {

	@Spy
	private ReservationMapper reservationMapper = new ReservationMapperImpl(
		new ReservationDetailMapperImpl(new ConditionMapperImpl(), new TreatmentMapperImpl()));

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private ReservationDetailRepository reservationDetailRepository;

	@InjectMocks
	private ReservationService reservationService;

	@Test
	void createReservation() {
		Long shopId = 1L;
		Long reservationId = 1L;
		ReservationDto.Post reservationPostDto = FixtureFactory.createReservationPostDto(
			null,
			List.of(
				FixtureFactory.createReservationDetailPostDto(
					null,
					RemoveOption.IN_SHOP,
					true,
					1718240400L,
					1718247600L,
					List.of(FixtureFactory.createConditionPostDto(ConditionOption.REPAIR)),
					List.of(FixtureFactory.createTreatmentPostDto(TreatmentOption.AOM, 1L, "hello/imageUrl")))
			)
		);

		given(reservationDetailRepository.findOngoingReservationDetailList(shopId,
			DateUtils.unixTimeStampToLocalDateTime(1718240400L),
			DateUtils.unixTimeStampToLocalDateTime(1718247600L))).willReturn(List.of());
		given(reservationRepository.save(any(Reservation.class))).willReturn(this.reservation(reservationId));

		ReservationDto.Response response = reservationService.createReservation(shopId, reservationPostDto);
		assertThat(response).hasFieldOrPropertyWithValue("reservationId", reservationId)
			.hasFieldOrProperty("reservationDetailList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst())
			.hasFieldOrPropertyWithValue("reservationDetailId", 1L)
			.hasFieldOrPropertyWithValue("nailArtistId", 1L)
			.hasFieldOrPropertyWithValue("remove", RemoveOption.IN_SHOP)
			.hasFieldOrPropertyWithValue("status", ReservationStatus.PENDING)
			.hasFieldOrPropertyWithValue("startTime", 1718240400L)
			.hasFieldOrPropertyWithValue("endTime", 1718247600L)
			.hasFieldOrProperty("conditionList")
			.hasFieldOrProperty("treatmentList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getConditionList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getConditionList().getFirst())
			.hasFieldOrPropertyWithValue("option", ConditionOption.REPAIR)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList().getFirst())
			.hasFieldOrPropertyWithValue("option", TreatmentOption.AOM)
			.hasFieldOrPropertyWithValue("imageId", 1L)
			.hasFieldOrPropertyWithValue("imageUrl", "hello/imageUrl")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
	}

	private Reservation reservation(Long reservationId) {
		return FixtureFactory.createReservation(
			reservationId,
			1L,
			1L,
			List.of(FixtureFactory.createReservationDetail(
				1L,
				1L,
				1L,
				List.of(FixtureFactory.createTreatment(1L, TreatmentOption.AOM, 1L, "hello/imageUrl")),
				List.of(FixtureFactory.createCondition(1L, ConditionOption.REPAIR)),
				DateUtils.unixTimeStampToLocalDateTime(1718240400L),
				DateUtils.unixTimeStampToLocalDateTime(1718247600L),
				ReservationStatus.PENDING,
				RemoveOption.IN_SHOP,
				true
			))
		);
	}
}
