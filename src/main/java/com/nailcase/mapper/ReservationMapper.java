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
		boolean isAccompanied = reservation.isAccompanied();
		System.out.println("Original details size: " + details.size());
		System.out.println("Is accompanied: " + isAccompanied);

		Set<ReservationDto.MainPageResponse.ReservationDetailInfo> mappedDetails = details.stream()
			.map(detail -> {
				ReservationDto.MainPageResponse.ReservationDetailInfo info = new ReservationDto.MainPageResponse.ReservationDetailInfo();
				info.setReservationDetailsId(detail.getReservationDetailId());
				info.setStartTime(DateUtils.localDateTimeToUnixTimeStamp(detail.getStartTime()));
				info.setEndTime(DateUtils.localDateTimeToUnixTimeStamp(detail.getEndTime()));
				info.setTreatmentOptions(detail.getTreatmentList().stream()
					.map(treatment -> treatment.getOption().name())
					.collect(Collectors.toList()));
				info.setRemoveOption(detail.getRemove().name());
				info.setConditionOptions(detail.getConditionList().stream()
					.map(condition -> condition.getOption().name())
					.collect(Collectors.toList()));
				info.setAccompanied(isAccompanied);
				info.setStatus(detail.getStatus().name());
				return info;
			})
			.collect(Collectors.toSet());

		System.out.println("Mapped details size: " + mappedDetails.size());
		return new ArrayList<>(mappedDetails);
	}

}