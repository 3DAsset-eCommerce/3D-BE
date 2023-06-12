package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.ReviewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QMyAsset.myAsset;
import static com.phoenix.assetbe.model.asset.QReview.review;
import static com.phoenix.assetbe.model.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class ReviewQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ReviewResponse.ReviewsOutDTO.Reviews> findReviewsByAssetId(Long assetId) {
        return queryFactory.select(Projections.constructor(ReviewResponse.ReviewsOutDTO.Reviews.class,
                review.id, review.rating, review.content, review.user.id, review.user.firstName, review.user.lastName))
                .from(review)
                .innerJoin(review.user, user)
                .where(review.asset.id.eq(assetId))
                .orderBy(review.createdAt.desc(), review.updatedAt.desc())
                .fetch();
    }

    public ReviewResponse.AddReviewOutDTO findReviewByUserIdAndAssetId(Long userId, Long assetId) {
        return queryFactory.select(Projections.constructor(ReviewResponse.AddReviewOutDTO.class,
                review.user.id, review.asset.id, review.id, review.rating, review.content))
                .from(review)
                .where(review.user.id.eq(userId).and(review.asset.id.eq(assetId)))
                .fetchOne();
    }

    public boolean existsReviewByAssetIdAndUserId(Long assetId, Long userId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(review)
                .where(review.asset.id.eq(assetId).and(review.user.id.eq(userId)))
                .fetchFirst(); // limit 1
        return fetchOne != null; // 1개가 있는지 없는지 판단 (없으면 null 이므로 null 체크)
    }
}
