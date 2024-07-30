package com.nailcase.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.util.DateUtils;

@Mapper(componentModel = "spring", imports = {DateUtils.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MainPageMapper {
	@Mapping(target = "id", source = "shopId")
	@Mapping(target = "name", source = "shopName")
	@Mapping(target = "overview", source = "overview")
	ShopDto.MainPageResponse mapShopToMainPageResponse(Shop shop);
}