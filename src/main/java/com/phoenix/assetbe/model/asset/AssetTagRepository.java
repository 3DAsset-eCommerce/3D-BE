package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetTagRepository extends JpaRepository<AssetTag, Long> {
    @Query("SELECT DISTINCT at.tag.tagName FROM AssetTag at WHERE at.asset.id = :assetId ORDER BY at.tag.tagName ASC")
    List<String> findTagNamesByAssetId(Long assetId);
}

