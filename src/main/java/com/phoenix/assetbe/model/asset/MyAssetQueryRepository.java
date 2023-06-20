package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    // 내 정보조회 QueryDSL
    public Page<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> getMyAssetListWithUserIdAndPaging(Long userId, Pageable pageable) {
        List<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> result = queryFactory
                .select(Projections.constructor(UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO.class,
                        asset.id, asset.assetName, asset.fileUrl, asset.thumbnailUrl))
                .from(myAsset)
                .innerJoin(myAsset.asset, asset)
                .where(myAsset.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(myAsset.asset.count())
                .from(myAsset)
                .innerJoin(myAsset.asset, asset)
                .where(myAsset.user.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    // 검색 QueryDSL
    public Page<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> searchMyAssetListWithUserIdAndPagingAndKeyword(Long userId, List<String> keywordList, Pageable pageable) {
        List<BooleanExpression> keywordExpressions = new ArrayList<>();
        for (String keyword: keywordList){
            String[] keywordWords = keyword.split(" "); // 공백을 기준으로 단어 분할
            BooleanExpression wordExpression = null; // 일치여부를 확인하기 위해

            for (String word: keywordWords) {
                if (wordExpression == null) wordExpression = asset.assetName.startsWithIgnoreCase(word);
                else wordExpression = wordExpression.or(asset.assetName.startsWithIgnoreCase(word));
            }
            keywordExpressions.add(wordExpression); // 추가
        }

        BooleanExpression combineExpression = keywordExpressions.stream()
                .reduce(BooleanExpression::or) // or 조건으로 결합
                .orElse(null);

        List<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> result = queryFactory
                .select(Projections.constructor(UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO.class,
                        asset.id, asset.assetName, asset.fileUrl, asset.thumbnailUrl))
                .from(myAsset)
                .innerJoin(myAsset.asset, asset)
                .where(myAsset.user.id.eq(userId).and(combineExpression))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(myAsset.asset.count())
                .from(myAsset)
                .innerJoin(myAsset.asset, asset)
                .where(myAsset.user.id.eq(userId).and(combineExpression))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    // 다운로드 QueryDSL
    public List<UserResponse.DownloadMyAssetListOutDTO.MyAssetFileUrlOutDTO> downloadMyAssetByAssetId(List<Long> assets) {
        return queryFactory
                .select(Projections.constructor(UserResponse.DownloadMyAssetListOutDTO.MyAssetFileUrlOutDTO.class,
                        asset.id, asset.fileUrl))
                .from(asset)
                .where(asset.id.in(assets))
                .fetch();
    }

    /**
     * 정렬 기준
     */
    private OrderSpecifier<?> assetSort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "id":
                        return new OrderSpecifier<>(direction, asset.id);
                    case "assetName":
                        return new OrderSpecifier<>(direction, asset.assetName);
                }
            }
        }
        return null;
    }

//    private List<OrderSpecifier<?>> assetSortByKeyword(List<String> keywordList) {
//        if (!keywordList.isEmpty()) {
//            List<ComparableExpressionBase<?>> keywordExpressions = new ArrayList<>();
//            for (String keyword : keywordList) {
//                keywordExpressions.add(asset.assetName.equalsIgnoreCase(keyword));
//            }
//
//            List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
//            for (ComparableExpressionBase<?> expression : keywordExpressions) {
//                orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, expression));
//            }
//
//            return orderSpecifiers;
//        }
//
//        return null;
//    }

    /**
     * 검증
     */
    public void validateMyAssets(Long userId, List<Long> assetIds) {
        long count = queryFactory.select(myAsset)
                .from(myAsset)
                .where(myAsset.user.id.eq(userId).and(myAsset.asset.id.in(assetIds)))
                .fetch()
                .stream()
                .count();

        if (count != assetIds.size()) {
            throw new Exception400("No match", "잘못된 요청입니다. ");
        }
    }
}
