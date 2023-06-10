package com.phoenix.assetbe.service;

import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.model.asset.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final AssetTagRepository assetTagRepository;

    public AssetResponse.CategoryOutDTO getCategoryList() {
        List<AssetTag> assetTags = assetTagRepository.findAll(); // 모든 AssetTag 레코드를 가져옴

        Map<Category, List<AssetTag>> categoryMap = assetTags.stream()
                .collect(Collectors.groupingBy(AssetTag::getCategory)); // 카테고리별로 AssetTag를 그룹화

        List<AssetResponse.CategoryOutDTO.CategoryDTO> categoryDTOList = new ArrayList<>();

        for (Map.Entry<Category, List<AssetTag>> entry : categoryMap.entrySet()) {
            Category category = entry.getKey();
            List<AssetTag> categoryAssetTags = entry.getValue();

            List<String> tagList = categoryAssetTags.stream()
                    .map(assetTag -> assetTag.getTag().getTagName())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            Map<SubCategory, List<AssetTag>> subCategoryMap = categoryAssetTags.stream()
                    .collect(Collectors.groupingBy(AssetTag::getSubCategory)); // 서브카테고리별로 AssetTag를 그룹화

            List<AssetResponse.CategoryOutDTO.SubCategoryDTO> subCategoryDTOList = new ArrayList<>();

            for (Map.Entry<SubCategory, List<AssetTag>> subCategoryEntry : subCategoryMap.entrySet()) {
                SubCategory subCategory = subCategoryEntry.getKey();
                List<AssetTag> subCategoryAssetTags = subCategoryEntry.getValue();

                List<String> subCategoryTagList = subCategoryAssetTags.stream()
                        .map(assetTag -> assetTag.getTag().getTagName())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                AssetResponse.CategoryOutDTO.SubCategoryDTO subCategoryDTO = new AssetResponse.CategoryOutDTO.SubCategoryDTO(
                        subCategory.getSubCategoryName(), subCategory.getSubCategoryCount(), subCategoryTagList
                );

                subCategoryDTOList.add(subCategoryDTO);
            }

            AssetResponse.CategoryOutDTO.CategoryDTO categoryDTO = new AssetResponse.CategoryOutDTO.CategoryDTO(
                    category.getCategoryName(),category.getCategoryCount(),tagList,subCategoryDTOList
            );

            categoryDTOList.add(categoryDTO);
        }

        // categoryName을 기준으로 카테고리 정렬
        categoryDTOList.sort(Comparator.comparing(AssetResponse.CategoryOutDTO.CategoryDTO::getCategoryName));

        // subCategoryName을 기준으로 서브카테고리 정렬
        for (AssetResponse.CategoryOutDTO.CategoryDTO categoryDTO : categoryDTOList) {
            categoryDTO.getSubCategoryList().sort(Comparator.comparing(AssetResponse.CategoryOutDTO.SubCategoryDTO::getSubCategoryName));
        }

        return new AssetResponse.CategoryOutDTO(categoryDTOList);
    }
}

