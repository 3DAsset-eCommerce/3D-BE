package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.*;
import com.phoenix.assetbe.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("에셋 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class AssetControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetService assetService;

    @Autowired
    private PreviewRepository previewRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);
    }

    @DisplayName("에셋 상세정보 비로그인유저 성공")
    @Test
    public void get_asset_details_test() throws Exception {
        // Given
        Asset asset = assetService.findAssetById(3L);
        Preview preview1 = Preview.builder().asset(asset).previewUrl("asset3_1_previewUrl").build();
        Preview preview2 = Preview.builder().asset(asset).previewUrl("asset3_2_previewUrl").build();
        Preview preview3 = Preview.builder().asset(asset).previewUrl("asset3_3_previewUrl").build();
        previewRepository.saveAll(Arrays.asList(preview1, preview2, preview3));
        Long id = 3L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/assets/{id}/details", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetId").value(3L));
    }

    @DisplayName("에셋 상세정보 로그인유저 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_details_with_user_test() throws Exception {
        // Given
        Asset asset = assetService.findAssetById(10L);
        Preview preview1 = Preview.builder().asset(asset).previewUrl("asset10_1_previewUrl").build();
        Preview preview2 = Preview.builder().asset(asset).previewUrl("asset10_2_previewUrl").build();
        Preview preview3 = Preview.builder().asset(asset).previewUrl("asset10_3_previewUrl").build();
        previewRepository.saveAll(Arrays.asList(preview1, preview2, preview3));
        Long id = 10L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/assets/{id}/details", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetId").value(10L))
                .andExpect(jsonPath("$.data.wishlistId").value(1L));
    }

    @DisplayName("개별에셋 비로그인유저 성공")
    @Test
    public void get_asset_list_test() throws Exception {
        // Given
        String page = "0";
        String size = "3";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/assets").param("page", page).param("size", size));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList[0].assetId").value(30L))
                .andExpect(jsonPath("$.data.assetList[0].wishlistId").doesNotExist())
                .andExpect(jsonPath("$.data.assetList[0].cartId").doesNotExist());
    }

    @DisplayName("개별에셋 로그인유저 성공")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_list_with_user_test() throws Exception {
        // Given
        String page = "8";
        String size = "3";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/assets").param("page", page).param("size", size));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList[0].assetId").value(6L))
                .andExpect(jsonPath("$.data.assetList[0].wishlistId").doesNotExist())
                .andExpect(jsonPath("$.data.assetList[0].cartId").value(18L));
    }

    @DisplayName("카테고리별 에셋 조회 비로그인유저 성공")
    @Test
    public void get_asset_list_with_pagination_by_category_test() throws Exception {
        // Given
        String categoryName = "luxury";
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/assets/{categoryName}", categoryName).param("page", page).param("size", size));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList[0].assetId").value(24L))
                .andExpect(jsonPath("$.data.assetList[0].assetName").value("luxury dancer"))
                .andExpect(jsonPath("$.data.assetList[0].wishlistId").doesNotExist())
                .andExpect(jsonPath("$.data.assetList[0].cartId").doesNotExist());
    }

    @DisplayName("카테고리별 에셋 조회 로그인유저 성공")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_list_with_user_id_and_pagination_by_category_test() throws Exception {
        // Given
        String categoryName = "luxury";
        String page = "1";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/assets/{categoryName}", categoryName).param("page", page).param("size", size));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList[1].assetId").value(19L))
                .andExpect(jsonPath("$.data.assetList[1].wishlistId").doesNotExist())
                .andExpect(jsonPath("$.data.assetList[1].cartId").doesNotExist());
    }

    @DisplayName("하위 카테고리별 에셋 조회 비로그인 성공")
    @Test
    public void find_asset_list_with_pagination_by_sub_category_test() throws Exception {
        // Given
        String categoryName = "luxury";
        String subCategoryName = "man";
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/assets/{categoryName}/{subCategoryName}", categoryName, subCategoryName)
                        .param("page", page)
                        .param("size", size));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.totalElement").value(1L));
    }

    @DisplayName("하위 카테고리별 에셋 조회 로그인 성공")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void find_asset_list_with_user_id_and_pagination_by_sub_category_test() throws Exception {
        // Given
        String categoryName = "luxury";
        String subCategoryName = "man";
        String page = "0";
        String size = "28";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/assets/{categoryName}/{subCategoryName}", categoryName, subCategoryName)
                        .param("page", page)
                        .param("size", size));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.totalElement").value(1L));
    }

    @DisplayName("에셋 검색 로그인 성공")
    @WithUserDetails(value = "user1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void find_asset_list_with_user_id_and_pagination_by_search_test() throws Exception {
        // given
        String page = "0";
        String size = "4";

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/search")
                .param("keyword", "a", "c", "d", "f", "g")
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("에셋 검색 비로그인 성공")
    @Test
    public void find_asset_list_with_pagination_by_search_test() throws Exception {
        // given
        String page = "0";
        String size = "4";

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/search")
                .param("keyword", "a", "c", "d", "f", "g")
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }
}
