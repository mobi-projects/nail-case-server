package com.nailcase.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nailcase.model.dto.ReservationDetailDto;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.util.DateUtils;

@Mapper(
	uses = {ConditionMapper.class, TreatmentMapper.class},
	imports = {DateUtils.class, Shop.class},
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ReservationDetailMapper {

	@Mapping(
		target = "shop",
		expression = "java( Shop.builder().shopId(dto.getShopId()).build() )"
	)

	@Mapping(
		target = "startTime",
		expression = "java( DateUtils.unixTimeStampToLocalDateTime( dto.getStartTime() ) )"
	)
	@Mapping(
		target = "endTime",
		expression = "java( DateUtils.unixTimeStampToLocalDateTime( dto.getEndTime() ) )"
	)
	@Mapping(target = "reservationDetailId", ignore = true)
	@Mapping(target = "nailArtist", ignore = true)
	@Mapping(target = "reservation", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	ReservationDetail toEntity(ReservationDetailDto.Post dto);

	@Mapping(
		target = "startTime",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservationDetail.getStartTime() ) )"
	)
	@Mapping(
		target = "endTime",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservationDetail.getEndTime() ) )"
	)
	@Mapping(
		target = "createdAt",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservationDetail.getCreatedAt() ) )"
	)
	@Mapping(
		target = "modifiedAt",
		expression = "java( DateUtils.localDateTimeToUnixTimeStamp( reservationDetail.getModifiedAt() ) )"
	)
	@Mapping(target = "nailArtistId", source = "nailArtist.nailArtistId")
	ReservationDetailDto.Response toResponse(ReservationDetail reservationDetail);
}
