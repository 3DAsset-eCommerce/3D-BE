package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.exception.Exception404;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetQueryRepository;
import com.phoenix.assetbe.model.asset.Category;
import com.phoenix.assetbe.model.asset.SubCategory;
import com.phoenix.assetbe.model.order.OrderQueryRepository;
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
    private final OrderQueryRepository orderQueryRepository;
    private final AssetRepository assetRepository;
    private final PreviewRepository previewRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final AssetTagRepository assetTagRepository;
    private final TagRepository tagRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetSubCategoryRepository assetSubCategoryRepository;

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

    /**
     * 에셋 조회
     */
    public AdminResponse.AssetListOutDTO getAssetListByAdminService(Long assetNumber, String assetName, String status, String category, String subCategory, Pageable pageable){
        List<String> assetNameList = null;
        if(assetName != null) {
            HashSet<String> keywordSet = new LinkedHashSet<>(Arrays.asList(assetName.split(" ")));
            assetNameList = new ArrayList<>(keywordSet);
        }

        Page<AdminResponse.AssetListOutDTO.AssetOutDTO> assetList = assetQueryRepository.findAssetListByAdmin(assetNumber, assetNameList, status, category, subCategory, pageable);
        return new AdminResponse.AssetListOutDTO(assetList);
    }

    /**
     * 에셋 수정
     */
    @Transactional
    public void updateAssetService(AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        // 1. asset 유무 확인
        Asset assetPS = assetRepository.findById(updateAssetInDTO.getAssetId()).orElseThrow(
                () -> new Exception404("존재하지 않는 에셋입니다. ")
        );

        // 2. Url, Name, Description, Price, Discount 전부 바꾸기
        changeNameDescriptionPriceDiscount(assetPS, updateAssetInDTO);
        changeUrl(assetPS, updateAssetInDTO);
        changePreviewUrl(assetPS, updateAssetInDTO);

        // 3. Category, SubCategory 변경
        changeCategory(updateAssetInDTO);
        changeSubCategory(updateAssetInDTO);

        // 4. Tag 변경
        changeTag(updateAssetInDTO);
    }

    /**
     * 주문내역조회
     */
    public AdminResponse.OrderListOutDTO getOrderListByAdminService(String orderPeriod, String startDate, String endDate, String orderNumber, String assetNumber, String assetName, String email, Pageable pageable) {
        Page<AdminResponse.OrderListOutDTO.OrderOutDTO> orderList = orderQueryRepository.findOrderListByAdmin(orderPeriod, startDate, endDate, orderNumber, assetNumber, assetName, email, pageable);
        return new AdminResponse.OrderListOutDTO(orderList);
    }















    /**
     * 모듈화
     */
    public void changeUrl(Asset assetPS, AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        if (!updateAssetInDTO.getFileUrl().equals(assetPS.getFileUrl())) {
            assetPS.changeFileUrl(updateAssetInDTO.getFileUrl());
        }
        if (!updateAssetInDTO.getThumbnailUrl().equals(assetPS.getThumbnailUrl())) {
            assetPS.changeThumbnailUrl(updateAssetInDTO.getThumbnailUrl());
        }
    }

    private void changePreviewUrl(Asset assetPS, AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        List<Preview> previewListPS = previewRepository.findPreviewListByAssetId(updateAssetInDTO.getAssetId()).orElseThrow(
                () -> new Exception404("존재하지 않는 프리뷰입니다. ")
        );
        List<String> previewListInDTO = updateAssetInDTO.getPreviewUrlList();
        List<Preview> newPreviewList = new ArrayList<>();

        if (previewListInDTO.size() == previewListPS.size()) {
            for (int i = 0; i < previewListInDTO.size(); i++)
                previewListPS.get(i).changePreviewUrl(previewListInDTO.get(i));
        } else if (previewListInDTO.size() > previewListPS.size()) {
            for (int i = 0; i < previewListPS.size(); i++)
                previewListPS.get(i).changePreviewUrl(previewListInDTO.get(i));
            for (int i = previewListPS.size(); i < previewListInDTO.size(); i++) {
                Preview preview = Preview.builder().asset(assetPS).previewUrl(previewListInDTO.get(i)).build();
                newPreviewList.add(preview);
            }
            previewRepository.saveAll(newPreviewList);

        } else {
            for (int i = 0; i < previewListInDTO.size(); i++)
                previewListPS.get(i).changePreviewUrl(previewListInDTO.get(i));
            for (int i = previewListInDTO.size(); i < previewListPS.size(); i++)
                newPreviewList.add(previewListPS.get(i));
            previewRepository.deleteAll(newPreviewList);
        }
    }

    private void changeNameDescriptionPriceDiscount(Asset assetPS, AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        if (!updateAssetInDTO.getAssetName().equals(assetPS.getAssetName())) {
            assetPS.changeAssetName(updateAssetInDTO.getAssetName());
        }
        if (!updateAssetInDTO.getAssetDescription().equals(assetPS.getDescription())) {
            assetPS.changeAssetDescription(updateAssetInDTO.getAssetDescription());
        }
        if (!updateAssetInDTO.getPrice().equals(assetPS.getPrice())) {
            assetPS.changePrice(updateAssetInDTO.getPrice());
        }
        if (!updateAssetInDTO.getDiscount().equals(assetPS.getDiscount())) {
            assetPS.changeDiscountAndDiscountPrice(updateAssetInDTO.getDiscount());
        }
    }

    private void changeCategory(AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        AssetCategory assetCategoryPS = assetCategoryRepository.findAssetCategoryByAssetId(updateAssetInDTO.getAssetId());
        if (!assetCategoryPS.getCategory().getCategoryName().equals(updateAssetInDTO.getCategory())) {
            String categoryName = updateAssetInDTO.getCategory();
            Category categoryPS = categoryRepository.findCategoryByCategoryName(categoryName).orElseGet(
                    () -> Category.builder()
                            .categoryName(categoryName)
                            .build()
            );
            try {
                categoryRepository.save(categoryPS);
                assetCategoryPS.changeCategory(categoryPS);
            } catch (Exception e) {
                throw new Exception500("카테고리를 DB에 저장하는데 실패했습니다. ");
            }
        }
    }

    private void changeSubCategory(AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        AssetSubCategory assetSubCategoryPS = assetSubCategoryRepository.findAssetSubCategoryByAssetId(updateAssetInDTO.getAssetId());
        if (!assetSubCategoryPS.getSubCategory().getSubCategoryName().equals(updateAssetInDTO.getSubCategory())) {
            String subCategoryName = updateAssetInDTO.getSubCategory();
            SubCategory subCategoryPS = subCategoryRepository.findSubCategoryBySubCategoryName(subCategoryName).orElseGet(
                    () -> SubCategory.builder()
                            .subCategoryName(subCategoryName)
                            .build()
            );
            try {
                subCategoryRepository.save(subCategoryPS);
                assetSubCategoryPS.changeSubCategory(subCategoryPS);
            } catch (Exception e) {
                throw new Exception500("서브 카테고리를 DB에 저장하는데 실패했습니다. ");
            }
        }
    }

    private void changeTag(AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        List<AssetTag> assetTagPS = assetTagRepository.findAssetTagByAssetId(updateAssetInDTO.getAssetId());
        List<String> deleteTags = updateAssetInDTO.getDeleteTagList();
        List<String> addTags = updateAssetInDTO.getAddTagList();

        assetTagPS.removeIf(assetTag -> deleteTags.contains(assetTag.getTag().getTagName()));
        assetTagRepository.deleteAll(assetTagPS);

        List<Tag> tagListPS = new ArrayList<>();
        for (String tagName: addTags) {
            Tag tagPS = tagRepository.findTagByTagName(tagName)
                    .orElseGet(() -> {
                        Tag tag = Tag.builder().tagName(tagName).build();
                        try {
                            tagRepository.save(tag);
                            tagListPS.add(tag);
                        } catch (Exception e) {
                            throw new Exception500("태그 등록에 실패했습니다. ");
                        }
                        return tag;
                    });
            tagListPS.add(tagPS);
        }

        for (int i = 0; i < tagListPS.size(); i++) // 순환하면서 change하는 코드
            assetTagPS.get(i).changeTag(tagListPS.get(i));
    }
}
