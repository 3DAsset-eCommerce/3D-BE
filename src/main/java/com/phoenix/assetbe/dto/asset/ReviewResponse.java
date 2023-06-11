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
            private Integer rating;
            private String content;
            private Long userId;
            private String firstName;
            private String lastName;
            @JsonIgnore
            private LocalDateTime createdAt;

            public Reviews(Long reviewId, Integer rating, String content,
                           Long userId, String firstName, String lastName,
                           LocalDateTime createdAt) {
                this.reviewId = reviewId;
                this.rating = rating;
                this.content = content;
                this.userId = userId;
                this.firstName = firstName;
                this.lastName = lastName;
                this.createdAt = createdAt;
            }
        }
    }
}
