package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.WorkHour;

@Repository
public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {
}
