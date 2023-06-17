package com.phoenix.assetbe.service;

import com.phoenix.assetbe.dto.asset.CategoryResponse;
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

    private final AssetTagQueryRepository assetTagQueryRepository;

    public CategoryResponse.CategoryOutDTO getCategoryListService() {

        List<CategoryResponse.CategoryOutDTO.CountByCategory> countByCategoryList
                = assetTagQueryRepository.findAssetCountByCategory();

        List<CategoryResponse.CategoryOutDTO.CountBySubCategory> countBySubCategoryList
                = assetTagQueryRepository.findAssetCountBySubCategory();

        List<CategoryResponse.CategoryOutDTO.CountByTag> countByTagList
                = assetTagQueryRepository.findAssetCountByTag();

        // Category 별로 그룹화된 Map을 생성한다.
        Map<String, List<CategoryResponse.CategoryOutDTO.CountByTag>> categoryMap
                = countByTagList.stream()
                .collect(Collectors.groupingBy(CategoryResponse.CategoryOutDTO.CountByTag::getCategoryName));

        // 결과를 담을 리스트를 생성한다.
        List<CategoryResponse.CategoryOutDTO.CategoryDTO> categoryDTOList = new ArrayList<>();

        // Category 별로 처리한다.
        for (Map.Entry<String, List<CategoryResponse.CategoryOutDTO.CountByTag>> entry : categoryMap.entrySet()) {
            String categoryName = entry.getKey();
            List<CategoryResponse.CategoryOutDTO.CountByTag> categoryDataList = entry.getValue();

            // Tag 리스트를 생성한다.
            List<String> categoryTagList = categoryDataList.stream()
                    .map(CategoryResponse.CategoryOutDTO.CountByTag::getTagName)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            // SubCategory 별로 그룹화된 Map을 생성한다.
            Map<String, List<CategoryResponse.CategoryOutDTO.CountByTag>> subCategoryMap = categoryDataList.stream()
                    .collect(Collectors.groupingBy(CategoryResponse.CategoryOutDTO.CountByTag::getSubCategoryName));

            // SubCategory 리스트를 생성한다.
            List<CategoryResponse.CategoryOutDTO.SubCategoryDTO> subCategoryDTOList = new ArrayList<>();

            // SubCategory 별로 처리한다.
            for (Map.Entry<String, List<CategoryResponse.CategoryOutDTO.CountByTag>> subCategoryEntry : subCategoryMap.entrySet()) {
                String subCategoryName = subCategoryEntry.getKey();
                List<CategoryResponse.CategoryOutDTO.CountByTag> subCategoryDataList = subCategoryEntry.getValue();

                // SubCategory 별로 Student 리스트를 생성한다.
                List<String> subCategoryTagList = subCategoryDataList.stream()
                        .map(CategoryResponse.CategoryOutDTO.CountByTag::getTagName)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                Long count = null;
                for(CategoryResponse.CategoryOutDTO.CountBySubCategory countBySubCategory : countBySubCategoryList){
                    if( countBySubCategory.getCategoryName().equals(categoryName)
                            && countBySubCategory.getSubCategoryName().equals(subCategoryName)) {
                        count = countBySubCategory.getSubCategoryCount();
                        break;
                    }
                }

                // subCategoryDTO 객체를 생성하여 subCategoryList에 추가한다.
                CategoryResponse.CategoryOutDTO.SubCategoryDTO subCategoryDTO
                        = new CategoryResponse.CategoryOutDTO.SubCategoryDTO(subCategoryName, count, subCategoryTagList);
                subCategoryDTOList.add(subCategoryDTO);
            }

            Long count = null;
            for(CategoryResponse.CategoryOutDTO.CountByCategory countByCategory : countByCategoryList){
                if( countByCategory.getCategoryName().equals(categoryName)) {
                    count = countByCategory.getCategoryCount();
                    break;
                }
            }

            // ResultDTO 객체를 생성하여 결과 리스트에 추가한다.
            CategoryResponse.CategoryOutDTO.CategoryDTO resultDTO
                    = new CategoryResponse.CategoryOutDTO.CategoryDTO(categoryName, count, categoryTagList, subCategoryDTOList);
            categoryDTOList.add(resultDTO);
        }

        // categoryName을 기준으로 카테고리 정렬
        categoryDTOList.sort(Comparator.comparing(CategoryResponse.CategoryOutDTO.CategoryDTO::getCategoryName));

        // subCategoryName을 기준으로 서브카테고리 정렬
        for (CategoryResponse.CategoryOutDTO.CategoryDTO categoryDTO : categoryDTOList) {
            categoryDTO.getSubCategoryList().sort(Comparator.comparing(CategoryResponse.CategoryOutDTO.SubCategoryDTO::getSubCategoryName));
        }

        return new CategoryResponse.CategoryOutDTO(categoryDTOList);
    }
}

