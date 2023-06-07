package com.phoenix.assetbe.dto.asset;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AssetResponse {

    @Getter @Setter
    public static class CountOutDTO {

        private List<Category> categoryList;

        public CountOutDTO(List<Category> categoryList) {
            this.categoryList = categoryList;
        }

        @Getter @Setter
        public static class Category {
            private String categoryName;
            private Long categoryCount;
            private List<SubCategory> subCategoryList;

            public Category(String categoryName, Long categoryCount, List<SubCategory> subCategoryList) {
                this.categoryName = categoryName;
                this.categoryCount = categoryCount;
                this.subCategoryList = subCategoryList;
            }
        }

        @Getter @Setter
        public static class SubCategoryList{
            private List<SubCategory> subCategoryList;

            public SubCategoryList(List<SubCategory> subCategoryList) {
                this.subCategoryList = subCategoryList;
            }
        }

        @Getter @Setter
        public static class SubCategory {
            private String subCategoryName;
            private Long subCategoryCount;

            public SubCategory(String subCategoryName, Long subCategoryCount) {
                this.subCategoryName = subCategoryName;
                this.subCategoryCount = subCategoryCount;
            }
        }

        @Getter @Setter
        public static class CountCategory {
            private String categoryName;
            private Long categoryCount;

            @QueryProjection
            public CountCategory(String categoryName, Long categoryCount) {
                this.categoryName = categoryName;
                this.categoryCount = categoryCount;
            }
        }

        @Getter @Setter
        public static class CountSubCategory {
            private String categoryName;
            private String subCategoryName;
            private Long subCategoryCount;

            @QueryProjection
            public CountSubCategory(String categoryName, String subCategoryName, Long subCategoryCount) {
                this.categoryName = categoryName;
                this.subCategoryName = subCategoryName;
                this.subCategoryCount = subCategoryCount;
            }
        }
    }
}
