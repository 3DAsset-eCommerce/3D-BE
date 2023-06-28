package com.phoenix.assetbe.model.wish;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    @Query("SELECT w FROM WishList w WHERE w.user.id = :userId")
    List<WishList> findAllByUser(@Param("userId") Long userId);
}
