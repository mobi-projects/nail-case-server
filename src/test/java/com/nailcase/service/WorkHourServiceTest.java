package com.nailcase.service;

import static org.assertj.core.api.Assertions.*;
import static org.hibernate.validator.internal.util.Contracts.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.repository.WorkHourRepository;
import com.nailcase.testUtils.Reflection;
import com.nailcase.testUtils.fixture.ShopFixture;
import com.nailcase.testUtils.fixture.WorkHourFixture;
import com.nailcase.util.DateUtils;

@ExtendWith(MockitoExtension.class)
class WorkHourServiceTest {
	private final ShopFixture shopFixture = new ShopFixture();
	private final WorkHourFixture workHourFixture = new WorkHourFixture();
	@Mock
	private WorkHourRepository workHourRepository;
	@Mock
	private ShopService shopService;
	@InjectMocks
	private WorkHourService workHourService;

	@Test
	@DisplayName("updateWorkHour 성공 테스트")
	void updateWorkHourSuccess() throws Exception {
		// Given
		Shop shop = shopFixture.getShop();
		Long memberId = shop.getMember().getMemberId();
		WorkHour newWorkHour = workHourFixture.getWorkHour();
		WorkHourDto.Put putRequest = (WorkHourDto.Put)Reflection.createInstance(WorkHourDto.Put.class);
		putRequest.setWorkHourId(newWorkHour.getWorkHourId());
		putRequest.setIsOpen(newWorkHour.getIsOpen());
		putRequest.setOpenTime(DateUtils.localDateTimeToUnixTimeStamp(newWorkHour.getOpenTime()));
		putRequest.setCloseTime(DateUtils.localDateTimeToUnixTimeStamp(newWorkHour.getCloseTime()));

		when(shopService.getShopById(shop.getShopId())).thenReturn(shop);
		when(workHourRepository.findByWorkHourIdAndShop(putRequest.getWorkHourId(), shop)).thenReturn(
			Optional.of(newWorkHour));

		// When
		WorkHourDto.Put result = workHourService.updateWorkHour(shop.getShopId(), putRequest, memberId);

		// Then
		assertNotNull(result);
		assertThat(result).extracting("workHourId", "isOpen", "openTime", "closeTime")
			.containsExactly(putRequest.getWorkHourId(), putRequest.getIsOpen(), putRequest.getOpenTime(),
				putRequest.getCloseTime());

		verify(shopService, times(1)).getShopById(shop.getShopId());
		verify(workHourRepository, times(1)).findByWorkHourIdAndShop(putRequest.getWorkHourId(), shop);
		verify(workHourRepository, times(1)).save(newWorkHour);
	}
}