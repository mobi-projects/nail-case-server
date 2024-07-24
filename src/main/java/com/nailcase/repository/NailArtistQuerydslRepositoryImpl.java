package com.nailcase.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.QNailArtist;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.enums.SocialType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NailArtistQuerydslRepositoryImpl implements NailArtistQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<NailArtist> findBySocialTypeAndSocialIdWithShops(SocialType socialType, String socialId) {
		QNailArtist nailArtist = QNailArtist.nailArtist;
		QShop shop = QShop.shop;

		return Optional.ofNullable(queryFactory
			.selectFrom(nailArtist)
			.leftJoin(nailArtist.shops, shop).fetchJoin()
			.where(nailArtist.socialType.eq(socialType)
				.and(nailArtist.socialId.eq(socialId)))
			.fetchOne());
	}

	@Override
	public Optional<NailArtist> findByIdWithShop(Long id) {
		QNailArtist nailArtist = QNailArtist.nailArtist;
		QShop shop = QShop.shop;

		return Optional.ofNullable(queryFactory
			.selectFrom(nailArtist)
			.leftJoin(nailArtist.shop, shop).fetchJoin()
			.where(nailArtist.nailArtistId.eq(id))
			.fetchOne());
	}
}
