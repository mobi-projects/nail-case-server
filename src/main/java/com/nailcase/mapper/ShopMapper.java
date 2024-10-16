package com.nailcase.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.model.entity.PriceImage;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.model.entity.TagMapping;
import com.nailcase.model.entity.WorkHour;
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

	static List<ShopDto.Image> toShopImageDtos(List<ShopImage> shopImages) {
		if (shopImages == null || shopImages.isEmpty()) {
			return new ArrayList<>(); // 빈 리스트 반환
		}

		return shopImages.stream()
			.map(shopImage -> new ShopDto.Image(
				shopImage.getImageId(),
				generateImageUrl(shopImage.getBucketName(), shopImage.getObjectName())
			))
			.collect(Collectors.toList());
	}

	static List<ShopDto.Image> toPriceImageDtos(List<PriceImage> priceImages) {
		if (priceImages == null || priceImages.isEmpty()) {
			return new ArrayList<>(); // 빈 리스트 반환
		}

		return priceImages.stream()
			.map(priceImage -> new ShopDto.Image(
				priceImage.getImageId(),
				generateImageUrl(priceImage.getBucketName(), priceImage.getObjectName())
			))
			.collect(Collectors.toList());
	}

	@Mapping(target = "profileImages", expression = "java(ShopMapper.toShopImageDtos(shop.getShopImages()))")
	@Mapping(target = "priceImages", expression = "java(ShopMapper.toPriceImageDtos(shop.getPriceImages()))")
	@Mapping(target = "workHours", expression = "java(mapWorkHours(shop.getWorkHours()))")
	ShopDto.Response toResponse(Shop shop);

	@Mapping(target = "profileImages", expression = "java(ShopMapper.toShopImageDtos(shop.getShopImages()))")
	@Mapping(target = "priceImages", expression = "java(ShopMapper.toPriceImageDtos(shop.getPriceImages()))")
	@Mapping(target = "workHours", expression = "java(mapWorkHours(shop.getWorkHours()))")
	@Mapping(target = "likedByUser", source = "isLiked")
	ShopDto.Response toResponseWithLiked(Shop shop, boolean isLiked);

	@Mapping(target = "shopName", source = "shopName")
	@Mapping(target = "phone", source = "phone")
	@Mapping(target = "address", source = "address")
	@Mapping(target = "shopImages", ignore = true)
	@Mapping(target = "priceImages", ignore = true)
	@Mapping(target = "workHours", ignore = true)
	Shop postDtoToShop(ShopDto.Post dto);

	default List<WorkHour> mapWorkHourDtoToWorkHour(List<WorkHourDto.Post> workHourDtos) {
		if (workHourDtos == null) {
			return new ArrayList<>();
		}
		return workHourDtos.stream()
			.map(dto -> WorkHour.builder()
				.dayOfWeek(dto.getDayOfWeek())
				.isOpen(dto.getIsOpen())
				.openTime(DateUtils.unixTimeStampToLocalDateTime(dto.getOpenTime()))
				.closeTime(DateUtils.unixTimeStampToLocalDateTime(dto.getCloseTime()))
				.build())
			.collect(Collectors.toList());
	}

	default List<WorkHourDto.Post> mapWorkHours(List<WorkHour> workHours) {
		return workHours.stream()
			.map(workHourData -> WorkHourDto.Post.builder()
				.dayOfWeek(workHourData.getDayOfWeek())
				.isOpen(workHourData.getIsOpen())
				.openTime(DateUtils.localDateTimeToUnixTimeStamp(workHourData.getOpenTime()))
				.closeTime(DateUtils.localDateTimeToUnixTimeStamp(workHourData.getCloseTime()))
				.build())
			.collect(Collectors.toList());
	}

	private static String generateImageUrl(String bucket, String objectName) {
		return "https://" + bucket + ".s3.amazonaws.com/" + objectName;
	}

	@Mapping(target = "shopName", source = "requestData.shopName")
	@Mapping(target = "phone", source = "requestData.phone")
	@Mapping(target = "address", source = "requestData.address")
	@Mapping(target = "shopImages", ignore = true)
	@Mapping(target = "priceImages", ignore = true)
	@Mapping(target = "workHours", expression = "java(mapWorkHourDtoToWorkHour(postResponse.getRequestData().getWorkHours()))")
	Shop postResponseToShop(ShopDto.PostResponse postResponse);

}