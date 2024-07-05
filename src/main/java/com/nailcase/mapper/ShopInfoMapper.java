package com.nailcase.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.ShopInfo;

@Mapper(
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ShopInfoMapper {

	ShopInfoMapper INSTANCE = Mappers.getMapper(ShopInfoMapper.class);

	ShopInfoDto.Address toAddressResponse(String address, String point);

	ShopInfoDto.Info toInfoResponse(ShopInfo shopInfo);

	@Mapping(target = "imageUrl", ignore = true)
	ShopInfoDto.PriceResponse toPriceResponse(ShopInfo shopInfo);

	@BeforeMapping
	default void beforeMapping(ShopInfo shopInfo, @MappingTarget ShopInfoDto.PriceResponse priceResponse) {
		// TODO 이미지 처리
		priceResponse.setImageUrl("임시");
	}
}
