package com.nailcase.testUtils.fixture;

import static org.jeasy.random.FieldPredicates.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;

public class WorkHourFixture {
	public WorkHour getWorkHour(Long id, int dayOfWeek) {
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("workHourId"), () -> id)
			.randomize(named("dayOfWeek"), () -> dayOfWeek)
			.excludeField(named("shop").and(ofType(Shop.class)));
		return new EasyRandom(params).nextObject(WorkHour.class);
	}

	public WorkHour getWorkHour() {
		return getWorkHour(0L, 0);
	}

	public Set<WorkHour> getWorkHours() {
		return IntStream
			.range(0, 7)
			.mapToObj(i -> getWorkHour(Long.parseLong(String.valueOf(i)), i))
			.collect(Collectors.toSet());
	}
}
