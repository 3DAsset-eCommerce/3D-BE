package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetSubCategoryRepository extends JpaRepository<AssetSubCategory, Long> {
    @Query("SELECT ac FROM AssetSubCategory ac WHERE ac.asset.id = :assetId")
    AssetSubCategory findAssetSubCategoryByAssetId(@Param("assetId") Long assetId);
}

