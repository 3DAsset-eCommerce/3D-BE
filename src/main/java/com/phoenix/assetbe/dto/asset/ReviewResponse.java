package com.phoenix.assetbe.dto.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse {

    @Getter
    @Setter
    public static class ReviewsOutDTO {
        private boolean hasAsset;
        private boolean hasReview;
        private List<Reviews> reviewList;

        public ReviewsOutDTO(boolean hasAsset, boolean hasReview, List<Reviews> reviewList) {
            this.hasAsset = hasAsset;
            this.hasReview = hasReview;
            this.reviewList = reviewList;
        }

        @Getter @Setter
        public static class Reviews {
            private Long reviewId;
            private Double rating;
            private String content;
            private Long userId;
            private String firstName;
            private String lastName;

            public Reviews(Long reviewId, Double rating, String content,
                           Long userId, String firstName, String lastName) {
                this.reviewId = reviewId;
                this.rating = rating;
                this.content = content;
                this.userId = userId;
                this.firstName = firstName;
                this.lastName = lastName;
            }
        }
    }
}
