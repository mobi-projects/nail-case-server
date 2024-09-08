package com.nailcase.testUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.nailcase.model.dto.ConditionDto;
import com.nailcase.model.dto.ReservationDetailDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.TreatmentDto;
import com.nailcase.model.entity.Condition;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.Treatment;
import com.nailcase.model.enums.ConditionOption;
import com.nailcase.model.enums.RemoveOption;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.model.enums.TreatmentOption;
import com.nailcase.testUtils.fixture.FileFixture;
import com.nailcase.testUtils.fixture.MemberFixture;
import com.nailcase.testUtils.fixture.MemberFixtureToBootTest;
import com.nailcase.testUtils.fixture.NailArtistFixture;
import com.nailcase.testUtils.fixture.NailArtistFixtureToBootTest;
import com.nailcase.testUtils.fixture.ShopFixture;
import com.nailcase.testUtils.fixture.ShopInfoFixture;
import com.nailcase.testUtils.fixture.WorkHourFixture;
import com.nailcase.util.DateUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 테스트를 위해 미리 정의된 데이터를 생성하는 유틸리티 클래스입니다.
 * 이 클래스는 다양한 유형의 테스트 데이터를 생성하는 메서드를 제공합니다.
 *
 * <p>예시 사용법:</p>
 * <pre>{@code
 * // 사용자 객체를 생성합니다.
 * User user = FixtureFactory.createUser("홍홍홍", 33);
 *
 * // 랜덤한 사용자 객체를 생성합니다.
 * User randomUser = FixtureFactory.createRandomUser();
 *
 * // 5명의 랜덤한 사용자 객체 리스트를 생성합니다.
 * List<User> userList = FixtureFactory.createUserList(5);
 * }</pre>
 */
@Getter
@Component
@RequiredArgsConstructor
public class FixtureFactory {

	public static final MemberFixture memberFixture = new MemberFixture();
	public static final NailArtistFixture nailArtistFixture = new NailArtistFixture();
	public static final ShopFixture shopFixture = new ShopFixture();
	public static final FileFixture fileFixture = new FileFixture();
	public static final WorkHourFixture workHourFixture = new WorkHourFixture();
	public static final ShopInfoFixture shopInfoFixture = new ShopInfoFixture();
	// MEMBER
	public static final Long FIXTURE_MEMBER_ID = 1L;
	// SHOP
	public static final Long FIXTURE_SHOP_ID = 1L;
	public static final int FIXTURE_AVAILABLE_SEATS = 3;
	// NAIL_ARTIST
	public static final Long FIXTURE_NAIL_ARTIST_ID = 1L;
	// RESERVATION
	public static final Long FIXTURE_RESERVATION_ID = 1L;
	// RESERVATION_DETAIL
	public static final Long FIXTURE_RESERVATION_DETAIL_ID = 1L;
	public static final Long FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME = 1718240400L;
	public static final LocalDateTime FIXTURE_RESERVATION_DETAIL_START_TIME =
		DateUtils.unixTimeStampToLocalDateTime(FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME);
	public static final Long FIXTURE_RESERVATION_DETAIL_END_UNIX_TIME = 1718247600L;
	public static final LocalDateTime FIXTURE_RESERVATION_DETAIL_END_TIME =
		DateUtils.unixTimeStampToLocalDateTime(FIXTURE_RESERVATION_DETAIL_END_UNIX_TIME);
	public static final ReservationStatus FIXTURE_RESERVATION_STATUS = ReservationStatus.PENDING;
	public static final ReservationStatus FIXTURE_RESERVATION_PATCH_STATUS = ReservationStatus.CANCELED;
	public static final RemoveOption FIXTURE_RESERVATION_REMOVE = RemoveOption.IN_SHOP;
	public static final Boolean FIXTURE_RESERVATION_EXTEND = true;
	// CONDITION
	public static final Long FIXTURE_CONDITION_ID = 1L;
	public static final ConditionOption FIXTURE_CONDITION_OPTION = ConditionOption.REPAIR;
	// TREATMENT
	public static final Long FIXTURE_TREATMENT_ID = 1L;
	public static final TreatmentOption FIXTURE_TREATMENT_OPTION = TreatmentOption.AOM;
	public static final Long FIXTURE_IMAGE_ID = 1L;
	public static final String FIXTURE_IMAGE_URL = "hello/imageUrl";
	private final MemberFixtureToBootTest memberFixtureToBootTest;
	private final NailArtistFixtureToBootTest nailArtistFixtureToBootTest;

	public static Treatment treatment() {
		return createTreatment(
			FIXTURE_TREATMENT_ID,
			FIXTURE_TREATMENT_OPTION,
			FIXTURE_IMAGE_ID,
			FIXTURE_IMAGE_URL
		);
	}

	public static TreatmentDto.Post treatmentPostDto() {
		return createTreatmentPostDto(
			FIXTURE_TREATMENT_OPTION,
			FIXTURE_IMAGE_ID);
	}

	public static Condition condition() {
		return createCondition(FIXTURE_CONDITION_ID, FIXTURE_CONDITION_OPTION);
	}

	public static ConditionDto.Post conditionPostDto() {
		return createConditionPostDto(FIXTURE_CONDITION_OPTION);
	}

	public static ReservationDetail reservationDetail() {
		return createReservationDetail(
			FIXTURE_RESERVATION_DETAIL_ID,
			FIXTURE_SHOP_ID,
			FIXTURE_NAIL_ARTIST_ID,
			treatment(),
			Set.of(condition()),
			FIXTURE_RESERVATION_DETAIL_START_TIME,
			FIXTURE_RESERVATION_DETAIL_END_TIME,
			FIXTURE_RESERVATION_STATUS,
			FIXTURE_RESERVATION_REMOVE,
			FIXTURE_RESERVATION_EXTEND
		);
	}

	public static ReservationDetailDto.Post reservationDetailPostDto() {
		return createReservationDetailPostDto(
			FIXTURE_SHOP_ID,
			FIXTURE_RESERVATION_REMOVE,
			FIXTURE_RESERVATION_EXTEND,
			FIXTURE_RESERVATION_DETAIL_START_UNIX_TIME,
			List.of(conditionPostDto()),
			treatmentPostDto()
		);
	}

	public static ReservationDetailDto.Patch reservationDetailPatchDto() {
		return createReservationDetailPatchDto(
			FIXTURE_RESERVATION_DETAIL_ID,
			FIXTURE_NAIL_ARTIST_ID
		);
	}

	public static Reservation reservation() {
		return createReservation(
			FIXTURE_RESERVATION_ID,
			FIXTURE_SHOP_ID,
			FIXTURE_NAIL_ARTIST_ID,
			reservationDetail()
		);
	}

	public static ReservationDto.Post reservationPostDto() {
		return createReservationPostDto(
			FIXTURE_SHOP_ID,
			reservationDetailPostDto()
		);
	}

	public static ReservationDto.Patch reservationPatchDto() {
		return createReservationPatchDto(
			FIXTURE_RESERVATION_PATCH_STATUS,
			List.of(reservationDetailPatchDto())
		);
	}

	public static ReservationDto.Post createReservationPostDto(
		Long shopId,
		ReservationDetailDto.Post reservationDetail
	) {
		ReservationDto.Post reservationDto = new ReservationDto.Post();
		reservationDto.setShopId(shopId);
		return reservationDto;
	}

	public static ReservationDetailDto.Post createReservationDetailPostDto(
		Long shopId,
		RemoveOption remove,
		boolean extend,
		Long startTime,
		List<ConditionDto.Post> conditionList,
		TreatmentDto.Post treatmentList
	) {
		ReservationDetailDto.Post reservationDetailDto = new ReservationDetailDto.Post();
		reservationDetailDto.setShopId(shopId);
		reservationDetailDto.setRemove(remove);
		reservationDetailDto.setExtend(extend);
		reservationDetailDto.setStartTime(startTime);
		reservationDetailDto.setConditionList(conditionList);
		reservationDetailDto.setTreatment(treatmentList);
		return reservationDetailDto;
	}

	public static ConditionDto.Post createConditionPostDto(
		ConditionOption option
	) {
		ConditionDto.Post conditionDto = new ConditionDto.Post();
		conditionDto.setOption(option);
		return conditionDto;
	}

	public static TreatmentDto.Post createTreatmentPostDto(
		TreatmentOption option,
		Long imageId
	) {
		TreatmentDto.Post treatmentDto = new TreatmentDto.Post();
		treatmentDto.setOption(option);
		treatmentDto.setImageId(imageId);
		return treatmentDto;
	}

	public static ReservationDto.Patch createReservationPatchDto(
		ReservationStatus status,
		List<ReservationDetailDto.Patch> reservationDetailList
	) {
		ReservationDto.Patch reservationDto = new ReservationDto.Patch();
		reservationDto.setStatus(status);
		reservationDto.setReservationDetailDtoList(reservationDetailList);
		return reservationDto;
	}

	public static ReservationDetailDto.Patch createReservationDetailPatchDto(
		Long reservationDetailId,
		Long nailArtistId
	) {
		ReservationDetailDto.Patch reservationDetailDto = new ReservationDetailDto.Patch();
		reservationDetailDto.setReservationDetailId(reservationDetailId);
		reservationDetailDto.setNailArtistId(nailArtistId);
		return reservationDetailDto;
	}

	public static Reservation createReservation(
		Long reservationId,
		Long shopId,
		Long nailArtistId,
		ReservationDetail reservationDetail
	) {
		return Reservation.builder()
			.reservationId(reservationId)
			.shop(Shop.builder().shopId(shopId).availableSeats(FIXTURE_AVAILABLE_SEATS).build())
			.nailArtist(NailArtist.builder().nailArtistId(nailArtistId).build())
			.reservationDetail(reservationDetail)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static ReservationDetail createReservationDetail(
		Long reservationDetailId,
		Long shopId,
		Long nailArtistId,
		Treatment treatmentList,
		Set<Condition> conditionList,
		LocalDateTime startTime,
		LocalDateTime endTime,
		ReservationStatus status,
		RemoveOption remove,
		boolean extend
	) {
		return ReservationDetail.builder()
			.reservationDetailId(reservationDetailId)
			.shop(Shop.builder().shopId(shopId).availableSeats(FIXTURE_AVAILABLE_SEATS).build())
			.nailArtist(NailArtist.builder().nailArtistId(nailArtistId).build())
			.treatment(treatmentList)
			.conditionList(conditionList)
			.startTime(startTime)
			.endTime(endTime)
			.status(status)
			.remove(remove)
			.extend(extend)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static Treatment createTreatment(
		Long treatmentId,
		TreatmentOption option,
		Long imageId,
		String imageUrl
	) {
		return Treatment.builder()
			.treatmentId(treatmentId)
			.option(option)
			.imageId(imageId)
			.imageUrl(imageUrl)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static Condition createCondition(
		Long conditionId,
		ConditionOption option
	) {
		return Condition.builder()
			.conditionId(conditionId)
			.option(option)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public List<String> getBase64ImageFiles(int count) throws Exception {
		return fileFixture.createBase64ImageFiles(count);
	}
}
