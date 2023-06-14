package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.user.UserResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.phoenix.assetbe.model.asset.QMyAsset.myAsset;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<UserResponse.MyAssetListOutDTO.FindMyAssetOutDTO> findMyAsset(Long userId, Pageable pageable) {
        // QueryDSL 작성한다는 사전 준비 세팅
        QMyAsset qMyAsset = QMyAsset.myAsset;
        QAsset qAsset = QAsset.asset;

        return queryFactory
                .select(Projections.constructor(UserResponse.MyAssetListOutDTO.FindMyAssetOutDTO.class,
                        qAsset.id,
                        qAsset.assetName,
                        qAsset.fileUrl,
                        qAsset.thumbnailUrl))
                .from(qMyAsset)
                .join(qMyAsset.asset, qAsset)
                .where(qMyAsset.id.eq(userId)) // userId 넣기
                .fetch()
                .stream()
                .map(asset -> new UserResponse.MyAssetListOutDTO.FindMyAssetOutDTO(
                        asset.getAssetId(),
                        asset.getAssetName(),
                        asset.getFileUrl(),
                        asset.getThumbnailUrl()
                ))
                .collect(Collectors.toList());
    }
}
