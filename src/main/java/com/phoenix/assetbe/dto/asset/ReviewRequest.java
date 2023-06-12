package com.phoenix.assetbe.dto.asset;

import lombok.Getter;
import lombok.Setter;

public class ReviewRequest {

    @Getter @Setter
    public static class ReviewInDTO {
        private Long userId;
        private Double rating;
        private String content;

        public ReviewInDTO(Long userId, Double rating, String content) {
            this.userId = userId;
            this.rating = rating;
            this.content = content;
        }
    }

    @Getter @Setter
    public static class DeleteReviewInDTO {
        private Long userId;

        public DeleteReviewInDTO(Long userId) {
            this.userId = userId;
        }
    }
}
