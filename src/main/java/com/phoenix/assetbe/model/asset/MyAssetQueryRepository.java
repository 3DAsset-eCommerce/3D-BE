package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.user.UserResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.asset.QMyAsset.myAsset;

@RequiredArgsConstructor
@Repository
public class MyAssetQueryRepository {

    private final JPAQueryFactory queryFactory;

    public boolean existsAssetIdAndUserId(Long assetId, Long userId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(myAsset)
                .where(myAsset.asset.id.eq(assetId).and(myAsset.user.id.eq(userId)))
                .fetchFirst(); // limit 1
        return fetchOne != null; // 1개가 있는지 없는지 판단 (없으면 null 이므로 null 체크)
    }

    public Page<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> getMyAssetListWithUserIdAndPaging(Long userId, Pageable pageable) {
        List<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> result = queryFactory
                .select(Projections.constructor(UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO.class,
                        asset.id, asset.assetName, asset.fileUrl, asset.thumbnailUrl))
                .from(myAsset)
                .innerJoin(myAsset.asset, asset)
                .where(myAsset.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(myAsset.asset.count())
                .from(myAsset)
                .innerJoin(myAsset.asset, asset)
                .where(myAsset.user.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }
}
