package com.nailcase.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.QNailArtist;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.QShopLikedMember;
import com.nailcase.model.entity.QWorkHour;
import com.nailcase.model.entity.Shop;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShopQuerydslRepositoryImpl implements ShopQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Shop> searchShop(String keyword, Pageable pageable) {
		QShop shop = QShop.shop;

		List<Shop> shops = queryFactory.selectFrom(shop)
			.where(shop.shopName.contains(keyword)
				.or(shop.address.contains(keyword)))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(shop.count())
			.from(shop)
			.where(shop.shopName.contains(keyword)
				.or(shop.address.contains(keyword)));

		return PageableExecutionUtils.getPage(shops, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Shop> findTopShopsByPopularityCriteria(Pageable pageable) {
		QShop qShop = QShop.shop;
		List<Shop> shops = queryFactory.selectFrom(qShop)
			.orderBy(qShop.likes.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// null 체크 및 빈 리스트로 대체
		shops = (shops != null) ? shops : new ArrayList<>();

		JPAQuery<Long> countQuery = queryFactory
			.select(qShop.count())
			.from(qShop);

		return PageableExecutionUtils.getPage(shops, pageable, () -> {
			Long count = countQuery.fetchOne();
			return count != null ? count : 0L;
		});
	}

	@Override
	public Page<Shop> findLikedShopsByMember(Long memberId, Pageable pageable) {
		QShop qShop = QShop.shop;

		QShopLikedMember qShopLikedMember = QShopLikedMember.shopLikedMember;

		List<Shop> shops = queryFactory
			.select(qShopLikedMember.shop)
			.from(qShopLikedMember)
			.where(qShopLikedMember.member.memberId.eq(memberId))
			.join(qShopLikedMember.shop, qShop)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// shops가 null이면 빈 리스트로 초기화
		shops = (shops != null) ? shops : new ArrayList<>();

		long total = Optional.ofNullable(queryFactory
				.select(qShopLikedMember.count())
				.from(qShopLikedMember)
				.where(qShopLikedMember.member.memberId.eq(memberId))
				.fetchOne())
			.orElse(0L);

		return new PageImpl<>(shops, pageable, total);
	}

	@Override
	public Optional<Shop> findByShopIdAndNailArtistsAndWorkHours(Long shopId) {
		QShop shop = QShop.shop;
		QNailArtist nailArtist = QNailArtist.nailArtist;
		QWorkHour workHour = QWorkHour.workHour;

		Shop fetch = queryFactory.selectFrom(shop)
			.leftJoin(shop.nailArtists, nailArtist)
			.fetchJoin()
			.leftJoin(shop.workHours, workHour)
			.fetchJoin()
			.where(shop.shopId.eq(shopId))
			.fetchOne();

		return Optional.ofNullable(fetch);
	}
}
