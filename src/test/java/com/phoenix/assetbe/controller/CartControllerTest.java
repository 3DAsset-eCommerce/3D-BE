package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.cart.CartRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
import com.phoenix.assetbe.model.order.Order;
import com.phoenix.assetbe.model.order.OrderProduct;
import com.phoenix.assetbe.model.order.OrderProductRepository;
import com.phoenix.assetbe.model.order.OrderRepository;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.model.wish.WishList;
import com.phoenix.assetbe.model.wish.WishListRepository;
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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() throws Exception {
        User user1 = dummy.newUser("유", "현주");
        User user2 = dummy.newUser("김", "현주");
        userRepository.saveAll(Arrays.asList(user1, user2));

        Asset asset1 = dummy.newAsset("뛰는 사람");
        Asset asset2 = dummy.newAsset("걷는 사람");
        Asset asset3 = dummy.newAsset("서있는 사람");
        Asset asset4 = dummy.newAsset("춤추는 사람");
        assetRepository.saveAll(Arrays.asList(asset1, asset2, asset3, asset4));

        Cart cart1 = Cart.builder().user(user1).asset(asset1).build(); //order wish
        Cart cart2 = Cart.builder().user(user1).asset(asset2).build(); //order
        Cart cart3 = Cart.builder().user(user1).asset(asset3).build(); //wish
        Cart cart4 = Cart.builder().user(user1).asset(asset4).build(); // null
        cartRepository.saveAll(Arrays.asList(cart1, cart2, cart3, cart4));

        Order order = Order.builder().user(user1).build();
        orderRepository.save(order);

        OrderProduct orderProduct1 = OrderProduct.builder().order(order).asset(asset1).build();
        OrderProduct orderProduct2 = OrderProduct.builder().order(order).asset(asset2).build();
        orderProductRepository.saveAll(Arrays.asList(orderProduct1, orderProduct2));

        WishList wishList1 = WishList.builder().user(user1).asset(asset1).build();
        WishList wishList2 = WishList.builder().user(user1).asset(asset3).build();
        wishListRepository.saveAll(Arrays.asList(wishList1, wishList2));

        em.clear();
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void add_cart_test() throws Exception {
        // Given
        Long userId = 1L;

        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartInDTO)));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        List<Cart> cartItems = cartRepository.findAll();
        assertEquals(6, cartItems.size());
    }

    @Test
    @DisplayName("장바구니 담기 실패 : 권한 체크 실패")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void add_cart_auth_fail_test() throws Exception {
        // Given
        Long userId = 2L;

        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartInDTO)));

        // Then
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("장바구니 삭제 성공")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void delete_cart_test() throws Exception {
        //Given
        Long userId = 1L;
        List<Long> carts = Arrays.asList(1L);

        CartRequest.DeleteCartInDTO deleteCartInDTO = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCartInDTO)));

        //Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        List<Cart> cartItems = cartRepository.findAll();
        assertEquals(3, cartItems.size());
    }

    @Test
    @DisplayName("장바구니 삭제 실패 : 권한 체크 실패")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void delete_cart_auth_fail_test() throws Exception {
        //Given
        Long userId = 2L;
        List<Long> carts = Arrays.asList(1L);

        CartRequest.DeleteCartInDTO deleteCartInDTO = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/cart/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCartInDTO)));

        // Then
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("장바구니 개수 조회 성공")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void count_cart_test() throws Exception {
        // Given
        Long id = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cartCount", id));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.cartCount").value(4));
    }

    @Test
    @DisplayName("장바구니 개수 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void count_cart_auth_fail_test() throws Exception {
        // Given
        Long id = 2L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cartCount", id));

        // Then
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_cart_test() throws Exception {
        // Given
        Long id = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cart", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("장바구니 조회 성공 : 0개 조회")
    @WithUserDetails(value = "김현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_cart_3_test() throws Exception {
        // Given
        Long id = 2L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/cart", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }
}
