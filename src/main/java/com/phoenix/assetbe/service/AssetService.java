package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception404;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.model.wish.WishListQueryRepository;
import com.phoenix.assetbe.model.wish.WishListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public AssetResponse.AssetListOutDTO getAssetListService(Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;
        if(myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetList = assetQueryRepository.findAssetListWithUserIdAndPaging(userId, pageable);
        }else {
            assetList = assetQueryRepository.findAssetListWithPaging(pageable);
        }
        if (assetList.getContent().isEmpty()) {
            throw new Exception404("에셋이 존재하지 않습니다. ");
        }
        return new AssetResponse.AssetListOutDTO(assetList);
    }

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

    public AssetResponse.AssetListOutDTO getAssetListByCategoryService(String categoryName, Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;
        if(myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetList = assetQueryRepository.findAssetListWithUserIdAndPaginationByCategory(userId, categoryName, pageable);
        }else {
            assetList = assetQueryRepository.findAssetListWithPaginationByCategory(categoryName, pageable);
        }
        return new AssetResponse.AssetListOutDTO(assetList);
    }

    public AssetResponse.AssetListOutDTO getAssetListBySubCategoryService(String categoryName, String subCategoryName,
                                                                       Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetListOutDTO.AssetOutDTO> assetList;
        if(myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetList = assetQueryRepository
                    .findAssetListWithUserIdAndPaginationBySubCategory(userId, categoryName, subCategoryName, pageable);
        }else {
            assetList = assetQueryRepository
                    .findAssetListWithPaginationBySubCategory(categoryName, subCategoryName, pageable);
        }
        return new AssetResponse.AssetListOutDTO(assetList);
    }

    public Asset findAssetById(Long assetId){
        Asset assetPS = assetQueryRepository.findById(assetId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 에셋입니다. "));
        return assetPS;
    }

    public List<Asset> findAllAssetById(List<Long> assetIds){
        List<Asset> assetList = assetQueryRepository.findAllById(assetIds);
        return assetList;
    }
}

