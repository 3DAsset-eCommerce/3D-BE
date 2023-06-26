package com.phoenix.assetbe.dto.cart;

import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class CartResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CountCartOutDTO{
        private Long cartCount;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetCartWithOrderOutDTO{
        private Long cartId;
        private AssetOutInList asset;
        private Long orderId;
        private Long wishListId;

        public GetCartWithOrderOutDTO(Long cartId, Asset asset, Long orderId, Long wishListId) {
            this.cartId = cartId;
            this.asset = new AssetOutInList(asset);
            this.orderId = orderId;
            this.wishListId = wishListId;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        public static class AssetOutInList{
            private Long assetId;
            private String assetName;
            private Double price;
            private Double discountPrice;
            private String extension;
            private Double size;
            private String thumbnailUrl;

            public AssetOutInList(Asset asset) {
                this.assetId = asset.getId();
                this.assetName = asset.getAssetName();
                this.price = asset.getPrice();
                this.discountPrice = asset.getDiscountPrice();
                this.extension = asset.getExtension();
                this.size = asset.getSize();
                this.thumbnailUrl = asset.getThumbnailUrl();
            }
        }
    }
}
