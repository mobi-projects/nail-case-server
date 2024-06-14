package com.nailcase.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.Shops;
import com.nailcase.util.DateUtils;

@Mapper(
	uses = ReservationDetailMapper.class,
	imports = {DateUtils.class, Shops.class},
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
		expression = "java( Shops.builder().shopId(dto.getShopId()).build() )"
	)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "reservationId", ignore = true)
	@Mapping(target = "nailArtist", ignore = true)
	Reservation toEntity(Long shopId, ReservationDto.Post dto);

	@Mapping(
		target = "createdAt",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservation.getCreatedAt() ) )"
	)
	@Mapping(
		target = "modifiedAt",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservation.getModifiedAt() ) )"
	)
	ReservationDto.Response toResponse(Reservation reservation);
}
