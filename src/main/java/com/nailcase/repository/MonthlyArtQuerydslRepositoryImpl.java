package com.nailcase.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.MonthlyArt;
import com.nailcase.model.entity.QMonthlyArt;
import com.nailcase.model.entity.QMonthlyArtImage;
import com.nailcase.model.entity.QShop;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MonthlyArtQuerydslRepositoryImpl implements MonthlyArtQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<MonthlyArt> findLatestByShop_ShopId(Long shopId) {
		QMonthlyArt monthlyArt = QMonthlyArt.monthlyArt;
		QShop shop = QShop.shop;
		QMonthlyArtImage monthlyArtImage = QMonthlyArtImage.monthlyArtImage;

		return Optional.ofNullable(queryFactory
			.selectFrom(monthlyArt)
			.leftJoin(monthlyArt.shop, shop).fetchJoin()
			.leftJoin(monthlyArt.monthlyArtImages, monthlyArtImage).fetchJoin()
			.where(monthlyArt.shop.shopId.eq(shopId))
			.orderBy(monthlyArt.createdAt.desc())
			.fetchFirst());
	}

}
