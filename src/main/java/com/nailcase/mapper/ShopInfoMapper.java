package com.nailcase.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.ShopInfo;

@Mapper(
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ShopInfoMapper {

	ShopInfoMapper INSTANCE = Mappers.getMapper(ShopInfoMapper.class);

	@Mapping(target = "imageUrl", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	ShopInfoDto.Response toResponse(ShopInfo shopInfo);

	ShopInfoDto.Address toAddressResponse(String address, String point);

	ShopInfoDto.Info toInfoResponse(ShopInfo shopInfo);

	ShopInfoDto.PriceResponse toPriceResponse(ShopInfo shopInfo);

}
