package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.cart.CartRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
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

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("장바구니 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class CartControllerTest {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void add_cart_test() throws Exception {
        // Given
        Long userId = 1L;

        List<Long> assets = Arrays.asList(29L, 30L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(addCartInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartInDTO)));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        List<Cart> cartItems = cartRepository.findAllByUser(1L);
        assertEquals(10, cartItems.size());
    }

    @Test
    @DisplayName("장바구니 담기 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void add_cart_auth_fail_test() throws Exception {
        // Given
        Long userId = 2L;

        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(addCartInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartInDTO)));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("장바구니 삭제 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void delete_cart_test() throws Exception {
        //Given
        Long userId = 1L;
        List<Long> carts = Arrays.asList(5L);

        CartRequest.DeleteCartInDTO deleteCartInDTO = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(deleteCartInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCartInDTO)));

        //Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        List<Cart> cartItems = cartRepository.findAllByUser(userId);
        assertEquals(7, cartItems.size());
    }

    @Test
    @DisplayName("장바구니 삭제 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void delete_cart_auth_fail_test() throws Exception {
        //Given
        Long userId = 2L;
        List<Long> carts = Arrays.asList(5L);

        CartRequest.DeleteCartInDTO deleteCartInDTO = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(deleteCartInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCartInDTO)));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("장바구니 개수 조회 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void count_cart_test() throws Exception {
        // Given
        Long id = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cartCount", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.cartCount").value(8));
    }

    @Test
    @DisplayName("장바구니 개수 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void count_cart_auth_fail_test() throws Exception {
        // Given
        Long id = 2L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cartCount", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_cart_test() throws Exception {
        // Given
        Long id = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cart", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("장바구니 조회 성공 : 0개 조회")
    @WithUserDetails(value = "leejihun4@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_cart_0_test() throws Exception {
        // Given
        Long id = 4L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cart", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("장바구니 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_cart_auth_fail_test() throws Exception {
        // Given
        Long id = 2L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cart", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }
}
