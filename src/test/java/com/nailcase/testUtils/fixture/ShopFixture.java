package com.nailcase.testUtils.fixture;

import static org.jeasy.random.FieldPredicates.*;

import java.util.List;
import java.util.Set;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.model.entity.Tag;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.StringGenerateFixture;

public class ShopFixture {

	private static final Long ID = 1L;

	private static final int AVAILABLE_SEATS = 1;

	public Shop getShop() {
		return getShop(ID);
	}

	public Shop getShop(Long shopId) {
		NailArtist member = FixtureFactory.nailArtistFixture.getNailArtist();
		Set<WorkHour> workHours = FixtureFactory.workHourFixture.getWorkHours();
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("shopId"), () -> shopId)
			.randomize(named("member"), () -> member)
			.randomize(named("shopName"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.randomize(named("phone"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.randomize(named("overview"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(1000))
			.randomize(named("address"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(50))
			.randomize(named("availableSeats"), () -> AVAILABLE_SEATS)
			.randomize(named("workHours"), () -> workHours)
			.excludeField(named("shopInfo").and(ofType(ShopInfo.class)))
			.excludeField(named("tags").and(ofType(Set.class)))
			.excludeField(named("shopImages").and(ofType(Set.class)))
			.excludeField(named("nailArtistList").and(ofType(List.class)))
			.excludeField(named("reservationList").and(ofType(List.class)))
			.excludeField(named("reservationDetailList").and(ofType(List.class)));
		return new EasyRandom(params).nextObject(Shop.class);
	}

	public Tag getTag() {
		return getTag(ID);
	}

	public Tag getTag(Long tagId) {
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("tagId"), () -> tagId)
			.randomize(named("tagName"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(5));

		return new EasyRandom(params).nextObject(Tag.class);
	}
}
