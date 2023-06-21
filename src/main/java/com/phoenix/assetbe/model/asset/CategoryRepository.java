package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName")
    Optional<Category> findCategodyByCategoryName(@Param("categoryName") String categoryName);
}
