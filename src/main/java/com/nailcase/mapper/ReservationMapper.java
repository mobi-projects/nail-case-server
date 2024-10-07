package com.nailcase.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.nailcase.model.dto.ConditionDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Condition;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.Treatment;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.util.DateUtils;
import com.nailcase.util.StringUtils;

@Mapper(
	uses = {ReservationDetailMapper.class, ShopMapper.class, TreatmentMapper.class},
	imports = {DateUtils.class, Shop.class, Member.class, Collections.class, LocalDateTime.class, Objects.class},
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ReservationMapper {

	@Mapping(target = "shop", expression = "java( Shop.builder().shopId(shopId).build() )")
	@Mapping(target = "customer", expression = "java( Member.builder().memberId(memberId).build() )")
	@Mapping(target = "reservationDetail", expression = "java( dtoToReservationDetail(dto, monthlyArtImage) )")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "reservationId", ignore = true)
	@Mapping(target = "customerName", source = "nickname")
	@Mapping(target = "nailArtist", ignore = true)
	Reservation toEntity(Long shopId, Long memberId, String nickname, ReservationDto.Post dto,
		MonthlyArtImage monthlyArtImage);

	@Mapping(target = "remove", source = "reservationDetail.remove")
	@Mapping(target = "extend", source = "reservationDetail.extend")
	@Mapping(target = "status", source = "reservationDetail.status")
	@Mapping(target = "startTime", expression = "java( reservation.getReservationDetail().getStartTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getStartTime()) : null )")
	@Mapping(target = "endTime", expression = "java( reservation.getReservationDetail().getEndTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getEndTime()) : null )")
	@Mapping(target = "price", source = "reservationDetail.price")
	@Mapping(target = "customerName", source = "customerName")
	@Mapping(target = "conditionList", expression = "java(mapConditionList(reservation.getReservationDetail().getConditionList()))")
	@Mapping(target = "treatment", source = "reservationDetail.treatment")
	@Mapping(target = "rejectReason", source = "rejectReason")
	ReservationDto.Response toResponse(Reservation reservation);

	@Mapping(target = "reservationId", source = "reservationId")
	@Mapping(target = "remove", source = "reservationDetail.remove")
	@Mapping(target = "extend", source = "reservationDetail.extend")
	@Mapping(target = "status", source = "reservationDetail.status")
	@Mapping(target = "startTime", expression = "java( reservation.getReservationDetail().getStartTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getStartTime()) : null )")
	@Mapping(target = "endTime", expression = "java( reservation.getReservationDetail().getEndTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getEndTime()) : null )")
	@Mapping(target = "price", source = "reservationDetail.price")
	@Mapping(target = "customerName", source = "customerName")
	@Mapping(target = "conditionList", expression = "java(mapConditionList(reservation.getReservationDetail().getConditionList()))")
	@Mapping(target = "treatment", source = "reservationDetail.treatment")
	@Mapping(target = "workHourInfo", expression = "java(mapWorkHourInfo(reservation))")
	@Mapping(target = "rejectReason", source = "rejectReason")
	ReservationDto.viewResponse toDetailedResponse(Reservation reservation);

	@Mapping(target = "remove", source = "reservationDetail.remove")
	@Mapping(target = "extend", source = "reservationDetail.extend")
	@Mapping(target = "status", source = "reservationDetail.status")
	@Mapping(target = "startTime", expression = "java( reservation.getReservationDetail().getStartTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getStartTime()) : null )")
	@Mapping(target = "endTime", expression = "java( reservation.getReservationDetail().getEndTime() != null ? DateUtils.localDateTimeToUnixTimeStamp(reservation.getReservationDetail().getEndTime()) : null )")
	@Mapping(target = "conditionList", expression = "java(mapConditionListToPageResponse(reservation.getReservationDetail().getConditionList()))")
	@Mapping(target = "treatment", source = "reservationDetail.treatment")
	ReservationDto.RegisterResponse toRegisterResponse(Reservation reservation);

	default List<ConditionDto.pageResponse> mapConditionListToPageResponse(List<Condition> conditions) {
		if (conditions == null) {
			return null;
		}
		return conditions.stream()
			.map(this::toConditionPageResponse)
			.collect(Collectors.toList());
	}

	ConditionDto.pageResponse toConditionPageResponse(Condition condition);

	default List<ConditionDto.Response> mapConditionList(List<Condition> conditions) {
		if (conditions == null) {
			return null;
		}
		return conditions.stream()
			.map(this::toConditionResponse)
			.collect(Collectors.toList());
	}

	ConditionDto.Response toConditionResponse(Condition condition);

	@Mapping(target = "reservationId", source = "reservationId")
	@Mapping(target = "shop", source = "shop", qualifiedByName = "mapShopInfo")
	@Mapping(target = "details", expression = "java(Collections.singletonList(mapReservationDetail(reservation.getReservationDetail())))")
	ReservationDto.MainPageResponse toMainPageResponse(Reservation reservation);

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

	default ReservationDto.pageableResponse toPageableResponse(Page<Reservation> reservationPage) {
		ReservationDto.pageableResponse response = new ReservationDto.pageableResponse();
		response.setReservationList(reservationPage.getContent().stream()
			.map(this::toRegisterResponse)
			.collect(Collectors.toList()));
		response.setPageNumber(reservationPage.getNumber());
		response.setPageSize(reservationPage.getSize());
		response.setTotalElements(reservationPage.getTotalElements());
		response.setTotalPages(reservationPage.getTotalPages());
		response.setLast(reservationPage.isLast());
		return response;
	}

	@Named("dtoToReservationDetail")
	default ReservationDetail dtoToReservationDetail(ReservationDto.Post dto, MonthlyArtImage monthlyArtImage) {
		if (dto == null) {
			return null;
		}

		Treatment treatment = Treatment.builder()
			.option(dto.getTreatment().getOption())
			.build();

		if (monthlyArtImage != null) {
			String imageUrl = StringUtils.generateImageUrl(monthlyArtImage.getBucketName(),
				monthlyArtImage.getObjectName());
			treatment.updateImageId(monthlyArtImage.getImageId());
			treatment.updateImageUrl(imageUrl);
		}

		return ReservationDetail.builder()
			.startTime(DateUtils.unixTimeStampToLocalDateTime(dto.getStartTime()))
			.status(ReservationStatus.PENDING)
			.remove(dto.getRemove())
			.extend(dto.getExtend())
			.treatment(treatment)
			.conditionList(dto.getConditionList().stream()
				.map(conditionDto -> Condition.builder()
					.option(conditionDto.getOption())
					.build())
				.collect(Collectors.toList()))
			.build();
	}

	@Named("mapWorkHourInfo")
	default ReservationDto.viewResponse.WorkHourInfo mapWorkHourInfo(Reservation reservation) {
		if (reservation == null || reservation.getShop() == null ||
			reservation.getShop().getWorkHours() == null ||
			reservation.getReservationDetail() == null ||
			reservation.getReservationDetail().getStartTime() == null) {
			return null;
		}

		List<WorkHour> workHours = reservation.getShop().getWorkHours();
		LocalDateTime reservationTime = reservation.getReservationDetail().getStartTime();

		int dayOfWeek = reservationTime.getDayOfWeek().getValue() % 7; // 0 (Sunday) to 6 (Saturday)

		WorkHour workHour = workHours.stream()
			.filter(wh -> wh.getDayOfWeek() == dayOfWeek)
			.findFirst()
			.orElse(null);

		if (workHour == null) {
			return null;
		}

		ReservationDto.viewResponse.WorkHourInfo workHourInfo = new ReservationDto.viewResponse.WorkHourInfo();
		workHourInfo.setIsOpen(workHour.getIsOpen());
		workHourInfo.setOpenTime(DateUtils.localDateTimeToUnixTimeStamp(workHour.getOpenTime()));
		workHourInfo.setCloseTime(DateUtils.localDateTimeToUnixTimeStamp(workHour.getCloseTime()));

		return workHourInfo;
	}

}