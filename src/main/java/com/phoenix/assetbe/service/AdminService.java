package com.phoenix.assetbe.service;

import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.model.asset.Category;
import com.phoenix.assetbe.model.asset.SubCategory;
import com.phoenix.assetbe.model.asset.SubQueryCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final CategoryService categoryService;
    private final SubQueryCategory subQueryCategory;

    /**
     * 카테고리
     */
    public AdminResponse.GetCategoryListOutDTO getCategoryListService(){
        List<Category> categoryList = categoryService.getCategoryList();
        return new AdminResponse.GetCategoryListOutDTO(categoryList);
    }

    /**
     * 서브 카테고리
     */
    public AdminResponse.GetSubCategoryListOutDTO getSubCategoryListService(String categoryName) {
        List<SubCategory> subCategoryList = subQueryCategory.getSubCategoryByCategoryName(categoryName);
        return new AdminResponse.GetSubCategoryListOutDTO(subCategoryList);
    }
}
