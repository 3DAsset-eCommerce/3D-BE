package com.phoenix.assetbe.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class CategoryResponse {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class CategoryOutDTO {

        private List<CategoryResponse.CategoryOutDTO.CategoryDTO> categoryList;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter @Setter
        public static class CategoryDTO {
            private String categoryName;
            private Long categoryCount;
            private List<String> tagList;
            private List<SubCategoryDTO> subCategoryList;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter @Setter
        public static class CountByCategory {
            private String categoryName;
            private Long categoryCount;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter @Setter
        public static class SubCategoryDTO {
            private String subCategoryName;
            private Long subCategoryCount;
            private List<String> tagList;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter @Setter
        public static class CountBySubCategory {
            private String categoryName;
            private String subCategoryName;
            private Long subCategoryCount;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter @Setter
        public static class CountByTag {
            private String categoryName;
            private String subCategoryName;
            private String tagName;
            private Long tagCount;
        }
    }
}
