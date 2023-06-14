package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.ReviewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r.id, r.rating, r.content, r.user.id, r.user.firstName, r.user.lastName FROM Review r WHERE r.asset.id = :assetId ORDER BY r.createdAt, r.updatedAt DESC")
    List<ReviewResponse.ReviewsOutDTO.Reviews> findByAssetId(@Param("assetId") Long assetId);
}
