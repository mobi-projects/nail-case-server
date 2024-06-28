package com.nailcase.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.util.DateUtils;

@Mapper(
	componentModel = "spring",
	imports = {DateUtils.class},
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface WorkHourMapper {

	WorkHourMapper INSTANCE = Mappers.getMapper(WorkHourMapper.class);

	@Mapping(
		target = "openTime",
		expression = "java(DateUtils.localDateTimeToUnixTimeStamp(workHour.getOpenTime()))"
	)
	@Mapping(
		target = "closeTime",
		expression = "java(DateUtils.localDateTimeToUnixTimeStamp(workHour.getCloseTime()))"
	)
	WorkHourDto toResponse(WorkHour workHour);
}
