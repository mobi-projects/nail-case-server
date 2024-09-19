package com.nailcase.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
	uses = {ReservationDetailMapper.class, ShopMapper.class, TreatmentMapper.class},
	imports = {DateUtils.class, Shop.class, Member.class, Collections.class, LocalDateTime.class, Objects.class},
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ReservationMapper {

	@Mapping(target = "shop", expression = "java( Shop.builder().shopId(shopId).build() )")
	@Mapping(target = "customer", expression = "java( Member.builder().memberId(memberId).build() )")
	@Mapping(target = "reservationDetail", source = "dto", qualifiedByName = "dtoToReservationDetail")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "reservationId", ignore = true)
	Reservation toEntity(Long shopId, Long memberId, ReservationDto.Post dto);

	ReservationDto.Response toResponse(Reservation reservation);

	@Mapping(target = "remove", source = "reservationDetail.remove")
	@Mapping(target = "extend", source = "reservationDetail.extend")
	@Mapping(target = "status", source = "reservationDetail.status")
	@Mapping(target = "startTime", expression = "java( reservation.getReservationDetail().getStartTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getStartTime()) : null )")
	@Mapping(target = "endTime", expression = "java( reservation.getReservationDetail().getEndTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getEndTime()) : null )")
	@Mapping(target = "conditionList", source = "reservationDetail.conditionList")
	@Mapping(target = "treatment", source = "reservationDetail.treatment")
	ReservationDto.RegisterResponse toRegisterResponse(Reservation reservation);

	@Mapping(target = "reservationId", source = "reservationId")
	@Mapping(target = "shop", source = "shop", qualifiedByName = "mapShopInfo")
	@Mapping(target = "details", expression = "java(Collections.singletonList(mapReservationDetail(reservation.getReservationDetail())))")
	ReservationDto.MainPageResponse toMainPageResponse(Reservation reservation);

	@Mapping(target = "reservationId", source = "reservationId")
	@Mapping(target = "shop", source = "shop", qualifiedByName = "mapShopInfoForCompleted")
	@Mapping(target = "startTime", expression = "java(getStartTime(reservation.getReservationDetail()))")
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
			.map(ReservationDetail::getTreatment)
			.filter(Objects::nonNull)
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
		detailInfo.setStatus(reservationDetail.getStatus().name()); // Setting the status
		detailInfo.setEstimatedPrice(null); // Setting the estimated price
		return detailInfo;
	}

	@Named("mapReservationDetail")
	default ReservationDto.MainPageResponse.ReservationDetailInfo mapReservationDetail(ReservationDetail detail) {
		if (detail == null) {
			return null;
		}

		ReservationDto.MainPageResponse.ReservationDetailInfo info = new ReservationDto.MainPageResponse.ReservationDetailInfo();
		info.setReservationDetailsId(detail.getReservationDetailId());
		info.setStartTime(
			detail.getStartTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(detail.getStartTime()) : null);
		info.setEndTime(
			detail.getEndTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(detail.getEndTime()) : null);
		info.setTreatmentOptions(detail.getTreatment() != null
			? Collections.singletonList(detail.getTreatment().getOption().name())
			: new ArrayList<>());
		info.setRemoveOption(detail.getRemove() != null ? detail.getRemove().name() : null);
		info.setConditionOptions(detail.getConditionList() != null ? detail.getConditionList().stream()
			.map(condition -> condition.getOption().name())
			.collect(Collectors.toList()) : new ArrayList<>());
		info.setStatus(detail.getStatus() != null ? detail.getStatus().name() : null);

		return info.getStartTime() != null ? info : null;
	}

	@Named("getStartTime")
	default Long getStartTime(ReservationDetail detail) {
		return detail != null && detail.getStartTime() != null
			? DateUtils.localDateTimeToUnixTimeStamp(detail.getStartTime())
			: null;
	}
}