package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @Query("SELECT s FROM SubCategory s WHERE s.subCategoryName = :subCategoryName")
    Optional<SubCategory> findSubCategoryBySubCategoryName(@Param("subCategoryName") String subCategoryName);
}

