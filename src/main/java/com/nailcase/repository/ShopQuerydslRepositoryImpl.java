package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.QMemberLikedShop;
import com.nailcase.model.entity.QNailArtist;
import com.nailcase.model.entity.QShop;
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

		// 좋아요 수 기반의 총 매장 수를 계산하여 효율적으로 페이지네이션 처리
		JPAQuery<Long> countQuery = queryFactory
			.select(qShop.count())
			.from(qShop);

		return PageableExecutionUtils.getPage(shops, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Shop> findLikedShopsByMember(Member member, Pageable pageable) {
		QShop qShop = QShop.shop;
		QMemberLikedShop qMemberLikedShop = QMemberLikedShop.memberLikedShop;

		List<Shop> shops = queryFactory
			.select(qMemberLikedShop.shop)
			.from(qMemberLikedShop)
			.where(qMemberLikedShop.member.eq(member))
			.join(qMemberLikedShop.shop, qShop)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.selectFrom(qMemberLikedShop)
			.where(qMemberLikedShop.member.eq(member))
			.fetch().size();

		return new PageImpl<>(shops, pageable, total);
	}

	@Override
	public Page<Shop> findShopsByIds(List<Long> ids, Pageable pageable) {
		QShop qShop = QShop.shop;
		List<Shop> shops = queryFactory.selectFrom(qShop)
			.where(qShop.shopId.in(ids))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory.selectFrom(qShop)
			.where(qShop.shopId.in(ids))
			.fetch().size();

		return PageableExecutionUtils.getPage(shops, pageable, () -> total);
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
