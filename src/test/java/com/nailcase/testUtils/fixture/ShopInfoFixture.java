package com.nailcase.testUtils.fixture;

import static org.jeasy.random.FieldPredicates.*;

import java.util.HashSet;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.nailcase.model.entity.PriceImage;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.StringGenerateFixture;

public class ShopInfoFixture {

	public ShopInfo getShopInfo() {
		Shop shop = FixtureFactory.shopFixture.getShop();
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("shopInfoId"), () -> 1L)
			.randomize(named("shop"), () -> shop)
			.randomize(named("shopId"), shop::getShopId)
			.randomize(named("point"), () -> "123")
			.randomize(named("priceImages"), HashSet::new)
			.randomize(named("parkingLotCnt"), () -> 1)
			.randomize(named("availableCnt"), () -> 2)
			.randomize(named("info"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.randomize(named("price"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.excludeField(named("priceImage").and(ofType(PriceImage.class)));

		return new EasyRandom(params).nextObject(ShopInfo.class);
	}
}
