package com.nailcase.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopInfoDto;

@Mapper(
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ShopInfoMapper {

	ShopInfoMapper INSTANCE = Mappers.getMapper(ShopInfoMapper.class);

	ShopInfoDto.Address toAddressResponse(String address, String point);
}
