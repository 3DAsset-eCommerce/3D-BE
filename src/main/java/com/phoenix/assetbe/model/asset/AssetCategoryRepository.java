package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {
    @Query("SELECT ac FROM AssetCategory ac WHERE ac.asset.id = :assetId")
    AssetCategory findAssetCategoryByAssetId(@Param("assetId") Long assetId);
}
