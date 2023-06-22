package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.model.asset.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final CategoryService categoryService;
    private final SubCategoryQueryRepository subCategoryQueryRepository;
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

        for (Asset asset: assetList) {
            if (!asset.isStatus()) {
                throw new Exception400("status error", asset.getId() + "번 에셋은 이미 비활성화되어 있습니다. ");
            }
            asset.changeStatusToINACTIVE();
        }
    }
}
