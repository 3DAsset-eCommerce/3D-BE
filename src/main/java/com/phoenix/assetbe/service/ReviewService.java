package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.asset.ReviewResponse;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final MyAssetQueryRepository myAssetQueryRepository;

    public ReviewResponse.ReviewsOutDTO getReviewsService(Long assetId) {
        Long id = assetRepository.findIdByAssetId(assetId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 에셋입니다. ")
        );

        List<Review> reviewList = reviewRepository.findByAssetId(id);
        List<ReviewResponse.ReviewsOutDTO.Reviews> reviewsList = reviewList.stream()
                .sorted(Comparator.comparing(Review::getCreatedAt, Comparator.reverseOrder())
                        .thenComparing(Review::getUpdatedAt, Comparator.reverseOrder()))
                .map(review -> new ReviewResponse.ReviewsOutDTO.Reviews(
                    review.getId(),
                    review.getRating(),
                    review.getContent(),
                    review.getUser().getId(),
                    review.getUser().getFirstName(),
                    review.getUser().getLastName()
                ))
                .collect(Collectors.toList());
        return new ReviewResponse.ReviewsOutDTO(false, false, reviewsList);
    }

    public ReviewResponse.ReviewsOutDTO getReviewsWithUserService(Long assetId, String userEmail) {
        Long userId = userRepository.findIdByEmail(userEmail).orElseThrow(
                () -> new Exception400("email", "존재하지 않는 유저입니다. ")
        );

        Long id = assetRepository.findIdByAssetId(assetId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 에셋입니다. ")
        );

        //이 에셋이 구매한 에셋인지 확인 -> hasAsset
        //이 에셋에 리뷰를 썼는지 확인 -> hasReview
        boolean hasAsset = myAssetQueryRepository.existsAssetIdAndUserId(id, userId);

        List<Review> reviewList = reviewRepository.findByAssetId(id);

        Optional<Review> foundReview = reviewList.stream()
                .filter(review -> review.getUser().getId().equals(userId) && review.getAsset().getId().equals(id))
                .findFirst();

        boolean hasReview = foundReview.isPresent();

        List<ReviewResponse.ReviewsOutDTO.Reviews> reviewsList = reviewList.stream()
                .sorted(Comparator.comparing(Review::getCreatedAt, Comparator.reverseOrder())
                        .thenComparing(Review::getUpdatedAt, Comparator.reverseOrder()))
                .map(review -> new ReviewResponse.ReviewsOutDTO.Reviews(
                        review.getId(),
                        review.getRating(),
                        review.getContent(),
                        review.getUser().getId(),
                        review.getUser().getFirstName(),
                        review.getUser().getLastName()
                ))
                .collect(Collectors.toList());
        return new ReviewResponse.ReviewsOutDTO(hasAsset, hasReview, reviewsList);
    }
}
