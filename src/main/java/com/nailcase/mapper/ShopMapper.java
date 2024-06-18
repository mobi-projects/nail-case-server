package com.nailcase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Shop;

@Mapper(componentModel = "spring")
public interface ShopMapper {

	ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

	Shop toEntity(ShopDto.Post shopRegisterRequest);

	ShopDto.Response toResponse(Shop shop);
}
