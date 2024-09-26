package com.nailcase.mapper;

import java.time.LocalDateTime;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.entity.Shop;
import com.nailcase.util.DateUtils;

@Mapper(
	uses = {ConditionMapper.class, TreatmentMapper.class},
	imports = {DateUtils.class, Shop.class, NailArtist.class},
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ReservationDetailMapper {

	@Named("dtoToReservationDetail")
	@Mapping(target = "startTime", source = "startTime", qualifiedByName = "longToLocalDateTime")
	@Mapping(target = "shop", expression = "java( Shop.builder().shopId(dto.getShopId()).build() )")
	@Mapping(target = "treatment", source = "treatment")
	@Mapping(target = "reservationDetailId", ignore = true)
	@Mapping(target = "status", constant = "PENDING")
	@Mapping(target = "endTime", ignore = true)
	@Mapping(target = "review", ignore = true)
	ReservationDetail toEntity(ReservationDto.Post dto);

	@Named("longToLocalDateTime")
	default LocalDateTime longToLocalDateTime(Long timestamp) {
		return timestamp != null ? DateUtils.unixTimeStampToLocalDateTime(timestamp) : null;
	}
}