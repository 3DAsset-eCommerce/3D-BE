package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetQueryRepository;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.model.asset.AssetTagQueryRepository;
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
    private final WishListRepository wishListRepository;
    private final AssetTagQueryRepository assetTagQueryRepository;

    public AssetResponse.AssetsOutDTO getAssetsService(Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetsOutDTO.AssetDetail> assetDetails;
        if(myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetDetails = assetQueryRepository.findAssetsWithUserIdAndPaging(userId, pageable);
        }else {
            assetDetails = assetQueryRepository.findAssetsWithPaging(pageable);
        }
        return new AssetResponse.AssetsOutDTO(assetDetails);
    }

    @Transactional
    public AssetResponse.AssetDetailsOutDTO getAssetDetailsService(Long assetId, MyUserDetails myUserDetails) {
        Long wishListId = null;
        if (myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            wishListId = wishListRepository.findIdByAssetIdAndUserId(assetId, userId);
        }
        Asset assetPS = findAssetById(assetId);
        List<String> tagNameList = assetTagQueryRepository.findTagNameListByAssetId(assetId);
        assetPS.increaseVisitCount();
        try {
            assetRepository.save(assetPS);
        }catch (Exception e){
            throw new Exception500("view 증가 실패");
        }
        return new AssetResponse.AssetDetailsOutDTO(assetPS, wishListId, tagNameList);
    }

    public AssetResponse.AssetsOutDTO getAssetListByCategoryService(String categoryName, Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetsOutDTO.AssetDetail> assetDetailList;
        if(myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetDetailList = assetQueryRepository.findAssetListWithUserIdAndPaginationByCategory(userId, categoryName, pageable);
        }else {
            assetDetailList = assetQueryRepository.findAssetListWithPaginationByCategory(categoryName, pageable);
        }
        return new AssetResponse.AssetsOutDTO(assetDetailList);
    }

    public AssetResponse.AssetsOutDTO getAssetListBySubCategoryService(String categoryName, String subCategoryName,
                                                                       Pageable pageable, MyUserDetails myUserDetails) {
        Page<AssetResponse.AssetsOutDTO.AssetDetail> assetDetailList;
        if(myUserDetails != null) {
            Long userId = myUserDetails.getUser().getId();
            assetDetailList = assetQueryRepository
                    .findAssetListWithUserIdAndPaginationBySubCategory(userId, categoryName, subCategoryName, pageable);
        }else {
            assetDetailList = assetQueryRepository
                    .findAssetListWithPaginationBySubCategory(categoryName, subCategoryName, pageable);
        }
        return new AssetResponse.AssetsOutDTO(assetDetailList);
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

