package com.nailcase.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ReservationErrorCode;
import com.nailcase.exception.codes.WorkHourErrorCode;
import com.nailcase.mapper.WorkHourMapper;
import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.repository.WorkHourRepository;
import com.nailcase.util.DateUtils;

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
	public WorkHourDto updateWorkHour(Long shopId, WorkHourDto putRequest, Long nailArtistId) {
		Shop shop = shopService.getShopById(shopId);

		// TODO 샵에 속해있는 아티스트 인지 권한 검사
		log.debug(String.valueOf(nailArtistId));

		WorkHour workHour = workHourRepository.findByWorkHourIdAndShop(putRequest.getWorkHourId(), shop)
			.orElseThrow(() -> new BusinessException(WorkHourErrorCode.WORK_HOUR_NOT_FOUND));

		workHour.update(putRequest.getIsOpen(), putRequest.getOpenTime(), putRequest.getCloseTime());

		workHourRepository.save(workHour);

		return workHourMapper.toResponse(workHour);
	}

	@Transactional(readOnly = true)
	public List<WorkHourDto> getWorkHours(Long shopId) {
		return shopService
			.getShopById(shopId)
			.getWorkHours()
			.stream()
			.sorted(Comparator.comparingInt(WorkHour::getDayOfWeek))
			.map(workHourMapper::toResponse)
			.collect(Collectors.toList());
	}

	public WorkHour retrieveWorkHourIfOpen(Collection<WorkHour> workHours, Long date) {
		int requestingDayOfWeek = DateUtils.unixTimeStampToLocalDateTime(date).getDayOfWeek().ordinal();

		WorkHour workHour = workHours.stream()
			.filter(wH -> wH.getDayOfWeek() == requestingDayOfWeek)
			.findFirst()
			.orElseThrow(() -> new BusinessException(ReservationErrorCode.WORK_HOUR_NOT_DEFINED));

		if (Boolean.FALSE.equals(workHour.getIsOpen())) {
			throw new BusinessException(ReservationErrorCode.WORK_HOUR_NOT_DEFINED);
		}

		return workHour;
	}
}
