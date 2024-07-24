package com.nailcase.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.PriceImage;
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

	@Mapping(target = "imageUrls", expression = "java(getPriceImageUrls(shopInfo))")
	ShopInfoDto.PriceResponse toPriceResponse(ShopInfo shopInfo);

	default List<String> getPriceImageUrls(ShopInfo shopInfo) {
		if (shopInfo.getPriceImages() != null) {
			return shopInfo.getPriceImages().stream()
				.map(this::generateImageUrl)
				.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	default String generateImageUrl(PriceImage priceImage) {
		return String.format("https://%s.s3.amazonaws.com/%s",
			priceImage.getBucketName(),
			priceImage.getObjectName());
	}

}
