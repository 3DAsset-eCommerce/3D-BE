package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import com.phoenix.assetbe.dto.asset.ReviewResponse;
import com.phoenix.assetbe.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/assets/{assetId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable Long assetId,
                                        @AuthenticationPrincipal MyUserDetails myUserDetails) {

        ReviewResponse.ReviewsOutDTO reviewsOutDTO = reviewService.getReviewsService(assetId, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(reviewsOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/assets/{id}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long id,
                                       @RequestBody ReviewRequest.ReviewInDTO reviewInDTO,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {

        ReviewResponse.ReviewOutDTO addReviewOutDTO =
                reviewService.addReviewService(id, myUserDetails, reviewInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(addReviewOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/assets/{assetId}/reviews/{reviewId}")
    public ResponseEntity<?> addReview(@PathVariable Long assetId, @PathVariable Long reviewId,
                                       @RequestBody ReviewRequest.ReviewInDTO reviewInDTO,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {

        ReviewResponse.ReviewOutDTO updateReviewOutDTO =
                reviewService.updateReviewService(assetId, reviewId, reviewInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(updateReviewOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}