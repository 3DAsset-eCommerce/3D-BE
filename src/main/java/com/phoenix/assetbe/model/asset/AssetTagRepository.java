package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface AssetTagRepository extends JpaRepository<AssetTag, Long> {
    @Query("")
    List<AssetTag> findAssetTagByAssetId(Long assetId);
}

