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
     * 에셋 등록
     */
    @Transactional
    public void addAssetService(AdminRequest.AddAssetInDTO addAssetInDTO) {
        // 1. 에셋 세이브
        Asset assetPS = saveAsset(addAssetInDTO);

        // 2. 프리뷰 세이브
        savePreview(assetPS, addAssetInDTO);

        // 3. 카테고리 세이브
        Category categoryPS = saveCategory(assetPS, addAssetInDTO);

        // 4. 서브카테고리 세이브
        SubCategory subCategoryPS = addSubCategory(assetPS, categoryPS, addAssetInDTO);

        // 5. 태그리스트 세이브
        addTagList(assetPS, categoryPS, subCategoryPS, addAssetInDTO);
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
        Category category = changeCategory(updateAssetInDTO);
        SubCategory subCategory = changeSubCategory(updateAssetInDTO, category);

        // 4. Tag 변경
        changeTag(updateAssetInDTO, category, subCategory);
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

    private Category changeCategory(AdminRequest.UpdateAssetInDTO updateAssetInDTO) {
        AssetCategory assetCategoryPS = assetCategoryRepository.findAssetCategoryByAssetId(updateAssetInDTO.getAssetId());
        if (!assetCategoryPS.getCategory().getCategoryName().equals(updateAssetInDTO.getCategory())) {
            String categoryName = updateAssetInDTO.getCategory();
            Category category = categoryRepository.findCategoryByCategoryName(categoryName).orElseGet(
                    () -> Category.builder()
                            .categoryName(categoryName)
                            .build()
            );
            try {
                Category categoryPS = categoryRepository.save(category);
                assetCategoryPS.changeCategory(categoryPS);
                return categoryPS;
            } catch (Exception e) {
                throw new Exception500("카테고리를 DB에 저장하는데 실패했습니다. ");
            }
        }

        return null;
    }

    private SubCategory changeSubCategory(AdminRequest.UpdateAssetInDTO updateAssetInDTO, Category category) {
        AssetSubCategory assetSubCategoryPS = assetSubCategoryRepository.findAssetSubCategoryByAssetId(updateAssetInDTO.getAssetId());
        if (!assetSubCategoryPS.getSubCategory().getSubCategoryName().equals(updateAssetInDTO.getSubCategory())) {
            String subCategoryName = updateAssetInDTO.getSubCategory();
            SubCategory subCategory = subCategoryRepository.findSubCategoryBySubCategoryName(subCategoryName).orElseGet(
                    () -> SubCategory.builder()
                            .subCategoryName(subCategoryName)
                            .build()
            );

            try {
                SubCategory subCategoryPS = subCategoryRepository.save(subCategory);
                assetSubCategoryPS.changeSubCategory(subCategoryPS);
                if (category != null) {
                    assetSubCategoryPS.changeCategory(category);
                }
                return subCategoryPS;
            } catch (Exception e) {
                throw new Exception500("서브 카테고리를 DB에 저장하는데 실패했습니다. ");
            }
        }
        return null;
    }

    private void changeTag(AdminRequest.UpdateAssetInDTO updateAssetInDTO, Category category, SubCategory subCategory) {
        List<AssetTag> assetTagPSList = assetTagRepository.findAssetTagByAssetId(updateAssetInDTO.getAssetId());
        List<String> deleteTagList = updateAssetInDTO.getDeleteTagList();
        List<String> addTagList = updateAssetInDTO.getAddTagList();

        assetTagPSList.removeIf(assetTag -> !deleteTagList.contains(assetTag.getTag().getTagName()));
        assetTagRepository.deleteAll(assetTagPSList);

        List<Tag> tagList = new ArrayList<>();
        List<Tag> tagPS = tagRepository.findAll();
        List<String> tagNameListPS = tagRepository.findTagNameList();
        for (String tagName: addTagList) {
            if (!tagNameListPS.contains(tagName)) {
                Tag tag = Tag.builder().tagName(tagName).build();
                tagList.add(tag);
            }
        }

        tagRepository.saveAll(tagList);

        for (Tag tag: tagPS) {
            if (addTagList.contains(tag.getTagName()))
                tagList.add(tag);
        }

        assetTagPSList = assetTagRepository.findAssetTagByAssetId(updateAssetInDTO.getAssetId());
        for (AssetTag assetTag: assetTagPSList) {
            assetTag.changeAssetTag(assetTag.getTag(), category, subCategory);
        }

        List<AssetTag> assetTagList = new ArrayList<>();

        for (Tag tag: tagList) {
            AssetTag assetTag = AssetTag.builder()
                    .tag(tag)
                    .category(assetTagPSList.get(0).getCategory())
                    .subCategory(assetTagPSList.get(0).getSubCategory())
                    .build();
            assetTagList.add(assetTag);
        }
        assetTagRepository.saveAll(assetTagList);
    }

    private Asset saveAsset(AdminRequest.AddAssetInDTO addAssetInDTO){
        Asset asset = Asset.builder().build();
        asset.addAssetDetails(addAssetInDTO);
        try {
            return assetRepository.save(asset);
        }catch (Exception e){
            throw new Exception500("에셋 저장이 실패했습니다. ");
        }
    }

    private void savePreview(Asset assetPS, AdminRequest.AddAssetInDTO addAssetInDTO){
        List<Preview> previewList = new ArrayList<>();
        for(String p : addAssetInDTO.getPreviewUrlList()) {
            Preview preview = Preview.builder().asset(assetPS).previewUrl(p).build();
            previewList.add(preview);
        }
        previewRepository.saveAll(previewList);
    }

    private Category saveCategory(Asset assetPS, AdminRequest.AddAssetInDTO addAssetInDTO) {
        String categoryName = addAssetInDTO.getCategory();
        Category category = categoryRepository.findCategoryByCategoryName(categoryName).orElseGet(
                () -> Category.builder()
                        .categoryName(categoryName)
                        .build()
        );
        try {
            Category categoryPS = categoryRepository.save(category);
            AssetCategory assetCategory = AssetCategory.builder().asset(assetPS).category(categoryPS).build();
            try {
                assetCategoryRepository.save(assetCategory);
            }catch (Exception e){
                throw new Exception500("에셋카테고리 저장이 실패했습니다. ");
            }
            return categoryPS;
        }catch (Exception e){
            throw new Exception500("카테고리 저장이 실패했습니다. ");
        }
    }

    private SubCategory addSubCategory(Asset assetPS, Category categoryPS, AdminRequest.AddAssetInDTO addAssetInDTO) {
        String subCategoryName = addAssetInDTO.getSubCategory();
        SubCategory subCategory = subCategoryRepository.findSubCategoryBySubCategoryName(subCategoryName).orElseGet(
                () -> SubCategory.builder()
                        .subCategoryName(subCategoryName)
                        .build()
        );

        try {
            SubCategory subCategoryPS = subCategoryRepository.save(subCategory);
            AssetSubCategory assetSubCategory = AssetSubCategory.builder().asset(assetPS).category(categoryPS).subCategory(subCategoryPS).build();
            try{
                assetSubCategoryRepository.save(assetSubCategory);
            }catch (Exception e){
                throw new Exception500("에셋서브카테고리 저장이 실패했습니다. ");
            }
            return subCategoryPS;
        }catch (Exception e){
            throw new Exception500("서브카테고리 저장이 실패했습니다. ");
        }
    }

    private void addTagList(Asset assetPS, Category categoryPS, SubCategory subCategoryPS, AdminRequest.AddAssetInDTO addAssetInDTO){
        List<String> addTagList = addAssetInDTO.getAddTagList();
        List<String> tagNameListPS = tagRepository.findTagNameList();
        List<Tag> tagList = new ArrayList<>(); // 새로 태그 테이블에 등록할 태그리스트
        for (String tagName: addTagList) {
            if (!tagNameListPS.contains(tagName)) {
                Tag tag = Tag.builder().tagName(tagName).build();
                tagList.add(tag);
            }
        }
        tagRepository.saveAll(tagList);

        List<AssetTag> assetTagList = new ArrayList<>();
        for(Tag tag : tagList) {
            AssetTag assetTag = AssetTag.builder().asset(assetPS).category(categoryPS).subCategory(subCategoryPS).tag(tag).build();
            assetTagList.add(assetTag);
        }
        assetTagRepository.saveAll(assetTagList);
    }

}
