package com.nailcase.testUtils.fixture;

import static org.jeasy.random.FieldPredicates.*;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.Tag;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.StringGenerateFixture;

public class ShopFixture {

	private static final Long ID = 1L;

	private static final int AVAILABLE_SEATS = 1;

	public Shop getShop() {
		return getShop(ID);
	}

	public Shop getShop(Long shopId) {
		Member member = FixtureFactory.memberFixture.getMember();
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("shopId"), () -> shopId)
			.randomize(named("member"), () -> member)
			.randomize(named("shopName"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.randomize(named("phone"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.randomize(named("overview"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(1000))
			.randomize(named("address"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(50))
			.randomize(named("availableSeats"), () -> AVAILABLE_SEATS);
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
