package com.nailcase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nailcase.model.dto.TreatmentDto;
import com.nailcase.model.entity.Treatment;
import com.nailcase.util.DateUtils;

@Mapper(
	imports = DateUtils.class,
	componentModel = "spring"
)
public interface TreatmentMapper {

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "modifiedBy", ignore = true)
	@Mapping(target = "treatmentId", ignore = true)
	Treatment toEntity(TreatmentDto.Post post);

	TreatmentDto.Response toResponse(Treatment treatment);
}
