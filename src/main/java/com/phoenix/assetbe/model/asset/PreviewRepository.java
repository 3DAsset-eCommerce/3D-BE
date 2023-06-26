package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PreviewRepository extends JpaRepository<Preview, Long> {
    @Query("SELECT p FROM Preview p WHERE p.asset.id = :assetId")
    Optional<List<Preview>> findPreviewListByAssetId(@Param("assetId") Long assetId);
}
