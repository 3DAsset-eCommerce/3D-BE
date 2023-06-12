package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import com.phoenix.assetbe.dto.asset.ReviewResponse;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.wish.WishListQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ReviewRepository reviewRepository;
    private final MyAssetQueryRepository myAssetQueryRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final WishListQueryRepository wishListQueryRepository;

    @Transactional
    public ReviewResponse.ReviewOutDTO addReviewService(Long assetId, ReviewRequest.ReviewInDTO addReviewInDTO,
                                                        MyUserDetails myUserDetails) {

        Long userId = addReviewInDTO.getUserId();
        userService.authCheck(myUserDetails, userId);

        User userPS = userService.findUserById(userId);
        Asset assetPS = assetService.findAssetById(assetId);
        boolean hasReview = reviewQueryRepository.existsReviewByAssetIdAndUserId(assetId, userId);
        boolean hasAsset = myAssetQueryRepository.existsAssetIdAndUserId(assetId, userId);
        if(hasAsset) {
            if (!hasReview) {
                Review review = Review.builder().user(userPS).asset(assetPS).rating(addReviewInDTO.getRating())
                        .content(addReviewInDTO.getContent()).build();
                try {
                    reviewRepository.save(review);
                }catch(Exception e) {
                    throw new Exception500("리뷰 작성 실패 : " + e.getMessage());
                }

                Double reviewRatingSum = reviewQueryRepository.findSumRatingByAssetId(assetId);
                assetPS.calculateRatingAndIncreaseReviewCount(assetPS, reviewRatingSum);

                try {
                    assetRepository.save(assetPS);
                }catch (Exception e) {
                    throw new Exception500("에셋 수정 실패 : " + e.getMessage());
                }
            }else {
                throw new Exception403("이미 이 에셋의 리뷰를 작성하셨습니다.");
            }
        }else {
            throw new Exception403("이 에셋을 구매하지 않았습니다.");
        }

        return reviewQueryRepository.findReviewByUserIdAndAssetId(userId, assetId);
    }

    public ReviewResponse.ReviewsOutDTO getReviewsService(Long assetId, MyUserDetails myUserDetails) {
        Long id = assetRepository.findIdByAssetId(assetId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 에셋입니다. ")
        );

        boolean hasAsset = false;
        boolean hasWishlist = false;
        boolean hasReview = false;

        List<ReviewResponse.ReviewsOutDTO.Reviews> reviewsList =
                reviewQueryRepository.findReviewsByAssetId(assetId);

        if (myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            hasAsset = myAssetQueryRepository.existsAssetIdAndUserId(id, userId);
            hasWishlist = wishListQueryRepository.existsAssetIdAndUserId(id, userId);
            Optional<ReviewResponse.ReviewsOutDTO.Reviews> foundReview = reviewsList.stream()
                    .filter(reviews -> reviews.getUserId().equals(userId))
                    .findFirst();
            hasReview = foundReview.isPresent();
        }

        return new ReviewResponse.ReviewsOutDTO(hasAsset, hasReview, hasWishlist, reviewsList);
    }

    @Transactional
    public ReviewResponse.ReviewOutDTO updateReviewService(Long assetId, Long reviewId,
                         ReviewRequest.ReviewInDTO updateReviewInDTO, MyUserDetails myUserDetails) {

        Long userId = updateReviewInDTO.getUserId();
        userService.authCheck(myUserDetails, userId);

        Review reviewPS = reviewQueryRepository.findReviewByUserIdAndAssetIdWithoutDTO(userId, assetId);

        if(reviewPS.getId().equals(reviewId)) {

            reviewPS.updatedReview(updateReviewInDTO);
            try {
                reviewRepository.save(reviewPS);
            }catch(Exception e) {
                throw new Exception500("리뷰 수정 실패");
            }

            Asset assetPS = assetService.findAssetById(assetId);

            Double reviewRatingSum = reviewQueryRepository.findSumRatingByAssetId(assetId);
            assetPS.calculateRatingOnUpdateReview(assetPS, reviewRatingSum);

            try {
                assetRepository.save(assetPS);
            } catch (Exception e) {
                throw new Exception500("에셋 수정 실패 : " + e.getMessage());
            }
        }else{
            throw new Exception400("reviewId", "잘못된 요청입니다.");
        }

        return reviewQueryRepository.findReviewByUserIdAndAssetId(userId, assetId);
    }
}
