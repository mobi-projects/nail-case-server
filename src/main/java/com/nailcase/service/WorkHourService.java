package com.nailcase.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.WorkHourErrorCode;
import com.nailcase.mapper.WorkHourMapper;
import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.repository.WorkHourRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkHourService {
	private final WorkHourMapper workHourMapper = WorkHourMapper.INSTANCE;
	private final WorkHourRepository workHourRepository;
	private final ShopService shopService;

	@Transactional
	public WorkHourDto.Put updateWorkHour(Long shopId, WorkHourDto.Put putRequest, Long memberId) {
		Shop shop = shopService.getShopById(shopId);

		// TODO 샵에 속해있는 아티스트 인지 권한 검사
		log.debug(String.valueOf(memberId));

		WorkHour workHour = workHourRepository.findByWorkHourIdAndShop(putRequest.getWorkHourId(), shop)
			.orElseThrow(() -> new BusinessException(WorkHourErrorCode.WORK_HOUR_NOT_FOUND));

		workHour.update(putRequest.getIsOpen(), putRequest.getOpenTime(), putRequest.getCloseTime());

		workHourRepository.save(workHour);

		return workHourMapper.toPutResponse(workHour);
	}
}
