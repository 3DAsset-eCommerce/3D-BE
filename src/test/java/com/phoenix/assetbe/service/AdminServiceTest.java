package com.phoenix.assetbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.asset.AssetQueryRepository;
import com.phoenix.assetbe.model.asset.Category;
import com.phoenix.assetbe.model.asset.SubCategory;
import com.phoenix.assetbe.model.order.OrderQueryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("관리자 서비스 TEST")
public class AdminServiceTest extends DummyEntity {

    private AdminService adminService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private SubCategoryQueryRepository subCategoryQueryRepository;
    @Mock
    private AssetQueryRepository assetQueryRepository;
    @Mock
    private OrderQueryRepository orderQueryRepository;

    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminService = new AdminService(categoryService, subCategoryQueryRepository, assetQueryRepository, orderQueryRepository);
    }

    /**
     * 카테고리
     */
    @Test
    public void testGetCategoryListService() throws Exception {
        // given
        List<Category> mockCategoryList = new ArrayList<>();

        // stub 1
        when(categoryService.getCategoryList()).thenReturn(mockCategoryList);

        // when
        AdminResponse.GetCategoryListOutDTO result = adminService.getCategoryListService();

        // then
        verify(categoryService, times(1)).getCategoryList();
    }

    /**
     * 서브 카테고리
     */
    @Test
    public void testGetSubCategoryListService() throws Exception {
        // given
        String categoryName = "pretty";

        List<SubCategory> subCategoryList = new ArrayList<>();
        subCategoryList.add(new SubCategory(1L, "woman"));
        subCategoryList.add(new SubCategory(2L, "man"));

        // stub 1
        when(subCategoryQueryRepository.getSubCategoryByCategoryName(any())).thenReturn(subCategoryList);

        // when
        AdminResponse.GetSubCategoryListOutDTO result = adminService.getSubCategoryListService(categoryName);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(subCategoryList, result.getSubCategoryList());
        verify(subCategoryQueryRepository, times(1)).getSubCategoryByCategoryName(categoryName);
    }

    /**
     * 에셋
     */
    @Test
    public void testInactiveAssetService() throws Exception {
        // given
        List<Long> assetIdList = Arrays.asList(1L, 2L, 3L);

        AdminRequest.InactiveAssetInDTO inactiveAssetInDTO = new AdminRequest.InactiveAssetInDTO();
        inactiveAssetInDTO.setAssets(assetIdList);

        // when
        adminService.inactiveAssetService(inactiveAssetInDTO);

        // then
        verify(assetQueryRepository, times(1)).getAssetListByAssetIdList(assetIdList);
    }

    @Test
    public void testActiveAssetService() throws Exception {
        // given
        List<Long> assetIdList = Arrays.asList(1L, 2L, 3L);

        AdminRequest.ActiveAssetInDTO activeAssetInDTO = new AdminRequest.ActiveAssetInDTO();
        activeAssetInDTO.setAssets(assetIdList);

        // when
        adminService.activeAssetService(activeAssetInDTO);

        // then
        verify(assetQueryRepository, times(1)).getAssetListByAssetIdList(assetIdList);
    }
}
