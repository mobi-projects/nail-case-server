package com.nailcase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nailcase.model.dto.ConditionDto;
import com.nailcase.model.entity.Condition;
import com.nailcase.util.DateUtils;

@Mapper(
	imports = DateUtils.class,
	componentModel = "spring"
)
public interface ConditionMapper {

	@Mapping(target = "conditionId", ignore = true)
	@Mapping(target = "reservationDetail", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	Condition toEntity(ConditionDto.Post dto);
	
	ConditionDto.Response toResponse(Condition condition);
}
