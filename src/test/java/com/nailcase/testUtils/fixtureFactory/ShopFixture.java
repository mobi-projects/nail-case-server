package com.nailcase.testUtils.fixtureFactory;

import static org.jeasy.random.FieldPredicates.*;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.StringGenerateFixture;

public class ShopFixture {

	private static final Long SHOP_ID = 1L;

	private static final int AVAILABLE_SEATS = 1;

	public Shop getShop() {
		Member member = FixtureFactory.memberFixture.getMember();
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("shopId"), () -> SHOP_ID)
			.randomize(named("member"), () -> member)
			.randomize(named("shopName"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.randomize(named("phone"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.randomize(named("overview"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(1000))
			.randomize(named("address"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(50))
			.randomize(named("availableSeats"), () -> AVAILABLE_SEATS);
		return new EasyRandom(params).nextObject(Shop.class);
	}
}
