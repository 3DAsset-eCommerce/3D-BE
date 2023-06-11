package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAssetId(Long assetId);
}
