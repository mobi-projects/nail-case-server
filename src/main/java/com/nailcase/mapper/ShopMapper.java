package com.nailcase.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.dto.WorkHourDto;
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
	@Mapping(target = "shopAvgRatings", ignore = true) // 이 부분을 추가
	@Mapping(target = "tags", expression = "java(ShopMapper.toTagNames(shop.getTags()))")
	@Mapping(target = "images", expression = "java(ShopMapper.toImageDtos(shop.getShopImages()))")
	ShopDto.Response toResponse(Shop shop);

	@Mapping(target = "id", source = "shopId")
	@Mapping(target = "name", source = "shopName")
	@Mapping(target = "overview", source = "overview")
	ShopDto.MainPageResponse toMainPageResponse(Shop shop);

	@Mapping(target = "shopName", source = "shopName")
	@Mapping(target = "phone", source = "phone")
	@Mapping(target = "address", source = "address")
	@Mapping(target = "shopImages", ignore = true)
	@Mapping(target = "workHours", ignore = true)
	Shop postDtoToShop(ShopDto.Post dto);

	default List<ShopImage> mapProfileImages(List<MultipartFile> files, String bucketName) {
		return files.stream()
			.map(file -> {
				ShopImage shopImage = ShopImage.builder()
					.bucketName(bucketName)
					.objectName(file.getOriginalFilename()) // 예시로 파일 이름을 사용
					.build();
				// 파일 저장 로직 필요 (예: AWS S3, Google Cloud Storage)
				return shopImage;
			})
			.collect(Collectors.toList());
	}

	default List<WorkHour> mapWorkHours(List<WorkHourDto> workHourDtos) {
		return workHourDtos.stream()
			.map(dto -> {
				WorkHour workHour = WorkHour.builder()
					.dayOfWeek(dto.getDayOfWeek())
					.isOpen(dto.getIsOpen())
					.openTime(DateUtils.unixTimeStampToLocalDateTime(dto.getOpenTime()))
					.closeTime(DateUtils.unixTimeStampToLocalDateTime(dto.getCloseTime()))
					.build();
				return workHour;
			})
			.collect(Collectors.toList());
	}
}