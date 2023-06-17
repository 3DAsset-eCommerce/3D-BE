package com.phoenix.assetbe.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewRequest {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class ReviewInDTO {
        private Long userId;
        private Double rating;
        private String content;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class DeleteReviewInDTO {
        private Long userId;
    }
}
