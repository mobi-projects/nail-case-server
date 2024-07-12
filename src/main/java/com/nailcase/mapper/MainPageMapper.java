package com.nailcase.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.nailcase.model.dto.MainPageDto;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.util.DateUtils;

@Mapper(
	componentModel = "spring",
	uses = {ReservationMapper.class, ShopMapper.class},
	imports = {DateUtils.class},
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface MainPageMapper {

	@Mapping(source = "reservation", target = "recentReservation", qualifiedByName = "mapReservationToMainPageResponse")
	@Mapping(source = "topShops", target = "topPopularShops", qualifiedByName = "mapShopsToMainPageResponses")
	@Mapping(source = "likedShops", target = "likedShops", qualifiedByName = "mapShopsToMainPageResponses")
	MainPageDto toMainPageResponse(Reservation reservation, List<Shop> topShops, List<Shop> likedShops);

	@Named("mapReservationToMainPageResponse")
	@Mapping(target = "reservationId", source = "reservationId")
	@Mapping(target = "shop.id", source = "shop.shopId")
	@Mapping(target = "shop.name", source = "shop.shopName")
	@Mapping(target = "startTime", source = "reservation", qualifiedByName = "getStartTime")
	@Mapping(target = "endTime", source = "reservation", qualifiedByName = "getEndTime")
	ReservationDto.MainPageResponse mapReservationToMainPageResponse(Reservation reservation);

	@Named("mapShopToMainPageResponse")
	@Mapping(target = "shopId", source = "shopId")
	@Mapping(target = "ownerId", source = "nailArtist.nailArtistId")
	@Mapping(target = "shopName", source = "shopName")
	@Mapping(target = "address", source = "address")
	@Mapping(target = "images", expression = "java(ShopMapper.toImageDtos(shop.getShopImages()))")
	ShopDto.MainPageResponse mapShopToMainPageResponse(Shop shop);

	@Named("mapShopsToMainPageResponses")
	default List<ShopDto.MainPageResponse> mapShopsToMainPageResponses(List<Shop> shops) {
		return shops.stream()
			.map(this::mapShopToMainPageResponse)
			.toList();
	}

	@Named("getStartTime")
	default LocalDateTime getStartTime(Reservation reservation) {
		return reservation.getReservationDetailList().stream()
			.findFirst()
			.map(ReservationDetail::getStartTime)
			.orElse(null);
	}

	@Named("getEndTime")
	default LocalDateTime getEndTime(Reservation reservation) {
		return reservation.getReservationDetailList().stream()
			.findFirst()
			.map(ReservationDetail::getEndTime)
			.orElse(null);
	}
}