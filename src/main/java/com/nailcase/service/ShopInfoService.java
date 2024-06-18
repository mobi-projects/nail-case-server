package com.nailcase.service;

import org.springframework.stereotype.Service;

import com.nailcase.model.entity.ShopInfo;
import com.nailcase.repository.ShopInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopInfoService {
	private final ShopInfoRepository shopInfoRepository;

	public void initShopInfo(Long shopId) {
		ShopInfo initShopInfo = ShopInfo.builder().shopId(shopId).build();
		shopInfoRepository.save(initShopInfo);
	}
}
