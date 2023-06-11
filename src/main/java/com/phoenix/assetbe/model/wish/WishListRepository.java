package com.phoenix.assetbe.model.wish;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    @Query("SELECT w.id FROM WishList w WHERE w.asset.id = :assetId AND w.user.id = :userId")
    Long findIdByAssetIdAndUserId(Long assetId, Long userId);
}
