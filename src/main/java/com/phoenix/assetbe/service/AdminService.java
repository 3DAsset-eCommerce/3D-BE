package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetQueryRepository;
import com.phoenix.assetbe.model.asset.Category;
import com.phoenix.assetbe.model.asset.SubCategory;
import com.phoenix.assetbe.model.asset.SubQueryCategory;
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
public class AdminService {

    private final CategoryService categoryService;
    private final SubCategoryQueryRepository subCategoryQueryRepository;
    private final AssetQueryRepository assetQueryRepository;

    private final AssetQueryRepository assetQueryRepository;

    /**
     * 카테고리
     */
    public AdminResponse.GetCategoryListOutDTO getCategoryListService(){
        List<Category> categoryList = categoryService.getCategoryList();
        return new AdminResponse.GetCategoryListOutDTO(categoryList);
    }

    /**
     * 서브 카테고리
     */
    public AdminResponse.GetSubCategoryListOutDTO getSubCategoryListService(String categoryName) {
        List<SubCategory> subCategoryList = subCategoryQueryRepository.getSubCategoryByCategoryName(categoryName);
        return new AdminResponse.GetSubCategoryListOutDTO(subCategoryList);
    }

    /**
     * 에셋
     */
    @Transactional
    public void inactiveAssetService(AdminRequest.InactiveAssetInDTO inactiveAssetInDTO) {
        List<Asset> assetList = assetQueryRepository.getAssetListByAssetIdList(inactiveAssetInDTO.getAssets());
        for (Asset asset: assetList)
            asset.changeStatusToINACTIVE();
    }

    @Transactional
    public void activeAssetService(AdminRequest.ActiveAssetInDTO activeAssetInDTO) {
        List<Asset> assetList = assetQueryRepository.getAssetListByAssetIdList(activeAssetInDTO.getAssets());

        for (Asset asset: assetList)
            asset.changeStatusToACTIVE();
    }

    public AdminResponse.AssetListOutDTO getAssetListByAdminService(Long assetNumber, List<String> assetNameList, String category, String subCategory, Pageable pageable){

        if(assetNameList != null) {
            HashSet<String> keywordSet = new LinkedHashSet<>(assetNameList);
            for (String assetName : assetNameList) {
                keywordSet.addAll(Arrays.asList(assetName.split(" ")));
            }
            assetNameList.clear();
            assetNameList = new ArrayList<>(keywordSet);
        }

        Page<AdminResponse.AssetListOutDTO.AssetOutDTO> assetList = assetQueryRepository.findAssetListByAdmin(assetNumber, assetNameList, category, subCategory, pageable);
        return new AdminResponse.AssetListOutDTO(assetList);
    }
}
