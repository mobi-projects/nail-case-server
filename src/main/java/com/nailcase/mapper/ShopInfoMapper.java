package com.nailcase.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.util.DateUtils;

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

	@Mapping(target = "imageUrl", ignore = true)
	ShopInfoDto.PriceResponse toPriceResponse(ShopInfo shopInfo);

	@BeforeMapping
	default void beforeMapping(ShopInfo shopInfo, @MappingTarget ShopInfoDto.Response response) {
		// TODO 이미지 처리
		response.setImageUrl("임시");

		response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(shopInfo.getCreatedAt()));
		response.setModifiedAt(DateUtils.localDateTimeToUnixTimeStamp(shopInfo.getModifiedAt()));
	}

	@BeforeMapping
	default void beforeMapping(ShopInfo shopInfo, @MappingTarget ShopInfoDto.PriceResponse priceResponse) {
		// TODO 이미지 처리
		priceResponse.setImageUrl("임시");
	}
}
