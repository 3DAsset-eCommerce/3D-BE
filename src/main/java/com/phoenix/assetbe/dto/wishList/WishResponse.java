package com.phoenix.assetbe.dto.wishList;

import com.phoenix.assetbe.dto.cart.CartResponse;
import com.phoenix.assetbe.model.asset.Asset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class WishResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetWishListWithOrderAndCartOutDTO{
        private Long wishListId;
        private WishResponse.AssetOutInList asset;
        private Long orderId;
        private Long cartId;


        public GetWishListWithOrderAndCartOutDTO(Long cartId, Asset asset, Long orderId, Long wishListId) {
            this.wishListId = wishListId;
            this.asset = new WishResponse.AssetOutInList(asset);
            this.orderId = orderId;
            this.cartId = cartId;
        }

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
