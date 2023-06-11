package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    @Query("SELECT ")
    Optional<Long> findIdByAssetId(Long assetId);
}
