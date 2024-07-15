package com.nailcase.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ReservationErrorCode;
import com.nailcase.mapper.ConditionMapperImpl;
import com.nailcase.mapper.ReservationDetailMapperImpl;
import com.nailcase.mapper.ReservationMapper;
import com.nailcase.mapper.ReservationMapperImpl;
import com.nailcase.mapper.TreatmentMapperImpl;
import com.nailcase.model.dto.ReservationDetailDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.repository.ReservationDetailRepository;
import com.nailcase.repository.ReservationRepository;
import com.nailcase.testUtils.FixtureFactory;

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
		Long shopId = FixtureFactory.FIXTURE_SHOP_ID;
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		ReservationDto.Post reservationPostDto = FixtureFactory.reservationPostDto();
		given(reservationDetailRepository.findOngoingReservationDetailList(
			shopId,
			FixtureFactory.FIXTURE_RESERVATION_DETAIL_START_TIME
		)).willReturn(List.of());
		given(reservationRepository.save(any(Reservation.class))).willReturn(this.reservation(reservationId));

		ReservationDto.Response response = reservationService.createReservation(
			shopId,
			FixtureFactory.FIXTURE_MEMBER_ID,
			reservationPostDto
		);

		assertThat(response).hasFieldOrPropertyWithValue("reservationId", reservationId)
			.hasFieldOrProperty("reservationDetailList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst())
			.hasFieldOrPropertyWithValue("reservationDetailId", FixtureFactory.FIXTURE_RESERVATION_DETAIL_ID)
			.hasFieldOrPropertyWithValue("nailArtistId", FixtureFactory.FIXTURE_NAIL_ARTIST_ID)
			.hasFieldOrPropertyWithValue("remove", FixtureFactory.FIXTURE_RESERVATION_REMOVE)
			.hasFieldOrPropertyWithValue("status", FixtureFactory.FIXTURE_RESERVATION_STATUS)
			.hasFieldOrPropertyWithValue("startTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME)
			.hasFieldOrPropertyWithValue("endTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_END_UNIX_TIME)
			.hasFieldOrProperty("conditionList")
			.hasFieldOrProperty("treatmentList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getConditionList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getConditionList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_CONDITION_OPTION)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_TREATMENT_OPTION)
			.hasFieldOrPropertyWithValue("imageId", FixtureFactory.FIXTURE_IMAGE_ID)
			.hasFieldOrPropertyWithValue("imageUrl", FixtureFactory.FIXTURE_IMAGE_URL)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
	}

	@Test
	void updateReservation() {
		given(reservationRepository.findById(anyLong()))
			.willReturn(Optional.of(FixtureFactory.reservation()));

		ReservationDto.Response response = reservationService.updateReservation(
			FixtureFactory.FIXTURE_SHOP_ID,
			FixtureFactory.FIXTURE_RESERVATION_ID,
			FixtureFactory.FIXTURE_MEMBER_ID,
			FixtureFactory.reservationPatchDto()
		);

		assertThat(response).hasFieldOrPropertyWithValue("reservationId", FixtureFactory.FIXTURE_RESERVATION_ID)
			.hasFieldOrProperty("reservationDetailList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst())
			.hasFieldOrPropertyWithValue("reservationDetailId", FixtureFactory.FIXTURE_RESERVATION_DETAIL_ID)
			.hasFieldOrPropertyWithValue("nailArtistId", FixtureFactory.FIXTURE_NAIL_ARTIST_ID)
			.hasFieldOrPropertyWithValue("remove", FixtureFactory.FIXTURE_RESERVATION_REMOVE)
			.hasFieldOrPropertyWithValue("status", FixtureFactory.FIXTURE_RESERVATION_PATCH_STATUS)
			.hasFieldOrPropertyWithValue("startTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME)
			.hasFieldOrPropertyWithValue("endTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_END_UNIX_TIME)
			.hasFieldOrProperty("conditionList")
			.hasFieldOrProperty("treatmentList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getConditionList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getConditionList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_CONDITION_OPTION)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_TREATMENT_OPTION)
			.hasFieldOrPropertyWithValue("imageId", FixtureFactory.FIXTURE_IMAGE_ID)
			.hasFieldOrPropertyWithValue("imageUrl", FixtureFactory.FIXTURE_IMAGE_URL)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
	}

	@Test
	void updateReservation_reservationNotFound() {
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

		ReservationDto.Patch reservationPatchDto = FixtureFactory.reservationPatchDto();
		Assertions.assertThrows(
			BusinessException.class,
			() -> reservationService.updateReservation(
				FixtureFactory.FIXTURE_SHOP_ID,
				reservationId,
				FixtureFactory.FIXTURE_MEMBER_ID,
				reservationPatchDto
			),
			ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage()
		);
	}

	@Test
	void updateReservation_shopNotFound() {
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

		ReservationDto.Patch reservationPatchDto = FixtureFactory.reservationPatchDto();
		Assertions.assertThrows(
			BusinessException.class,
			() -> reservationService.updateReservation(
				FixtureFactory.FIXTURE_SHOP_ID + 1L,
				reservationId,
				FixtureFactory.FIXTURE_MEMBER_ID,
				reservationPatchDto
			),
			CommonErrorCode.NOT_FOUND.getMessage()
		);
	}

	@Test
	void updateReservation_notUpdatable_whenCanceled() {
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		Reservation reservation = FixtureFactory.reservation();
		for (ReservationDetail reservationDetail : reservation.getReservationDetailList()) {
			reservationDetail.updateStatus(ReservationStatus.CANCELED);
		}
		given(reservationRepository.findById(reservationId))
			.willReturn(Optional.of(reservation));

		ReservationDto.Patch reservationPatchDto = FixtureFactory.reservationPatchDto();
		Assertions.assertThrows(
			BusinessException.class,
			() -> reservationService.updateReservation(
				FixtureFactory.FIXTURE_SHOP_ID,
				reservationId,
				FixtureFactory.FIXTURE_MEMBER_ID,
				reservationPatchDto
			),
			ReservationErrorCode.STATUS_NOT_UPDATABLE.getMessage()
		);
	}

	@Test
	void updateReservation_notUpdatable_whenRejected() {
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		Reservation reservation = FixtureFactory.reservation();
		for (ReservationDetail reservationDetail : reservation.getReservationDetailList()) {
			reservationDetail.updateStatus(ReservationStatus.REJECTED);
		}
		given(reservationRepository.findById(reservationId))
			.willReturn(Optional.of(reservation));

		ReservationDto.Patch reservationPatchDto = FixtureFactory.reservationPatchDto();
		Assertions.assertThrows(
			BusinessException.class,
			() -> reservationService.updateReservation(
				FixtureFactory.FIXTURE_SHOP_ID,
				reservationId,
				FixtureFactory.FIXTURE_MEMBER_ID,
				reservationPatchDto
			),
			ReservationErrorCode.STATUS_NOT_UPDATABLE.getMessage()
		);
	}

	@Test
	void updateReservation_notUpdatable_whenConfirmed() {
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		Reservation reservation = FixtureFactory.reservation();
		for (ReservationDetail reservationDetail : reservation.getReservationDetailList()) {
			reservationDetail.updateStatus(ReservationStatus.CONFIRMED);
		}
		ReservationDto.Patch reservationPatchDto = FixtureFactory.reservationPatchDto();
		reservationPatchDto.setStatus(ReservationStatus.PENDING);
		given(reservationRepository.findById(reservationId))
			.willReturn(Optional.of(reservation));

		Assertions.assertThrows(
			BusinessException.class,
			() -> reservationService.updateReservation(
				FixtureFactory.FIXTURE_SHOP_ID,
				reservationId,
				FixtureFactory.FIXTURE_MEMBER_ID,
				reservationPatchDto
			),
			ReservationErrorCode.STATUS_NOT_UPDATABLE.getMessage()
		);
	}

	@Test
	void updateReservation_reservationDetailNotFound() {
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		ReservationDto.Patch reservationPatchDto = FixtureFactory.reservationPatchDto();
		for (ReservationDetailDto.Patch patch : reservationPatchDto.getReservationDetailDtoList()) {
			patch.setReservationDetailId(patch.getReservationDetailId() + 1);
		}
		given(reservationRepository.findById(reservationId))
			.willReturn(Optional.of(FixtureFactory.reservation()));

		Assertions.assertThrows(
			BusinessException.class,
			() -> reservationService.updateReservation(
				FixtureFactory.FIXTURE_SHOP_ID,
				reservationId,
				FixtureFactory.FIXTURE_MEMBER_ID,
				reservationPatchDto
			),
			ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage()
		);
	}

	@Test
	void listReservation() {
		given(reservationRepository
			.findReservationListWithinDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(
				ReservationStatus.class)))
			.willReturn(List.of(this.reservation(1L), this.reservation(2L), this.reservation(3L)));

		List<ReservationDto.Response> responses = reservationService.listReservation(
			FixtureFactory.FIXTURE_SHOP_ID,
			FixtureFactory.FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME,
			FixtureFactory.FIXTURE_RESERVATION_DETAIL_END_UNIX_TIME,
			FixtureFactory.FIXTURE_RESERVATION_STATUS
		);

		ReservationDto.Response response = responses.getFirst();
		assertThat(responses).hasSize(3);
		assertThat(response).hasFieldOrPropertyWithValue("reservationId", 1L)
			.hasFieldOrProperty("reservationDetailList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst())
			.hasFieldOrPropertyWithValue("reservationDetailId", FixtureFactory.FIXTURE_RESERVATION_DETAIL_ID)
			.hasFieldOrPropertyWithValue("nailArtistId", FixtureFactory.FIXTURE_NAIL_ARTIST_ID)
			.hasFieldOrPropertyWithValue("remove", FixtureFactory.FIXTURE_RESERVATION_REMOVE)
			.hasFieldOrPropertyWithValue("status", FixtureFactory.FIXTURE_RESERVATION_STATUS)
			.hasFieldOrPropertyWithValue("startTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME)
			.hasFieldOrPropertyWithValue("endTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_END_UNIX_TIME)
			.hasFieldOrProperty("conditionList")
			.hasFieldOrProperty("treatmentList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getConditionList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getConditionList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_CONDITION_OPTION)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_TREATMENT_OPTION)
			.hasFieldOrPropertyWithValue("imageId", 1L)
			.hasFieldOrPropertyWithValue("imageUrl", "hello/imageUrl")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
	}

	@Test
	void viewReservation() {
		Long reservationId = FixtureFactory.FIXTURE_RESERVATION_ID;
		given(reservationRepository.findById(anyLong()))
			.willReturn(Optional.ofNullable(this.reservation(reservationId)));

		ReservationDto.Response response = reservationService.viewReservation(
			FixtureFactory.FIXTURE_SHOP_ID,
			reservationId
		);

		assertThat(response).hasFieldOrPropertyWithValue("reservationId", FixtureFactory.FIXTURE_RESERVATION_ID)
			.hasFieldOrProperty("reservationDetailList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst())
			.hasFieldOrPropertyWithValue("reservationDetailId", FixtureFactory.FIXTURE_RESERVATION_DETAIL_ID)
			.hasFieldOrPropertyWithValue("nailArtistId", FixtureFactory.FIXTURE_NAIL_ARTIST_ID)
			.hasFieldOrPropertyWithValue("remove", FixtureFactory.FIXTURE_RESERVATION_REMOVE)
			.hasFieldOrPropertyWithValue("status", FixtureFactory.FIXTURE_RESERVATION_STATUS)
			.hasFieldOrPropertyWithValue("startTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME)
			.hasFieldOrPropertyWithValue("endTime", FixtureFactory.FIXTURE_RESERVATION_DETAIL_END_UNIX_TIME)
			.hasFieldOrProperty("conditionList")
			.hasFieldOrProperty("treatmentList")
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getConditionList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getConditionList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_CONDITION_OPTION)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList()).hasSize(1);
		assertThat(response.getReservationDetailList().getFirst().getTreatmentList().getFirst())
			.hasFieldOrPropertyWithValue("option", FixtureFactory.FIXTURE_TREATMENT_OPTION)
			.hasFieldOrPropertyWithValue("imageId", FixtureFactory.FIXTURE_IMAGE_ID)
			.hasFieldOrPropertyWithValue("imageUrl", FixtureFactory.FIXTURE_IMAGE_URL)
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
	}

	private Reservation reservation(Long reservationId) {
		return FixtureFactory.createReservation(
			reservationId,
			FixtureFactory.FIXTURE_SHOP_ID,
			FixtureFactory.FIXTURE_NAIL_ARTIST_ID,
			Set.of(FixtureFactory.reservationDetail())
		);
	}
}
