package com.nailcase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.model.entity.WorkHour;

@Mapper(componentModel = "spring")
public interface WorkHourMapper {

	WorkHourMapper INSTANCE = Mappers.getMapper(WorkHourMapper.class);

	WorkHourDto.Put toPutResponse(WorkHour workHour);
}
