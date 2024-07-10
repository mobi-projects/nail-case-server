package com.nailcase.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.model.entity.TagMapping;
import com.nailcase.util.DateUtils;

@Mapper(
	componentModel = "spring",
	imports = {DateUtils.class},
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ShopMapper {

	ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

	static List<String> toTagNames(Set<TagMapping> tagMappings) {
		if (tagMappings == null || tagMappings.isEmpty()) {
			return null;
		}

		return tagMappings.stream()
			.map(tagMapping -> tagMapping.getTag().getTagName())
			.collect(Collectors.toList());
	}

	static List<ShopDto.Image> toImageDtos(Set<ShopImage> shopImages) {
		if (shopImages == null || shopImages.isEmpty()) {
			return null;
		}

		return shopImages.stream()
			.map(shopImage -> ShopDto.Image.builder()
				.imageId(shopImage.getImageId())
				.imageUrl(String.format("%s/%s", shopImage.getBucketName(), shopImage.getObjectName()))
				.build())
			.collect(Collectors.toList());
	}

	@Mapping(
		target = "createdAt",
		expression = "java(DateUtils.localDateTimeToUnixTimeStamp(shop.getCreatedAt()))"
	)
	@Mapping(
		target = "modifiedAt",
		expression = "java(DateUtils.localDateTimeToUnixTimeStamp(shop.getModifiedAt()))"
	)
	@Mapping(target = "ownerId", source = "nailArtist.nailArtistId")
	@Mapping(target = "tags", expression = "java(ShopMapper.toTagNames(shop.getTags()))")
	@Mapping(target = "images", expression = "java(ShopMapper.toImageDtos(shop.getShopImages()))")
	ShopDto.Response toResponse(Shop shop);
}