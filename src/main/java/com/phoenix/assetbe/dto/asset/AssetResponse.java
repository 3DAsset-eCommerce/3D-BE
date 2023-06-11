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

    @Getter @Setter
    public static class AssetDetailsOutDTO {
        private Long assetId;
        private String assetName;
        private Double price;
        private Double fileSize;
        private String fileUrl;
        private String creator;
        private Double rating;
        private Long reviewCount;
        private Long wishCount;
        private Long visitCount;
        private Long wishlistId;
        private List<String> tagList;

        public AssetDetailsOutDTO(Long assetId, String assetName, Double price,
                                  Double fileSize, String fileUrl, String creator, Double rating,
                                  Long reviewCount, Long wishCount, Long visitCount,
                                  Long wishlistId, List<String> tagList) {
            this.assetId = assetId;
            this.assetName = assetName;
            this.price = price;
            this.fileSize = fileSize;
            this.fileUrl = fileUrl;
            this.creator = creator;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.wishCount = wishCount;
            this.visitCount = visitCount;
            this.wishlistId = wishlistId;
            this.tagList = tagList;
        }
    }
}
