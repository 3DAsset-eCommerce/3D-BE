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

    public List<ReviewResponse.ReviewsOutDTO.Reviews> findReviewsByAssetId(Long assetId) {
        return queryFactory.select(Projections.constructor(ReviewResponse.ReviewsOutDTO.Reviews.class,
                review.id, review.rating, review.content, review.user.id, review.user.firstName, review.user.lastName))
                .from(review)
                .innerJoin(review.user, user)
                .where(review.asset.id.eq(assetId))
                .orderBy(review.createdAt.desc(), review.updatedAt.desc())
                .fetch();
    }
}
