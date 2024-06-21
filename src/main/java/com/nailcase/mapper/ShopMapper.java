package com.nailcase.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.TagMapping;
import com.nailcase.util.DateUtils;

@Mapper(
	componentModel = "spring",
	imports = {DateUtils.class},
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ShopMapper {

	ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

	@Mapping(
		target = "createdAt",
		expression = "java(DateUtils.localDateTimeToUnixTimeStamp(shop.getCreatedAt()))"
	)
	@Mapping(
		target = "modifiedAt",
		expression = "java(DateUtils.localDateTimeToUnixTimeStamp(shop.getModifiedAt()))"
	)
	@Mapping(target = "ownerId", source = "member.memberId")
	@Mapping(target = "tags", expression = "java(ShopMapper.toTagNames(shop.getTags()))")
	ShopDto.Response toResponse(Shop shop);

	static List<String> toTagNames(Set<TagMapping> tagMappings) {
		return tagMappings.stream()
			.sorted(Comparator.comparing(TagMapping::getSortOrder))
			.map(tagMapping -> tagMapping.getTag().getTagName())
			.collect(Collectors.toList());
	}
}
