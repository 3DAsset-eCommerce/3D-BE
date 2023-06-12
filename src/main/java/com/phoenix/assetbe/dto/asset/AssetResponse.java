package com.phoenix.assetbe.dto.asset;

import com.phoenix.assetbe.model.asset.Asset;
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

        public AssetDetailsOutDTO(Asset asset, Long wishlistId, List<String> tagList) {
            this.assetId = asset.getId();
            this.assetName = asset.getAssetName();
            this.price = asset.getPrice();
            this.fileSize = asset.getSize();
            this.fileUrl = asset.getFileUrl();
            this.creator = asset.getCreator();
            this.rating = asset.getRating();
            this.reviewCount = asset.getReviewCount();
            this.wishCount = asset.getWishCount();
            this.visitCount = asset.getVisitCount();
            this.wishlistId = wishlistId;
            this.tagList = tagList;
        }
    }
}
