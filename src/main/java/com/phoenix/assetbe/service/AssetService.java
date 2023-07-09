package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.model.wish.WishListQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetQueryRepository assetQueryRepository;
    private final WishListQueryRepository wishListQueryRepository;
    private final AssetTagQueryRepository assetTagQueryRepository;
    private final PreviewQueryRepository previewQueryRepository;

    /**
     * 개별 에셋
     */
    public AssetResponse.AssetListOutDTO getAssetListService(Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;

        if(myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetList = assetQueryRepository.findAssetListWithUser(userId, null, pageable);
        }else {
            assetList = assetQueryRepository.findAssetList(null, pageable);
        }

        return new AssetResponse.AssetListOutDTO(assetList);
    }

    /**
     * 에셋 상세보기
     */
    @Transactional
    public AssetResponse.AssetDetailsOutDTO getAssetDetailsService(Long assetId, MyUserDetails myUserDetails) {
        Long wishListId = null;
        Long cartId = null;
        if (myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            AssetResponse.AssetDetailsOutDTO.Ids Ids = assetQueryRepository.findIdByAssetIdAndUserId(assetId, userId);
            wishListId = Ids.getWishlistId();
            cartId = Ids.getCartId();
        }
        Asset assetPS = findAssetById(assetId);
        List<String> tagNameList = assetTagQueryRepository.findTagNameListByAssetId(assetId);
        List<String> previewList = previewQueryRepository.findPreviewListByAssetId(assetId);
        assetPS.increaseVisitCount();
        try {
            assetRepository.save(assetPS);
        }catch (Exception e){
            throw new Exception500("view 증가 실패");
        }
        return new AssetResponse.AssetDetailsOutDTO(assetPS, wishListId, cartId, previewList, tagNameList);
    }

    /**
     * 카테고리별 에셋 조회 및 검색(tag,keyword)
     */
    public AssetResponse.AssetListOutDTO getAssetListByCategoryService(String categoryName, List<String> tagList,
                                                                       List<String> keywordList, Pageable pageable,
                                                                       MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;

        List<String> splitKeywordList = null;

        if(keywordList != null) {
            HashSet<String> keywordSet = new LinkedHashSet<>(keywordList);
            for (String keyword : keywordList) {
                keywordSet.addAll(Arrays.asList(keyword.split(" ")));
            }
            splitKeywordList = new ArrayList<>(keywordSet);
        }

        if (myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetList = assetQueryRepository.findAssetListWithUserByCategoryOrSubCategory(userId, categoryName, null, tagList, splitKeywordList, pageable);
        } else {
            assetList = assetQueryRepository.findAssetListByCategoryOrSubCategory(categoryName, null, tagList, splitKeywordList, pageable);
        }

        return new AssetResponse.AssetListOutDTO(assetList);
    }

    /**
     * 서브카테고리별 에셋 조회 및 검색(tag,keyword)
     */
    public AssetResponse.AssetListOutDTO getAssetListBySubCategoryService(String categoryName, String subCategoryName,
                                                                          List<String> tagList, List<String> keywordList,
                                                                          Pageable pageable, MyUserDetails myUserDetails) {

        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;

        List<String> splitKeywordList = null;

        if(keywordList != null) {
            HashSet<String> keywordSet = new LinkedHashSet<>(keywordList);
            for (String keyword : keywordList) {
                keywordSet.addAll(Arrays.asList(keyword.split(" ")));
            }
            splitKeywordList = new ArrayList<>(keywordSet);
        }

        if (myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetList = assetQueryRepository
                    .findAssetListWithUserByCategoryOrSubCategory(userId, categoryName, subCategoryName, tagList, splitKeywordList, pageable);
        } else {
            assetList = assetQueryRepository
                    .findAssetListByCategoryOrSubCategory(categoryName, subCategoryName, tagList, splitKeywordList, pageable);
        }

        return new AssetResponse.AssetListOutDTO(assetList);
    }

    /**
     * 에셋 검색(keyword)
     */
    public AssetResponse.AssetListOutDTO getAssetListBySearchService(List<String> keywordList,
                                                                     Pageable pageable, MyUserDetails myUserDetails) {

        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;

        if (keywordList == null || keywordList.isEmpty()) {
            throw new Exception400("keyword", "잘못된 요청입니다. ");
        } else {

            HashSet<String> keywordSet = new LinkedHashSet<>(keywordList);

            for (String keyword : keywordList) {
                keywordSet.addAll(Arrays.asList(keyword.split(" ")));
            }

            List<String> splitKeywordList = new ArrayList<>(keywordSet);

            if (myUserDetails != null) {
                Long userId = myUserDetails.getUser().getId();
                assetList = assetQueryRepository
                        .findAssetListWithUser(userId, splitKeywordList, pageable);
            } else {
                assetList = assetQueryRepository
                        .findAssetList(splitKeywordList, pageable);
            }

        }

        return new AssetResponse.AssetListOutDTO(assetList);
    }

    public Asset findAssetById(Long assetId){
        Asset assetPS = assetRepository.findById(assetId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 에셋입니다. "));
        return assetPS;
    }

    public List<Asset> findAllAssetById(List<Long> assetIds){
        List<Asset> assetList = assetRepository.findAllById(assetIds);
        return assetList;
    }
}

