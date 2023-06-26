package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.order.Order;
import com.phoenix.assetbe.model.order.OrderProduct;
import com.phoenix.assetbe.model.order.OrderProductRepository;
import com.phoenix.assetbe.model.order.OrderRepository;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
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
import java.util.Arrays;
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
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);

        Asset asset1 = Asset.builder().assetName("asset1").status(false).build(); // 31
        assetRepository.save(asset1);
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

    @DisplayName("관리자 에셋 조회: 카테고리 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_list_by_admin_with_category_test() throws Exception {
        // Given
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/assets")
                        .param("category", "luxury"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList.size()").value(6L));
    }

    @DisplayName("관리자 에셋 조회: 카테고리&서브카테고리 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_list_by_admin_with_category_and_sub_category_test() throws Exception {
        // Given
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/assets")
                        .param("category", "luxury")
                        .param("subcategory", "man"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList.size()").value(1L));
    }

    @DisplayName("관리자 에셋 조회: 상품명 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_list_by_admin_with_asset_name_test() throws Exception {
        // Given
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/assets")
                        .param("name", "luxury boy"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList.size()").value(1L));
    }

    @DisplayName("관리자 에셋 조회: status=false - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_list_by_admin_with_status_test() throws Exception {
        // Given
        String page = "1";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/assets")
                        .param("status", "false"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetList.size()").value(0L));
    }

    @DisplayName("관리자 주문내역 조회: 기간 & 주문일 최신순 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_order_list_by_admin_with_period_test() throws Exception {
        // Given
        Asset a32 = Asset.builder().assetName("aaaaa").build();
        Asset a33 = Asset.builder().assetName("abbbb").build();
        Asset a34 = Asset.builder().assetName("acccc").build();
        Asset a35 = Asset.builder().assetName("adddd").build();
        Asset a36 = Asset.builder().assetName("aeeee").build();
        Asset a37 = Asset.builder().assetName("affff").build();
        Asset a38 = Asset.builder().assetName("agggg").build();
        Asset a39 = Asset.builder().assetName("ahhhh").build();
        Asset a40 = Asset.builder().assetName("aiiii").build();
        Asset a41 = Asset.builder().assetName("ajjjj").build();
        assetRepository.saveAll(Arrays.asList(a32,a33,a34,a35,a36,a37,a38,a39,a40,a41));

        List<Order> o = orderRepository.findAll();

        OrderProduct p33 = OrderProduct.builder().order(o.get(0)).asset(a32).build();
        OrderProduct p34 = OrderProduct.builder().order(o.get(1)).asset(a33).build();
        OrderProduct p35 = OrderProduct.builder().order(o.get(1)).asset(a34).build();
        OrderProduct p36 = OrderProduct.builder().order(o.get(2)).asset(a35).build();
        OrderProduct p37 = OrderProduct.builder().order(o.get(2)).asset(a36).build();
        OrderProduct p38 = OrderProduct.builder().order(o.get(2)).asset(a37).build();
        OrderProduct p39 = OrderProduct.builder().order(o.get(3)).asset(a38).build();
        OrderProduct p40 = OrderProduct.builder().order(o.get(3)).asset(a39).build();
        OrderProduct p41 = OrderProduct.builder().order(o.get(3)).asset(a40).build();
        OrderProduct p42 = OrderProduct.builder().order(o.get(3)).asset(a41).build();
        orderProductRepository.saveAll(Arrays.asList(p33,p34,p35,p36,p37,p38,p39,p40,p41,p42));
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/orders")
                        .param("period","oneWeek"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderList.size()").value(4L));
    }

    @DisplayName("관리자 주문내역 조회: 기간 & 주문일 오래된순 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_order_list_by_admin_with_period_and_sort_asc_test() throws Exception {
        // Given
        Asset a32 = Asset.builder().assetName("aaaaa").build();
        Asset a33 = Asset.builder().assetName("abbbb").build();
        Asset a34 = Asset.builder().assetName("acccc").build();
        Asset a35 = Asset.builder().assetName("adddd").build();
        Asset a36 = Asset.builder().assetName("aeeee").build();
        Asset a37 = Asset.builder().assetName("affff").build();
        Asset a38 = Asset.builder().assetName("agggg").build();
        Asset a39 = Asset.builder().assetName("ahhhh").build();
        Asset a40 = Asset.builder().assetName("aiiii").build();
        Asset a41 = Asset.builder().assetName("ajjjj").build();
        assetRepository.saveAll(Arrays.asList(a32,a33,a34,a35,a36,a37,a38,a39,a40,a41));

        List<Order> o = orderRepository.findAll();

        OrderProduct p33 = OrderProduct.builder().order(o.get(0)).asset(a32).build();
        OrderProduct p34 = OrderProduct.builder().order(o.get(1)).asset(a33).build();
        OrderProduct p35 = OrderProduct.builder().order(o.get(1)).asset(a34).build();
        OrderProduct p36 = OrderProduct.builder().order(o.get(2)).asset(a35).build();
        OrderProduct p37 = OrderProduct.builder().order(o.get(2)).asset(a36).build();
        OrderProduct p38 = OrderProduct.builder().order(o.get(2)).asset(a37).build();
        OrderProduct p39 = OrderProduct.builder().order(o.get(3)).asset(a38).build();
        OrderProduct p40 = OrderProduct.builder().order(o.get(3)).asset(a39).build();
        OrderProduct p41 = OrderProduct.builder().order(o.get(3)).asset(a40).build();
        OrderProduct p42 = OrderProduct.builder().order(o.get(3)).asset(a41).build();
        orderProductRepository.saveAll(Arrays.asList(p33,p34,p35,p36,p37,p38,p39,p40,p41,p42));
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/orders")
                        .param("sort","createdAt,asc")
                        .param("period","oneWeek"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderList.size()").value(4L))
                .andExpect(jsonPath("$.data.orderList[0].assetName").value("aaaaa 외 8건"));
    }

    @DisplayName("관리자 주문내역 조회: 기간 & 주문번호 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_order_list_by_admin_with_period_and_order_number_test() throws Exception {
        // Given
        Asset a32 = Asset.builder().assetName("aaaaa").build();
        Asset a33 = Asset.builder().assetName("abbbb").build();
        Asset a34 = Asset.builder().assetName("acccc").build();
        Asset a35 = Asset.builder().assetName("adddd").build();
        Asset a36 = Asset.builder().assetName("aeeee").build();
        Asset a37 = Asset.builder().assetName("affff").build();
        Asset a38 = Asset.builder().assetName("agggg").build();
        Asset a39 = Asset.builder().assetName("ahhhh").build();
        Asset a40 = Asset.builder().assetName("aiiii").build();
        Asset a41 = Asset.builder().assetName("ajjjj").build();
        assetRepository.saveAll(Arrays.asList(a32,a33,a34,a35,a36,a37,a38,a39,a40,a41));

        List<Order> o = orderRepository.findAll();

        OrderProduct p33 = OrderProduct.builder().order(o.get(0)).asset(a32).build();
        OrderProduct p34 = OrderProduct.builder().order(o.get(1)).asset(a33).build();
        OrderProduct p35 = OrderProduct.builder().order(o.get(1)).asset(a34).build();
        OrderProduct p36 = OrderProduct.builder().order(o.get(2)).asset(a35).build();
        OrderProduct p37 = OrderProduct.builder().order(o.get(2)).asset(a36).build();
        OrderProduct p38 = OrderProduct.builder().order(o.get(2)).asset(a37).build();
        OrderProduct p39 = OrderProduct.builder().order(o.get(3)).asset(a38).build();
        OrderProduct p40 = OrderProduct.builder().order(o.get(3)).asset(a39).build();
        OrderProduct p41 = OrderProduct.builder().order(o.get(3)).asset(a40).build();
        OrderProduct p42 = OrderProduct.builder().order(o.get(3)).asset(a41).build();
        orderProductRepository.saveAll(Arrays.asList(p33,p34,p35,p36,p37,p38,p39,p40,p41,p42));
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/orders")
                        .param("onum","20230625-000003")
                        .param("period","oneWeek"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderList.size()").value(1L));
    }

    @DisplayName("관리자 주문내역 조회: 기간 & 상품번호 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_order_list_by_admin_with_period_and_asset_number_test() throws Exception {
        // Given
        Asset a32 = Asset.builder().assetName("aaaaa").build();
        Asset a33 = Asset.builder().assetName("abbbb").build();
        Asset a34 = Asset.builder().assetName("acccc").build();
        Asset a35 = Asset.builder().assetName("adddd").build();
        Asset a36 = Asset.builder().assetName("aeeee").build();
        Asset a37 = Asset.builder().assetName("affff").build();
        Asset a38 = Asset.builder().assetName("agggg").build();
        Asset a39 = Asset.builder().assetName("ahhhh").build();
        Asset a40 = Asset.builder().assetName("aiiii").build();
        Asset a41 = Asset.builder().assetName("ajjjj").build();
        assetRepository.saveAll(Arrays.asList(a32,a33,a34,a35,a36,a37,a38,a39,a40,a41));

        List<Order> o = orderRepository.findAll();

        OrderProduct p33 = OrderProduct.builder().order(o.get(0)).asset(a32).build();
        OrderProduct p34 = OrderProduct.builder().order(o.get(1)).asset(a33).build();
        OrderProduct p35 = OrderProduct.builder().order(o.get(1)).asset(a34).build();
        OrderProduct p36 = OrderProduct.builder().order(o.get(2)).asset(a35).build();
        OrderProduct p37 = OrderProduct.builder().order(o.get(2)).asset(a36).build();
        OrderProduct p38 = OrderProduct.builder().order(o.get(2)).asset(a37).build();
        OrderProduct p39 = OrderProduct.builder().order(o.get(3)).asset(a38).build();
        OrderProduct p40 = OrderProduct.builder().order(o.get(3)).asset(a39).build();
        OrderProduct p41 = OrderProduct.builder().order(o.get(3)).asset(a40).build();
        OrderProduct p42 = OrderProduct.builder().order(o.get(3)).asset(a41).build();
        orderProductRepository.saveAll(Arrays.asList(p33,p34,p35,p36,p37,p38,p39,p40,p41,p42));
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/orders")
                        .param("anum","20230625-000037")
                        .param("period","oneWeek"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderList.size()").value(1L));

    }

    @DisplayName("관리자 주문내역 조회: 기간 & 상품명 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_order_list_by_admin_with_period_and_asset_name_test() throws Exception {
        // Given
        Asset a32 = Asset.builder().assetName("aaaaa").build();
        Asset a33 = Asset.builder().assetName("abbbb").build();
        Asset a34 = Asset.builder().assetName("acccc").build();
        Asset a35 = Asset.builder().assetName("adddd").build();
        Asset a36 = Asset.builder().assetName("aeeee").build();
        Asset a37 = Asset.builder().assetName("affff").build();
        Asset a38 = Asset.builder().assetName("agggg").build();
        Asset a39 = Asset.builder().assetName("ahhhh").build();
        Asset a40 = Asset.builder().assetName("aiiii").build();
        Asset a41 = Asset.builder().assetName("ajjjj").build();
        assetRepository.saveAll(Arrays.asList(a32,a33,a34,a35,a36,a37,a38,a39,a40,a41));

        List<Order> o = orderRepository.findAll();

        OrderProduct p33 = OrderProduct.builder().order(o.get(0)).asset(a32).build();
        OrderProduct p34 = OrderProduct.builder().order(o.get(1)).asset(a33).build();
        OrderProduct p35 = OrderProduct.builder().order(o.get(1)).asset(a34).build();
        OrderProduct p36 = OrderProduct.builder().order(o.get(2)).asset(a35).build();
        OrderProduct p37 = OrderProduct.builder().order(o.get(2)).asset(a36).build();
        OrderProduct p38 = OrderProduct.builder().order(o.get(2)).asset(a37).build();
        OrderProduct p39 = OrderProduct.builder().order(o.get(3)).asset(a38).build();
        OrderProduct p40 = OrderProduct.builder().order(o.get(3)).asset(a39).build();
        OrderProduct p41 = OrderProduct.builder().order(o.get(3)).asset(a40).build();
        OrderProduct p42 = OrderProduct.builder().order(o.get(3)).asset(a41).build();
        orderProductRepository.saveAll(Arrays.asList(p33,p34,p35,p36,p37,p38,p39,p40,p41,p42));
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/orders")
                        .param("name","agggg")
                        .param("period","oneWeek"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderList.size()").value(1L));
    }

    @DisplayName("관리자 주문내역 조회: 기간 & 이메일 - 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_order_list_by_admin_with_period_and_email_test() throws Exception {
        // Given
        Asset a32 = Asset.builder().assetName("aaaaa").build();
        Asset a33 = Asset.builder().assetName("abbbb").build();
        Asset a34 = Asset.builder().assetName("acccc").build();
        Asset a35 = Asset.builder().assetName("adddd").build();
        Asset a36 = Asset.builder().assetName("aeeee").build();
        Asset a37 = Asset.builder().assetName("affff").build();
        Asset a38 = Asset.builder().assetName("agggg").build();
        Asset a39 = Asset.builder().assetName("ahhhh").build();
        Asset a40 = Asset.builder().assetName("aiiii").build();
        Asset a41 = Asset.builder().assetName("ajjjj").build();
        assetRepository.saveAll(Arrays.asList(a32,a33,a34,a35,a36,a37,a38,a39,a40,a41));

        List<Order> o = orderRepository.findAll();

        OrderProduct p33 = OrderProduct.builder().order(o.get(0)).asset(a32).build();
        OrderProduct p34 = OrderProduct.builder().order(o.get(1)).asset(a33).build();
        OrderProduct p35 = OrderProduct.builder().order(o.get(1)).asset(a34).build();
        OrderProduct p36 = OrderProduct.builder().order(o.get(2)).asset(a35).build();
        OrderProduct p37 = OrderProduct.builder().order(o.get(2)).asset(a36).build();
        OrderProduct p38 = OrderProduct.builder().order(o.get(2)).asset(a37).build();
        OrderProduct p39 = OrderProduct.builder().order(o.get(3)).asset(a38).build();
        OrderProduct p40 = OrderProduct.builder().order(o.get(3)).asset(a39).build();
        OrderProduct p41 = OrderProduct.builder().order(o.get(3)).asset(a40).build();
        OrderProduct p42 = OrderProduct.builder().order(o.get(3)).asset(a41).build();
        orderProductRepository.saveAll(Arrays.asList(p33,p34,p35,p36,p37,p38,p39,p40,p41,p42));
        String page = "0";
        String size = "4";

        // When
        ResultActions resultActions = mockMvc.perform(
                get("/s/admin/orders")
                        .param("email","yuhyunju1@nate.com")
                        .param("period","oneWeek"));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderList.size()").value(1L));
    }

    @DisplayName("관리자 에셋 수정 성공")
    @WithUserDetails(value = "kuanliza8@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_asset_test() throws Exception {
        // given
        List<String> previewUrlList = new ArrayList<>();
        previewUrlList.add("1 previewUrl");
        previewUrlList.add("2 previewUrl");
        previewUrlList.add("3 previewUrl");
        previewUrlList.add("4 previewUrl");

        List<String> deleteTags = new ArrayList<>();
        deleteTags.add("tag1");
        deleteTags.add("tag2");
        deleteTags.add("tag3");
        deleteTags.add("tag4");
        deleteTags.add("tag5");

        List<String> addTags = new ArrayList<>();
        addTags.add("한글");
        addTags.add("영어");
        addTags.add("중국어");
        addTags.add("일어");
        addTags.add("러시아어");

        AdminRequest.UpdateAssetInDTO updateAssetInDTO = new AdminRequest.UpdateAssetInDTO();
        updateAssetInDTO.setAssetId(1L);
        updateAssetInDTO.setAssetName("Run Motion");
        updateAssetInDTO.setAssetDescription("This motion is Running motion in 3D");
        updateAssetInDTO.setPrice(10.5);
        updateAssetInDTO.setDiscount(10);
        updateAssetInDTO.setCategory("powerful");
        updateAssetInDTO.setSubCategory("robot");
        updateAssetInDTO.setDeleteTagList(deleteTags);
        updateAssetInDTO.setAddTagList(addTags);
        updateAssetInDTO.setFileUrl("This is update FileUrl");
        updateAssetInDTO.setThumbnailUrl("This is update thumbnailUrl");
        updateAssetInDTO.setPreviewUrlList(previewUrlList);

        System.out.println("테스트 request : " + objectMapper.writeValueAsString(updateAssetInDTO));

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/admin/asset/update")
                .content(objectMapper.writeValueAsString(updateAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("관리자 에셋 수정 실패 : 권한 체크 실패")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_asset_fail_test() throws Exception {
        // given
        List<String> previewUrlList = new ArrayList<>();
        previewUrlList.add("1 previewUrl");
        previewUrlList.add("2 previewUrl");
        previewUrlList.add("3 previewUrl");
        previewUrlList.add("4 previewUrl");

        List<String> deleteTags = new ArrayList<>();
        deleteTags.add("tag1");
        deleteTags.add("tag2");
        deleteTags.add("tag3");
        deleteTags.add("tag4");
        deleteTags.add("tag5");

        List<String> addTags = new ArrayList<>();
        addTags.add("한글");
        addTags.add("영어");
        addTags.add("중국어");
        addTags.add("일어");
        addTags.add("러시아어");

        AdminRequest.UpdateAssetInDTO updateAssetInDTO = new AdminRequest.UpdateAssetInDTO();
        updateAssetInDTO.setAssetId(1L);
        updateAssetInDTO.setAssetName("Run Motion");
        updateAssetInDTO.setAssetDescription("This motion is Running motion in 3D");
        updateAssetInDTO.setPrice(10.5);
        updateAssetInDTO.setDiscount(10);
        updateAssetInDTO.setCategory("powerful");
        updateAssetInDTO.setSubCategory("robot");
        updateAssetInDTO.setDeleteTagList(deleteTags);
        updateAssetInDTO.setAddTagList(addTags);
        updateAssetInDTO.setFileUrl("This is update FileUrl");
        updateAssetInDTO.setThumbnailUrl("This is update thumbnailUrl");
        updateAssetInDTO.setPreviewUrlList(previewUrlList);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/admin/asset/update")
                .content(objectMapper.writeValueAsString(updateAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
