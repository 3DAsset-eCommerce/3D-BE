package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.AssetResponse;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<AssetResponse.CountOutDTO.CountCategory> countByCategory();

    List<AssetResponse.CountOutDTO.CountSubCategory> countBySubCategory();
}
