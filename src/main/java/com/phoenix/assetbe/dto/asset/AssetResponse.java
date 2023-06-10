package com.phoenix.assetbe.dto.asset;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AssetResponse {

    @Getter @Setter
    public static class CategoryOutDTO {

        private List<AssetResponse.CategoryOutDTO.CategoryDTO> categoryList;

        public CategoryOutDTO(List<AssetResponse.CategoryOutDTO.CategoryDTO> categoryList) {
            this.categoryList = categoryList;
        }

        @Getter
        @Setter
        public static class CategoryDTO {
            private String categoryName;
            private Long categoryCount;
            private List<String> tagList;
            private List<SubCategoryDTO> subCategoryList;

            public CategoryDTO(String categoryName, Long categoryCount, List<String> tagList, List<SubCategoryDTO> subCategoryList) {
                this.categoryName = categoryName;
                this.categoryCount = categoryCount;
                this.tagList = tagList;
                this.subCategoryList = subCategoryList;
            }
        }

        @Getter
        @Setter
        public static class SubCategoryDTO {
            private String subCategoryName;
            private Long subCategoryCount;
            private List<String> tagList;

            public SubCategoryDTO(String subCategoryName, Long subCategoryCount, List<String> tagList) {
                this.subCategoryName = subCategoryName;
                this.subCategoryCount = subCategoryCount;
                this.tagList = tagList;
            }
        }
    }
}
