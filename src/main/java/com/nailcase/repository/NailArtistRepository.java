package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.NailArtist;

@Repository
public interface NailArtistRepository extends JpaRepository<NailArtist, Long>, NailArtistQuerydslRepository {
	Optional<NailArtist> findByEmail(String email);

	List<NailArtist> findByShop_ShopId(Long shopId);
}