package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.annotation.MyLog;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AssetController {

    private final AssetService assetService;

    /**
     * 개별 에셋
     */
    @MyLog
    @GetMapping("/assets")
    public ResponseEntity<?> getAssetList(
            @PageableDefault(size = 28, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        AssetResponse.AssetListOutDTO assetListOutDTO = assetService.getAssetListService(pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(assetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 에셋 상세
     */
    @MyLog
    @GetMapping("/assets/{id}/details")
    public ResponseEntity<?> getAssetDetails(@PathVariable Long id,
                                             @AuthenticationPrincipal MyUserDetails myUserDetails) {

        AssetResponse.AssetDetailsOutDTO assetDetailsOutDTO =
                assetService.getAssetDetailsService(id, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(assetDetailsOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 카테고리별 에셋
     */
    @MyLog
    @GetMapping("/assets/{categoryName}")
    public ResponseEntity<?> getAssetListByCategory(
            @PathVariable String categoryName,
            @RequestParam(value = "tag", required = false) List<String> tagList,
            @RequestParam(value = "keyword", required = false) List<String> keywordList,
            @PageableDefault(size = 28, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        AssetResponse.AssetListOutDTO assetListOutDTO =
                assetService.getAssetListByCategoryService(categoryName, tagList, keywordList, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(assetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 카테고리별
     * 하위카테고리별 에셋
     */
    @MyLog
    @GetMapping("/assets/{categoryName}/{subCategoryName}")
    public ResponseEntity<?> getAssetListBySubCategory(
            @PathVariable String categoryName,
            @PathVariable String subCategoryName,
            @RequestParam(value = "tag", required = false) List<String> tagList,
            @RequestParam(value = "keyword", required = false) List<String> keywordList,
            @PageableDefault(size = 28, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        AssetResponse.AssetListOutDTO assetListOutDTO =
                assetService.getAssetListBySubCategoryService(categoryName, subCategoryName, tagList, keywordList, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(assetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 키워드로 에셋 검색
     */
    @MyLog
    @GetMapping("/assets/search")
    public ResponseEntity<?> getAssetListBySearch(
            @RequestParam(value = "keyword", required = false) List<String> keywordList,
            @PageableDefault(size = 28, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal MyUserDetails myUserDetails) {

        AssetResponse.AssetListOutDTO assetsOutDTO =
                assetService.getAssetListBySearchService(keywordList, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(assetsOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}