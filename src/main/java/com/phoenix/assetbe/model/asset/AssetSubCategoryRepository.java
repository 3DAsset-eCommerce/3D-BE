package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssetSubCategoryRepository extends JpaRepository<AssetSubCategory, Long> {
    @Query("SELECT ac FROM AssetCategory ac WHERE ac.asset.id = :assetId")
    AssetSubCategory findAssetSubCategoryByAssetId(Long assetId);
}

