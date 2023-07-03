package com.phoenix.assetbe.model.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    List<Cart> findAllByUser(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.asset.id = :assetId")
    void deleteByUserIdAndAssetId(@Param("userId") Long userId, @Param("assetId") Long assetId);

}
