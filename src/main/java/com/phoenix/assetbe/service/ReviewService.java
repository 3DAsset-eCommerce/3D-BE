package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import com.phoenix.assetbe.dto.asset.ReviewResponse;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.model.wish.WishListQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final UserService userService;
    private final AssetService assetService;

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MyAssetQueryRepository myAssetQueryRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final WishListQueryRepository wishListQueryRepository;

    @Transactional
    public ReviewResponse.AddReviewOutDTO addReviewService(Long assetId, MyUserDetails myUserDetails
            , ReviewRequest.AddReviewInDTO addReviewInDTO) {

        Long userId = addReviewInDTO.getUserId();
        userService.authCheck(myUserDetails, userId);

        User userPS = userService.findUserById(userId);
        Asset assetPS = assetService.findAssetById(assetId);
        Review review = Review.builder().user(userPS).asset(assetPS).rating(addReviewInDTO.getRating())
                                        .content(addReviewInDTO.getContent()).build();
        try {
            reviewRepository.save(review);
        } catch (Exception e) {
            throw new Exception500("리뷰 작성 실패 : "+e.getMessage());
        }

        return reviewQueryRepository.findReviewByUserIdAndAssetId(userId, assetId);
    }

    public ReviewResponse.ReviewsOutDTO getReviewsService(Long assetId) {
        boolean existAsset = assetRepository.existsById(assetId);
        if (!existAsset) {
            throw new Exception400("id", "존재하지 않는 에셋입니다. ");
        }

        List<ReviewResponse.ReviewsOutDTO.Reviews> reviewsList =
                reviewQueryRepository.findReviewsByAssetId(assetId);

        return new ReviewResponse.ReviewsOutDTO(false, false, false, reviewsList);
    }

    public ReviewResponse.ReviewsOutDTO getReviewsWithUserService(Long assetId, String userEmail) {
        Long userId = userRepository.findIdByEmail(userEmail).orElseThrow(
                () -> new Exception400("email", "존재하지 않는 유저입니다. ")
        );

        Long id = assetRepository.findIdByAssetId(assetId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 에셋입니다. ")
        );

        boolean hasAsset = myAssetQueryRepository.existsAssetIdAndUserId(id, userId);
        boolean hasWishlist = wishListQueryRepository.existsAssetIdAndUserId(id, userId);

        List<ReviewResponse.ReviewsOutDTO.Reviews> reviewsList =
                reviewQueryRepository.findReviewsByAssetId(assetId);

        Optional<ReviewResponse.ReviewsOutDTO.Reviews> foundReview = reviewsList.stream()
                .filter(reviews -> reviews.getUserId().equals(userId))
                .findFirst();

        boolean hasReview = foundReview.isPresent();

        return new ReviewResponse.ReviewsOutDTO(hasAsset, hasReview, hasWishlist, reviewsList);
    }
}