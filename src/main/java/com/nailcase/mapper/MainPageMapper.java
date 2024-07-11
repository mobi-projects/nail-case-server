package com.nailcase.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nailcase.model.dto.MainPageDto;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.Shop;

@Mapper(componentModel = "spring", uses = {ReservationMapper.class, ShopMapper.class})
public interface MainPageMapper {

	@Mapping(source = "reservation", target = "recentReservation")
	@Mapping(source = "topShops", target = "topPopularShops")
	@Mapping(source = "likedShops", target = "likedShops")
	MainPageDto toMainPageResponse(Reservation reservation, List<Shop> topShops, List<Shop> likedShops);
}
