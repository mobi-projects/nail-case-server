package com.nailcase.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ValidationErrorCode;
import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.service.WorkHourService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/hours")
@RequiredArgsConstructor
public class WorkHourController {

	private final WorkHourService workHourService;

	@PutMapping
	public WorkHourDto updateWorkHour(
		@PathVariable Long shopId,
		@Valid @RequestBody WorkHourDto putRequest,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		if (putRequest.getIsOpen() == null && putRequest.getOpenTime() == null && putRequest.getCloseTime() == null) {
			throw new BusinessException(ValidationErrorCode.MISSING_REQUIRED_FIELD);
		}
		return workHourService.updateWorkHour(shopId, putRequest, memberDetails.getMemberId());
	}

	@GetMapping
	public List<WorkHourDto> getWorkHours(@PathVariable Long shopId) {
		return workHourService.getWorkHours(shopId);
	}
}
