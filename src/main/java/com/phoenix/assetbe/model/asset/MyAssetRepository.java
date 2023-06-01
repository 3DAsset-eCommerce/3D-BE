package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyAssetRepository extends JpaRepository<MyAsset, Long> {
}
