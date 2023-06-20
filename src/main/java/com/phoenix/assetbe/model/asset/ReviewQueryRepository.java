package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.ReviewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QReview.review;
import static com.phoenix.assetbe.model.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class ReviewQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ReviewResponse.ReviewListOutDTO.ReviewOutDTO> findReviewListByAssetId(Long assetId) {
        return queryFactory.select(Projections.constructor(ReviewResponse.ReviewListOutDTO.ReviewOutDTO.class,
                        review.id,
                        review.rating,
                        review.content,
                        review.user.id,
                        review.user.firstName,
                        review.user.lastName)
                )
                .from(review)
                .innerJoin(review.user, user)
                .where(review.asset.id.eq(assetId))
                .orderBy(review.createdAt.desc(), review.updatedAt.desc())
                .fetch();
    }

    public ReviewResponse.ReviewOutDTO findReviewByUserIdAndAssetId(Long userId, Long assetId) {
        return queryFactory.select(Projections.constructor(ReviewResponse.ReviewOutDTO.class,
                        review.user.id,
                        review.asset.id,
                        review.id,
                        review.content,
                        review.rating,
                        review.asset.rating)
                )
                .from(review)
                .innerJoin(review.asset)
                .where(review.user.id.eq(userId).and(review.asset.id.eq(assetId)))
                .fetchOne();
    }

    public Review findReviewByUserIdAndAssetIdWithoutDTO(Long userId, Long assetId) {
        return queryFactory.selectFrom(review)
                .where(review.user.id.eq(userId).and(review.asset.id.eq(assetId)))
                .fetchOne();
    }

    public Long findReviewIdByUserIdAndAssetId(Long userId, Long assetId) {
        return queryFactory.select(review.id)
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

    public Double findSumRatingByAssetId(Long assetId) {
        return queryFactory
                .select(review.rating.sum())
                .from(review)
                .where(review.asset.id.eq(assetId))
                .fetchOne();
    }
}
