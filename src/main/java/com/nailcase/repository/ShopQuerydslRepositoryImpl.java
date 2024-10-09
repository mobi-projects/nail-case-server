package com.nailcase.repository;

import static com.nailcase.model.entity.QReview.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.QNailArtist;
import com.nailcase.model.entity.QReservationDetail;
import com.nailcase.model.entity.QReview;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.QShopImage;
import com.nailcase.model.entity.QShopLikedMember;
import com.nailcase.model.entity.QWorkHour;
import com.nailcase.model.entity.Shop;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	@Override
	public double calculateShopReviewRating(Long shopId) {
		boolean exists = queryFactory
			.selectOne()
			.from(QReservationDetail.reservationDetail)
			.join(QReservationDetail.reservationDetail.review, QReview.review)
			.where(QReservationDetail.reservationDetail.shop.shopId.eq(shopId)
				.and(QReview.review.isNotNull()))
			.fetchFirst() != null;

		if (!exists) {
			return 0.0;  // 리뷰가 없으면 0 반환
		}

		return Optional.ofNullable(queryFactory
				.select(review.rating.avg())
				.from(review)
				.where(review.shop.shopId.eq(shopId))
				.fetchOne())
			.orElse(0.0);  // Optional을 사용하여 null 처리
	}

	@Override
	public Page<ShopDto.MainPageBeforeResponse> getTopPopularShops(Optional<Long> memberId, Pageable pageable) {
		QShop shop = QShop.shop;
		QShopImage shopImage = QShopImage.shopImage;
		QShopLikedMember shopLikedMember = QShopLikedMember.shopLikedMember;

		List<ShopDto.MainPageBeforeResponse> shopsRaw = queryFactory
			.select(Projections.constructor(ShopDto.MainPageBeforeResponse.class,
				shop.shopId,
				shop.shopName,
				shopImage.bucketName,
				shopImage.objectName,
				JPAExpressions
					.selectOne()
					.from(shopLikedMember)
					.where(shopLikedMember.shop.eq(shop)
						.and(shopLikedMember.member.memberId.eq(memberId.orElse(-1L))))
					.exists()))
			.from(shop)
			.leftJoin(shopImage).on(shopImage.shop.eq(shop))
			.orderBy(shop.likes.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(shop.count())
			.from(shop);

		return PageableExecutionUtils.getPage(shopsRaw, pageable, countQuery::fetchOne);
	}

	@Override
	public boolean isShopLikedByMember(Long shopId, Long memberId) {
		QShopLikedMember shopLikedMember = QShopLikedMember.shopLikedMember;

		Integer fetchOne = queryFactory
			.selectOne()
			.from(shopLikedMember)
			.where(shopLikedMember.shop.shopId.eq(shopId)
				.and(shopLikedMember.member.memberId.eq(memberId)))
			.fetchFirst();  // limit(1).fetchOne() 대신 fetchFirst() 사용

		return fetchOne != null;
	}

	@Override
	public Optional<Shop> findByShopIdWithNailArtist(Long shopId) {
		QShop shop = QShop.shop;
		QNailArtist nailArtist = QNailArtist.nailArtist;

		Shop fetch = queryFactory.selectFrom(shop)
			.leftJoin(shop.nailArtists, nailArtist)
			.fetchJoin()
			.where(shop.shopId.eq(shopId))
			.fetchOne();

		return Optional.ofNullable(fetch);
	}

}
