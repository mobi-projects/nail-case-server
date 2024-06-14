package com.nailcase.testUtils;

import java.time.LocalDateTime;
import java.util.List;

import com.nailcase.model.dto.ConditionDto;
import com.nailcase.model.dto.ReservationDetailDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.TreatmentDto;
import com.nailcase.model.entity.Condition;
import com.nailcase.model.entity.NailArtists;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shops;
import com.nailcase.model.entity.Treatment;
import com.nailcase.model.enums.ConditionOption;
import com.nailcase.model.enums.RemoveOption;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.model.enums.TreatmentOption;

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
public class FixtureFactory {
	public static ReservationDto.Post createReservationPostDto(
		Long shopId,
		List<ReservationDetailDto.Post> reservationDetailList
	) {
		ReservationDto.Post reservationDto = new ReservationDto.Post();
		reservationDto.setShopId(shopId);
		reservationDto.setReservationDetailList(reservationDetailList);
		return reservationDto;
	}

	public static ReservationDetailDto.Post createReservationDetailPostDto(
		Long shopId,
		RemoveOption remove,
		boolean extend,
		Long startTime,
		Long endTime,
		List<ConditionDto.Post> conditionList,
		List<TreatmentDto.Post> treatmentList
	) {
		ReservationDetailDto.Post reservationDetailDto = new ReservationDetailDto.Post();
		reservationDetailDto.setShopId(shopId);
		reservationDetailDto.setRemove(remove);
		reservationDetailDto.setExtend(extend);
		reservationDetailDto.setStartTime(startTime);
		reservationDetailDto.setEndTime(endTime);
		reservationDetailDto.setConditionList(conditionList);
		reservationDetailDto.setTreatmentList(treatmentList);
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
		Long imageId,
		String imageUrl
	) {
		TreatmentDto.Post treatmentDto = new TreatmentDto.Post();
		treatmentDto.setOption(option);
		treatmentDto.setImageId(imageId);
		treatmentDto.setImageUrl(imageUrl);
		return treatmentDto;
	}

	public static Reservation createReservation(
		Long reservationId,
		Long shopId,
		Long nailArtistId,
		List<ReservationDetail> reservationDetailList
	) {
		return Reservation.builder()
			.reservationId(reservationId)
			.shop(Shops.builder().shopId(shopId).build())
			.nailArtist(NailArtists.builder().nailArtistId(nailArtistId).build())
			.reservationDetailList(reservationDetailList)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
	}

	public static ReservationDetail createReservationDetail(
		Long reservationDetailId,
		Long shopId,
		Long nailArtistId,
		List<Treatment> treatmentList,
		List<Condition> conditionList,
		LocalDateTime startTime,
		LocalDateTime endTime,
		ReservationStatus status,
		RemoveOption remove,
		boolean extend
	) {
		return ReservationDetail.builder()
			.reservationDetailId(reservationDetailId)
			.shop(Shops.builder().shopId(shopId).build())
			.nailArtist(NailArtists.builder().nailArtistId(nailArtistId).build())
			.treatmentList(treatmentList)
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
}
