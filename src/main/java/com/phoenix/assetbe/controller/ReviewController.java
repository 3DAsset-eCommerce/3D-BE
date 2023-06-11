package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import com.phoenix.assetbe.dto.asset.ReviewResponse;
import com.phoenix.assetbe.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/assets/{id}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ReviewResponse.ReviewsOutDTO reviewsOutDTO;

        if (authentication.getPrincipal() == "anonymousUser" || authentication.getPrincipal() == "anonymous"
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {

            reviewsOutDTO = reviewService.getReviewsService(id);

        }else {

            String userEmail = authentication.getName();
            reviewsOutDTO = reviewService.getReviewsWithUserService(id, userEmail);

        }

        ResponseDTO<?> responseDTO = new ResponseDTO<>(reviewsOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/assets/{id}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long id,
                                       @RequestBody ReviewRequest.AddReviewInDTO addReviewInDTO,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {

        ReviewResponse.AddReviewOutDTO addReviewOutDTO =
                reviewService.addReviewService(id, myUserDetails, addReviewInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(addReviewOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}