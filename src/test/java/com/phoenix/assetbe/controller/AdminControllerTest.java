package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("관리자 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class AdminControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);

        Asset inactiveAsset1 = dummy.newInactiveAsset("inactiveAsset1", 10000.0, 10.0, null, 2.5); // 31
        assetRepository.save(inactiveAsset1);
    }

    /**
     * 관리자 카테고리
     */
    @DisplayName("관리자 카테고리 조회 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_category_list_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/admin/category"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("관리자 카테고리 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_category_list_fail_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/admin/category"));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    /**
     * 관리자 서브 카테고리
     */
    @DisplayName("관리자 서브 카테고리 조회 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_sub_category_list_test() throws Exception {
        // given
        String categoryName = "pretty";

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/admin/{categoryName}/subcategory", categoryName));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("관리자 서브 카테고리 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_sub_category_list_fail_test() throws Exception {
        // given
        String categoryName = "pretty";

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/admin/{categoryName}/subcategory", categoryName));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    /**
     * 에셋
     */
    @DisplayName("관리자 에셋 비활성화 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void inactive_asset_test() throws Exception {
        // given
        List<Long> assetIdList = new ArrayList<>();
        assetIdList.add(1L);
        assetIdList.add(2L);
        assetIdList.add(3L);

        AdminRequest.InactiveAssetInDTO inactiveAssetInDTO = new AdminRequest.InactiveAssetInDTO();
        inactiveAssetInDTO.setAssets(assetIdList);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/admin/asset/inactive")
                .content(objectMapper.writeValueAsString(inactiveAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("관리자 에셋 비활성화 실패 : 권한 체크 실패")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void inactive_asset_fail_test() throws Exception {
        // given
        List<Long> assetIdList = new ArrayList<>();
        assetIdList.add(1L);
        assetIdList.add(2L);
        assetIdList.add(4L);

        AdminRequest.InactiveAssetInDTO inactiveAssetInDTO = new AdminRequest.InactiveAssetInDTO();
        inactiveAssetInDTO.setAssets(assetIdList);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/admin/asset/inactive")
                .content(objectMapper.writeValueAsString(inactiveAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("관리자 에셋 활성화 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void active_asset_test() throws Exception {
        // given
        List<Long> assetIdList = new ArrayList<>();
        assetIdList.add(31L);

        AdminRequest.ActiveAssetInDTO activeAssetInDTO = new AdminRequest.ActiveAssetInDTO();
        activeAssetInDTO.setAssets(assetIdList);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/admin/asset/active")
                .content(objectMapper.writeValueAsString(activeAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("관리자 에셋 활성화 실패 : 권한 체크 실패")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void active_asset_fail_test() throws Exception {
        // given
        List<Long> assetIdList = new ArrayList<>();
        assetIdList.add(1L);
        assetIdList.add(2L);
        assetIdList.add(4L);

        AdminRequest.InactiveAssetInDTO inactiveAssetInDTO = new AdminRequest.InactiveAssetInDTO();
        inactiveAssetInDTO.setAssets(assetIdList);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/admin/asset/active")
                .content(objectMapper.writeValueAsString(inactiveAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
