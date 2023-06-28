package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.order.OrderRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("주문 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);


    }

    @Test
    @DisplayName("주문 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void order_assets_test() throws Exception {
        // Given
        List<Long> orderAssetList = Arrays.asList(1L, 2L);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "yuhyunju1@nate.com", "hyunju1", "yu", "010-1234-1234", 3000D, "카드");

        String request = objectMapper.writeValueAsString(orderAssetsInDTO);
        System.out.println("테스트 request : " + request);

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("주문 실패 : 유저 조회 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void order_assets_fail1_test() throws Exception {
        // Given
        List<Long> orderAssetList = Arrays.asList(1L, 2L);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "chehyunju@nate.com", "hyunju1", "yu", "010-1234-1234", 20000D, "카드");

        String request = objectMapper.writeValueAsString(orderAssetsInDTO);
        System.out.println("테스트 request : " + request);

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data.key").value("email"))
                .andExpect(jsonPath("$.data.value").value("존재하지 않는 유저입니다. "));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("주문 실패 : 총 금액 불일치")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void order_assets_fail2_test() throws Exception {
        // Given
        List<Long> orderAssetList = Arrays.asList(1L, 2L);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "yuhyunju1@nate.com", "hyunju1", "yu", "010-1234-1234", 10000D, "카드");

        String request = objectMapper.writeValueAsString(orderAssetsInDTO);
        System.out.println("테스트 request : " + request);

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data.key").value("totalPrice"))
                .andExpect(jsonPath("$.data.value").value("정확한 금액을 입력해주세요"));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("주문 내역 조회 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void get_order_list_test() throws Exception {
        // Given
        Long userId = 1L;
        String startDate = LocalDate.of(2023, 6, 1).toString();
        String endDate = LocalDate.of(2023, 6, 10).toString();


        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/s/user/{id}/orders", userId).param("startDate", startDate).param("endDate", endDate));
        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
        resultActions.andDo(document.document(requestParameters(parameterWithName("startDate").description("시작 날짜"), parameterWithName("endDate").description("종료 날짜"))));
        resultActions.andDo(document.document(pathParameters(parameterWithName("id").description("유저 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("주문 내역 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void get_order_list_auth_fail_test() throws Exception {
        // Given
        Long userId = 2L;

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/s/user/{id}/orders", userId));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        resultActions.andDo(document.document(pathParameters(parameterWithName("id").description("유저 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void get_order_product_list_test() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/s/user/{userId}/orders/{orderId}", userId, orderId));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
        resultActions.andDo(document.document(pathParameters(parameterWithName("userId").description("유저 id"), parameterWithName("orderId").description("주문 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("주문 상세 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void get_order_product_list_auth_fail_test() throws Exception {
        // Given
        Long userId = 2L;
        Long orderId = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/s/user/{userId}/orders/{orderId}", userId, orderId));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        resultActions.andDo(document.document(pathParameters(parameterWithName("userId").description("유저 id"), parameterWithName("orderId").description("주문 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("주문 상세 조회 실패 : 내 주문 내역 아님")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void get_order_product_list_fail_others_test() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 2L;

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/s/user/{userId}/orders/{orderId}", userId, orderId));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data.key").value("orderId"))
                .andExpect(jsonPath("$.data.value").value("잘못된 요청입니다. "));
        resultActions.andDo(document.document(pathParameters(parameterWithName("userId").description("유저 id"), parameterWithName("orderId").description("주문 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("주문 상세 조회 실패 : 없는 주문 내역")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void get_order_product_list_auth_fail_others_test() throws Exception {
        // Given
        Long userId = 1L;
        Long orderId = 10L;

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/s/user/{userId}/orders/{orderId}", userId, orderId));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data.key").value("orderId"))
                .andExpect(jsonPath("$.data.value").value("잘못된 요청입니다. "));
        resultActions.andDo(document.document(pathParameters(parameterWithName("userId").description("유저 id"), parameterWithName("orderId").description("주문 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
