package com.phoenix.assetbe.service;


import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception404;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.asset.CategoryQueryRepository;
import com.phoenix.assetbe.model.asset.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public Asset findAssetById(Long assetId){
        Asset assetPS = assetRepository.findById(assetId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 에셋입니다. "));
        return assetPS;
    }

    public List<Asset> findAllAssetById(List<Long> assetIds){
        List<Asset> assetList = assetRepository.findAllById(assetIds);
        return assetList;
    }

//    public AssetResponse.CountOutDTO countByCategory() {
//        List<AssetResponse.CountOutDTO.CountCategory> listByCategory = categoryQueryRepository.countByCategory();
//        if(listByCategory == null) {
//            throw new Exception404("존재하지 않습니다.");
//        }
//
//        List<AssetResponse.CountOutDTO.CountSubCategory> listBySubCategory = categoryQueryRepository.countBySubCategory();
//        if(listBySubCategory == null) {
//            throw new Exception404("존재하지 않습니다.");
//        }
//
//        int[] intArray = new int[listByCategory.size()];
//        IntStream.range(0, listByCategory.size())
//                .forEach(i -> {
//                    intArray[i] = (int) listBySubCategory.stream()
//                            .filter(subCategory -> listByCategory.get(i).getCategoryName().equals(subCategory.getCategoryName()))
//                            .count();
//                });
//
//        List<AssetResponse.CountOutDTO.SubCategory> temp = listBySubCategory.stream()
//                .map(subCategory -> new AssetResponse.CountOutDTO.SubCategory(
//                        subCategory.getSubCategoryName(), subCategory.getSubCategoryCount()))
//                .collect(Collectors.toList());
//
//        List<List<AssetResponse.CountOutDTO.SubCategory>> subCategory = new ArrayList<>();
//        int index = 0;
//        for (int count : intArray) {
//            List<AssetResponse.CountOutDTO.SubCategory> subList = temp.stream()
//                    .skip(index)
//                    .limit(count)
//                    .collect(Collectors.toList());
//
//            subCategory.add(subList);
//            index += count;
//        }
//
//        List<AssetResponse.CountOutDTO.Category> categoryList = IntStream.range(0, listByCategory.size())
//                .mapToObj(m -> {
//                    String categoryName = listByCategory.get(m).getCategoryName();
//                    Long categoryCount = listByCategory.get(m).getCategoryCount();
//                    List<AssetResponse.CountOutDTO.SubCategory> subCategoryList = subCategory.get(m);
//                    return new AssetResponse.CountOutDTO.Category(categoryName, categoryCount, subCategoryList);
//                })
//                .collect(Collectors.toList());
//
//        return new AssetResponse.CountOutDTO(categoryList);
//    }
}

