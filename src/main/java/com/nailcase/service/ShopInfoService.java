package com.nailcase.service;

import org.springframework.stereotype.Service;

import com.nailcase.repository.ShopInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopInfoService {
	private final ShopInfoRepository shopInfoRepository;

}
