package com.phoenix.assetbe.dto.asset;

import com.phoenix.assetbe.model.asset.Asset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public class AssetResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class AssetDetailsOutDTO {
        private Long assetId;
        private String assetName;
        private Double price;
        private String description;
        private Integer discount;
        private Double discountPrice;
        private String extension;
        private Double fileSize;
        private String fileUrl;
        private String creator;
        private Double rating;
        private Long reviewCount;
        private Long wishCount;
        private Long visitCount;
        private Long wishlistId;
        private Long cartId;
        private List<String> previewList;
        private List<String> tagList;

        public AssetDetailsOutDTO(Asset asset, Long wishlistId, Long cartId, List<String> previewList, List<String> tagList) {
            this.assetId = asset.getId();
            this.assetName = asset.getAssetName();
            this.price = asset.getPrice();
            this.description = asset.getDescription();
            this.discount = asset.getDiscount();
            this.discountPrice = asset.getDiscountPrice();
            this.extension = asset.getExtension();
            this.fileSize = asset.getSize();
            this.fileUrl = asset.getFileUrl();
            this.creator = asset.getCreator();
            this.rating = asset.getRating();
            this.reviewCount = asset.getReviewCount();
            this.wishCount = asset.getWishCount();
            this.visitCount = asset.getVisitCount();
            this.wishlistId = wishlistId;
            this.cartId = cartId;
            this.previewList = previewList;
            this.tagList = tagList;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter @Setter
        public static class Ids {
            private Long assetId;
            private Long wishlistId;
            private Long cartId;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class AssetListOutDTO {
        private List<?> assetList;
        private int size;
        private int currentPage;
        private int totalPage;
        private long totalElement;

        public AssetListOutDTO(Page<?> assetList) {
            this.assetList = assetList.getContent();
            this.size = assetList.getSize();
            this.currentPage = assetList.getPageable().getPageNumber();
            this.totalPage = assetList.getTotalPages();
            this.totalElement = assetList.getTotalElements();
        }


        @NoArgsConstructor
        @AllArgsConstructor
        @Getter @Setter
        public static class AssetOutDTO {
            private Long assetId;
            private String assetName;
            private Double price;
            private Integer discount;
            private Double discountPrice;
            private LocalDate releaseDate;
            private String thumbnailUrl;
            private Double rating;
            private Long reviewCount;
            private Long wishCount;
            private Long wishlistId;
            private Long cartId;

            public AssetOutDTO(Long assetId, String assetName, Double price,
                               Integer discount, Double discountPrice,
                               LocalDate releaseDate, String thumbnailUrl, Double rating, Long reviewCount,
                               Long wishCount) {
                this.assetId = assetId;
                this.assetName = assetName;
                this.price = price;
                this.discount = discount;
                this.discountPrice = discountPrice;
                this.releaseDate = releaseDate;
                this.thumbnailUrl = thumbnailUrl;
                this.rating = rating;
                this.reviewCount = reviewCount;
                this.wishCount = wishCount;
            }
        }
    }
}
