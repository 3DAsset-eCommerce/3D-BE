package com.phoenix.assetbe.model.wish;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    @Query("SELECT w.id FROM WishList w WHERE w.asset.id = :assetId AND w.user.id = :userId")
    Long findIdByAssetIdAndUserId(@Param("assetId") Long assetId, @Param("userId") Long userId);
}
