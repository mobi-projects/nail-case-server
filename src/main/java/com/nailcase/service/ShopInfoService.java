package com.nailcase.service;

import org.springframework.stereotype.Service;

import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.repository.ShopInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopInfoService {
	private final ShopInfoRepository shopInfoRepository;

	public void initShopInfo(Shop shop) {
		ShopInfo initShopInfo = ShopInfo.builder().shop(shop).build();
		shopInfoRepository.save(initShopInfo);
	}
}
