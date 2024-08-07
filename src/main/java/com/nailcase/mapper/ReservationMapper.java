package com.nailcase.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.BeforeMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.util.DateUtils;

@Mapper(
	uses = {ReservationDetailMapper.class, ShopMapper.class},
	imports = {DateUtils.class, Shop.class, Member.class},
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ReservationMapper {

	@BeforeMapping
	default void beforeMapping(Long shopId, ReservationDto.Post dto) {
		dto.setShopId(shopId);
		dto.getReservationDetailList().forEach(detailDto -> detailDto.setShopId(shopId));
	}

	@Mapping(
		target = "shop",
		expression = "java( Shop.builder().shopId(dto.getShopId()).build() )"
	)
	@Mapping(
		target = "customer",
		expression = "java( Member.builder().memberId(memberId).build() )"
	)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "reservationId", ignore = true)
	@Mapping(target = "nailArtist", ignore = true)
	Reservation toEntity(Long shopId, Long memberId, ReservationDto.Post dto);

	@Mapping(
		target = "createdAt",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservation.getCreatedAt() ) )"
	)
	@Mapping(
		target = "modifiedAt",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservation.getModifiedAt() ) )"
	)
	@Mapping(target = "nickname", source = "customer.nickname")
	ReservationDto.Response toResponse(Reservation reservation);

	@Mapping(target = "reservationId", source = "reservationId")
	@Mapping(target = "shop", source = "shop", qualifiedByName = "mapShopInfo")
	@Mapping(target = "details", expression = "java(mapReservationDetails(reservation.getReservationDetailList(), reservation))")
	ReservationDto.MainPageResponse toMainPageResponse(Reservation reservation);

	@Mapping(target = "reservationId", source = "reservationId")
	@Mapping(target = "shop", source = "shop", qualifiedByName = "mapShopInfoForCompleted")
	@Mapping(target = "startTime", expression = "java(getEarliestStartTime(reservation.getReservationDetailList()))")
	ReservationDto.CompletedReservationResponse toCompletedReservationResponse(Reservation reservation);

	@Named("mapShopInfo")
	default ReservationDto.MainPageResponse.ShopInfo mapShopInfo(Shop shop) {
		ReservationDto.MainPageResponse.ShopInfo shopInfo = new ReservationDto.MainPageResponse.ShopInfo();
		shopInfo.setId(shop.getShopId());
		shopInfo.setName(shop.getShopName());
		return shopInfo;
	}

	@Named("mapShopInfoForCompleted")
	default ReservationDto.CompletedReservationResponse.ShopInfo mapShopInfoForCompleted(Shop shop) {
		ReservationDto.CompletedReservationResponse.ShopInfo shopInfo = new ReservationDto.CompletedReservationResponse.ShopInfo();
		shopInfo.setId(shop.getShopId());
		shopInfo.setName(shop.getShopName());
		shopInfo.setImage(getFirstShopImage(shop));
		return shopInfo;
	}

	default String getFirstShopImage(Shop shop) {
		return null;
	}

	default Long getEarliestStartTime(Set<ReservationDetail> details) {
		return details.stream()
			.map(ReservationDetail::getStartTime)
			.min(java.time.LocalDateTime::compareTo)
			.map(DateUtils::localDateTimeToUnixTimeStamp)
			.orElse(null);
	}

	default Long getLatestEndTime(List<ReservationDetail> details) {
		return details.stream()
			.map(ReservationDetail::getEndTime)
			.max(java.time.LocalDateTime::compareTo)
			.map(DateUtils::localDateTimeToUnixTimeStamp)
			.orElse(null);
	}

	default List<String> getTreatmentOptions(List<ReservationDetail> details) {
		return details.stream()
			.flatMap(detail -> detail.getTreatmentList().stream())
			.map(treatment -> treatment.getOption().name())
			.distinct()
			.collect(Collectors.toList());
	}

	default String getRemoveOption(List<ReservationDetail> details) {
		return details.isEmpty() ? null : details.get(0).getRemove().name();
	}

	default List<String> getConditionOptions(List<ReservationDetail> details) {
		return details.stream()
			.flatMap(detail -> detail.getConditionList().stream())
			.map(condition -> condition.getOption().name())
			.distinct()
			.collect(Collectors.toList());
	}

	@Named("mapShopInfoForCompletedReservation")
	default ReservationDto.CompletedReservationResponse.ShopInfo mapShopInfoForCompletedReservation(Shop shop) {
		ReservationDto.CompletedReservationResponse.ShopInfo shopInfo = new ReservationDto.CompletedReservationResponse.ShopInfo();
		shopInfo.setId(shop.getShopId());
		shopInfo.setName(shop.getShopName());
		return shopInfo;
	}

	default List<ReservationDto.MainPageResponse.ReservationDetailInfo> toReservationDetailInfoList(
		List<ReservationDetail> reservationDetails) {
		return reservationDetails.stream()
			.map(this::toReservationDetailInfo)
			.collect(Collectors.toList());
	}

	default ReservationDto.MainPageResponse.ReservationDetailInfo toReservationDetailInfo(
		ReservationDetail reservationDetail) {
		ReservationDto.MainPageResponse.ReservationDetailInfo detailInfo = new ReservationDto.MainPageResponse.ReservationDetailInfo();
		detailInfo.setStartTime(DateUtils.localDateTimeToUnixTimeStamp(reservationDetail.getStartTime()));
		detailInfo.setEndTime(DateUtils.localDateTimeToUnixTimeStamp(reservationDetail.getEndTime()));
		detailInfo.setTreatmentOptions(getTreatmentOptions(List.of(reservationDetail)));
		detailInfo.setRemoveOption(getRemoveOption(List.of(reservationDetail)));
		detailInfo.setConditionOptions(getConditionOptions(List.of(reservationDetail)));
		detailInfo.setAccompanied(reservationDetail.getReservation().isAccompanied());
		detailInfo.setStatus(reservationDetail.getStatus().name()); // Setting the status
		return detailInfo;
	}

	@Named("mapReservationDetails")
	default List<ReservationDto.MainPageResponse.ReservationDetailInfo> mapReservationDetails(
		Set<ReservationDetail> details, Reservation reservation) {
		if (details == null || details.isEmpty()) {
			return new ArrayList<>();
		}
		boolean isAccompanied = reservation.isAccompanied();

		return details.stream()
			.map(detail -> {
				ReservationDto.MainPageResponse.ReservationDetailInfo info = new ReservationDto.MainPageResponse.ReservationDetailInfo();
				info.setReservationDetailsId(detail.getReservationDetailId());
				info.setStartTime(
					detail.getStartTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(detail.getStartTime()) :
						null);
				info.setEndTime(
					detail.getEndTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(detail.getEndTime()) : null);
				info.setTreatmentOptions(detail.getTreatmentList() != null ? detail.getTreatmentList().stream()
					.map(treatment -> treatment.getOption().name())
					.collect(Collectors.toList()) : new ArrayList<>());
				info.setRemoveOption(detail.getRemove() != null ? detail.getRemove().name() : null);
				info.setConditionOptions(detail.getConditionList() != null ? detail.getConditionList().stream()
					.map(condition -> condition.getOption().name())
					.collect(Collectors.toList()) : new ArrayList<>());
				info.setAccompanied(isAccompanied);
				info.setStatus(detail.getStatus() != null ? detail.getStatus().name() : null);
				return info;
			})
			.filter(info -> info.getStartTime() != null) // endTime은 null이어도 괜찮습니다.
			.collect(Collectors.toList());
	}

}