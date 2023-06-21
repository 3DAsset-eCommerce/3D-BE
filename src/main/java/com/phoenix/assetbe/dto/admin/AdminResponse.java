package com.phoenix.assetbe.dto.admin;

import com.phoenix.assetbe.model.asset.Category;
import com.phoenix.assetbe.model.asset.SubCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AdminResponse {

    @Getter @Setter
    public static class GetCategoryListOutDTO {
        private List<Category> categoryList;

        public GetCategoryListOutDTO(List<Category> categoryList) {
            this.categoryList = categoryList;
        }
    }

    @Getter @Setter
    public static class GetSubCategoryListOutDTO {
        private List<SubCategory> subCategoryList;

        public GetSubCategoryListOutDTO(List<SubCategory> subCategoryList) {
            this.subCategoryList = subCategoryList;
        }
    }
}