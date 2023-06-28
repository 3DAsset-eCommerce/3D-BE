package com.phoenix.assetbe.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class ReviewResponse {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class ReviewListOutDTO {
        private boolean hasAsset;
        private boolean hasReview;
        private boolean hasWishlist;
        private List<ReviewOutDTO> reviewList;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter @Setter
        public static class ReviewOutDTO {
            private Long reviewId;
            private Double rating;
            private String content;
            private Long userId;
            private String firstName;
            private String lastName;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class ReviewOutDTO {
        private Long userId;
        private Long assetId;
        private Long reviewId;
        private String content;
        private Double reviewRating;
        private Double assetRating;
    }
}
