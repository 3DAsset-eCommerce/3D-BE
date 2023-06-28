package com.phoenix.assetbe.model.asset;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.asset.QPreview.preview;

@RequiredArgsConstructor
@Repository
public class PreviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findPreviewListByAssetId(Long assetId) {
        return queryFactory.select(preview.previewUrl)
                .from(preview)
                .where(asset.id.eq(assetId))
                .orderBy(preview.id.asc())
                .fetch();
    }
}
