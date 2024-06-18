package com.nailcase.service;

import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopHour;
import com.nailcase.repository.ShopHourRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopHourService {
	private final ShopHourRepository shopHourRepository;

	public void initShopHour(Shop shop) {
		IntStream.range(0, 7)
			.mapToObj(i -> ShopHour.builder().shop(shop).dayOfWeek(i).build())
			.forEach(shopHourRepository::save);
	}
}
