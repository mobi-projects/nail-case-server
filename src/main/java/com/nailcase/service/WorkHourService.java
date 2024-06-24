package com.nailcase.service;

import org.springframework.stereotype.Service;

import com.nailcase.repository.WorkHourRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkHourService {

	private final WorkHourRepository workHourRepository;
}
