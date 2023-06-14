package com.phoenix.assetbe.model.asset;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAssetTag.assetTag;
import static com.phoenix.assetbe.model.asset.QTag.tag;

@RequiredArgsConstructor
@Repository
public class AssetTagQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<String> findTagNameListByAssetId(Long assetId) {
        return queryFactory.selectDistinct(assetTag.tag.tagName)
                .from(assetTag)
                .join(assetTag.tag, tag)
                .where(assetTag.asset.id.eq(assetId))
                .orderBy(tag.tagName.asc())
                .fetch();
    }
}
