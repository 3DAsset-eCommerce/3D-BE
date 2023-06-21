package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception404;
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
            assetList = assetQueryRepository.findAssetListWithUserAndPage(userId, pageable);
        }else {
            assetList = assetQueryRepository.findAssetListWithPage(pageable);
        }
        if (assetList.getContent().isEmpty()) {
            throw new Exception404("에셋이 존재하지 않습니다. ");
        }
        return new AssetResponse.AssetListOutDTO(assetList);
    }

    /**
     * 에셋 상세보기
     */
    @Transactional
    public AssetResponse.AssetDetailsOutDTO getAssetDetailsService(Long assetId, MyUserDetails myUserDetails) {
        Long wishListId = null;
        if (myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            wishListId = wishListQueryRepository.findIdByAssetIdAndUserId(assetId, userId);
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
        return new AssetResponse.AssetDetailsOutDTO(assetPS, wishListId, previewList, tagNameList);
    }

    /**
     * 카테고리별 에셋 조회
     */
    public AssetResponse.AssetListOutDTO getAssetListByCategoryService(String categoryName, List<String> keywordList,
                                                                       Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;

        if(keywordList == null) {
            if (myUserDetails != null) {
                Long userId = myUserDetails.getUser().getId();
                assetList = assetQueryRepository.findAssetListWithUserAndPageByCategory(userId, categoryName, pageable);
            } else {
                assetList = assetQueryRepository.findAssetListWithPageByCategory(categoryName, pageable);
            }
        }else {
            HashSet<String> keywordSet = new LinkedHashSet<>(keywordList);
            for (String keyword : keywordList) {
                keywordSet.addAll(Arrays.asList(keyword.split(" ")));
            }
            List<String> splitKeywordList = new ArrayList<>(keywordSet);

            if (myUserDetails != null) {
                Long userId = myUserDetails.getUser().getId();
                assetList = assetQueryRepository.findAssetListWithUserAndPageAndSearchByCategory(userId, categoryName, splitKeywordList, pageable);
            } else {
                assetList = assetQueryRepository.findAssetListWithPageAndSearchByCategory(categoryName, splitKeywordList, pageable);
            }
        }
        if(assetList.getContent().isEmpty()) {
            throw new Exception404("에셋이 존재하지 않습니다. ");
        }
        return new AssetResponse.AssetListOutDTO(assetList);
    }

    /**
     * 서브카테고리별 에셋 조회
     */
    public AssetResponse.AssetListOutDTO getAssetListBySubCategoryService(
                                            String categoryName, String subCategoryName, List<String> keywordList,
                                            Pageable pageable, MyUserDetails myUserDetails) {

        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;
        if(keywordList == null) {
            if (myUserDetails != null) {
                Long userId = myUserDetails.getUser().getId();
                assetList = assetQueryRepository
                        .findAssetListWithUserAndPageBySubCategory(userId, categoryName, subCategoryName, pageable);
            } else {
                assetList = assetQueryRepository
                        .findAssetListWithPageBySubCategory(categoryName, subCategoryName, pageable);
            }
        }else {
            HashSet<String> keywordSet = new LinkedHashSet<>(keywordList);
            for (String keyword : keywordList) {
                keywordSet.addAll(Arrays.asList(keyword.split(" ")));
            }
            List<String> splitKeywordList = new ArrayList<>(keywordSet);

            if (myUserDetails != null) {
                Long userId = myUserDetails.getUser().getId();
                assetList = assetQueryRepository
                        .findAssetListWithUserAndPageAndSearchBySubCategory(userId, categoryName, subCategoryName, splitKeywordList, pageable);
            } else {
                assetList = assetQueryRepository
                        .findAssetListWithPageAndSearchBySubCategory(categoryName, subCategoryName, splitKeywordList, pageable);
            }
        }
        if(assetList.getContent().isEmpty()) {
            throw new Exception404("에셋이 존재하지 않습니다. ");
        }

        return new AssetResponse.AssetListOutDTO(assetList);
    }

    /**
     * 에셋 검색
     */
    public AssetResponse.AssetListOutDTO getAssetListBySearchService(List<String> keywordList,
                                                                     Pageable pageable, MyUserDetails myUserDetails) {

        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;

        if(!keywordList.isEmpty()) {

            HashSet<String> keywordSet = new LinkedHashSet<>(keywordList);

            for (String keyword : keywordList) {
                keywordSet.addAll(Arrays.asList(keyword.split(" ")));
            }

            List<String> splitKeywordList = new ArrayList<>(keywordSet);

            if (myUserDetails != null) {
                Long userId = myUserDetails.getUser().getId();
                assetList = assetQueryRepository
                        .findAssetListWithUserAndPageBySearch(userId, splitKeywordList, pageable);
            } else {
                assetList = assetQueryRepository
                        .findAssetListWithPageBySearch(splitKeywordList, pageable);
            }

            if(assetList.getContent().isEmpty()) {
                throw new Exception404("에셋이 존재하지 않습니다. ");
            }

        }else{
            throw new Exception400("keyword", "잘못된 요청입니다. ");
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

