package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.model.asset.AssetTagQueryRepository;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.model.wish.WishListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AssetService {

    private final AssetRepository assetRepository;

    private final AssetTagQueryRepository assetTagQueryRepository;
    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;

    @Transactional
    public AssetResponse.AssetDetailsOutDTO getAssetDetailsService(Long assetId) {
        Asset assetPS = findAssetById(assetId);
        List<String> tagNameList = assetTagQueryRepository.findTagNamesByAssetId(assetId);
        assetPS.increaseVisitCount();
        try {
            assetRepository.save(assetPS);
        }catch (Exception e){
            throw new Exception500("view 증가 실패");
        }
        return new AssetResponse.AssetDetailsOutDTO(assetPS, null, tagNameList);
    }

    @Transactional
    public AssetResponse.AssetDetailsOutDTO getAssetDetailsWithUserService(Long assetId, String userEmail) {
        Long userId = userRepository.findIdByEmail(userEmail).orElseThrow(
                () -> new Exception400("email", "존재하지 않는 유저입니다. ")
        );

        Asset assetPS = findAssetById(assetId);
        Long wishListId = wishListRepository.findIdByAssetIdAndUserId(assetId, userId);
        List<String> tagNameList = assetTagQueryRepository.findTagNamesByAssetId(assetId);
        assetPS.increaseVisitCount();
        try {
            assetRepository.save(assetPS);
        }catch (Exception e){
            throw new Exception500("view 증가 실패");
        }
        return new AssetResponse.AssetDetailsOutDTO(assetPS, wishListId, tagNameList);
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

