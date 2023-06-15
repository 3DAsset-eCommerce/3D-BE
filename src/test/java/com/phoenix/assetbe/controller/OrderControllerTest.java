package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.order.OrderRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.order.*;
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

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("주문 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() throws Exception {
        User user1 = dummy.newUser("유", "현주");
        User user2 = dummy.newUser("김", "현주");
        userRepository.saveAll(Arrays.asList(user1, user2));

        Asset asset1 = dummy.newAsset1("뛰는 사람");
        Asset asset2 = dummy.newAsset1("걷는 사람");
        Asset asset3 = dummy.newAsset1("서있는 사람");
        Asset asset4 = dummy.newAsset1("춤추는 사람");
        assetRepository.saveAll(Arrays.asList(asset1, asset2, asset3, asset4));

        em.clear();
    }

    @Test
    @DisplayName("주문 성공")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void order_assets_test() throws Exception {
        // Given
        List<Long> orderAssetList = Arrays.asList(1L, 2L);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "유현주@nate.com", "현주", "유", "010-1234-1234", 20000D, "카드");

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

        List<OrderProduct> orderCount = orderProductRepository.findAll();
        assertEquals(2, orderCount.size());
    }

    @Test
    @DisplayName("주문 실패 : 유저 조회 실패")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void order_assets_fail1_test() throws Exception {
        // Given
        List<Long> orderAssetList = Arrays.asList(1L, 2L);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "최현주@nate.com", "현주", "유", "010-1234-1234", 20000D, "카드");

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

    }

    @Test
    @DisplayName("주문 실패 : 총 금액 불일치")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void order_assets_fail2_test() throws Exception {
        // Given
        List<Long> orderAssetList = Arrays.asList(1L, 2L);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "유현주@nate.com", "현주", "유", "010-1234-1234", 10000D, "카드");

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
    }
}
