package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.wishList.WishRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("위시리스트 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class WishControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WishListRepository wishListRepository;


    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
    }
    /**
     * 1L 사용자 -> 1L~8L 구매, 5L~12L 장바구니, 10L~18L 위시
     * 2L 사용자 -> 5L~12L 장바구니, 10L~18L 위시
     * 3L 사용자 -> 1L~8L 구매, 5L~12L 장바구니
     * 4L 사용자 -> 1L~8L 구매, 10L~18L 위시
     * 5L 사용자 -> 1L~8L 구매
     * 6L 사용자 -> 10L~18L 위시
     * 7L 사용자 -> 5L~12L 장바구니
     */

    @Test
    @DisplayName("위시리스트 담기 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void add_wish_test() throws Exception {
        // Given
        Long userId = 1L;
        Long assetId = 1L;

        WishRequest.AddWishInDTO addWishInDTO = new WishRequest.AddWishInDTO();
        addWishInDTO.setUserId(userId);
        addWishInDTO.setAssetId(assetId);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(addWishInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/wishlist/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addWishInDTO)));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        List<WishList> wishItems = wishListRepository.findAllByUser(userId);
        assertEquals(10, wishItems.size());
    }

    @Test
    @DisplayName("위시리스트 담기 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void add_wish_auth_fail_test() throws Exception {
        // Given
        Long userId = 2L;
        Long assetId = 1L;

        WishRequest.AddWishInDTO addWishInDTO = new WishRequest.AddWishInDTO();
        addWishInDTO.setUserId(userId);
        addWishInDTO.setAssetId(assetId);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(addWishInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/wishlist/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addWishInDTO)));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("위시리스트 삭제 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void delete_wish_test() throws Exception {
        //Given
        Long userId = 1L;
        List<Long> wishes = Arrays.asList(1L, 2L);

        WishRequest.DeleteWishInDTO deleteWishInDTO = new WishRequest.DeleteWishInDTO();
        deleteWishInDTO.setUserId(userId);
        deleteWishInDTO.setWishes(wishes);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(deleteWishInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/wishlist/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteWishInDTO)));

        //Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        List<WishList> wishItems = wishListRepository.findAllByUser(userId);
        assertEquals(7, wishItems.size());
    }

    @Test
    @DisplayName("위시리스트 삭제 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void delete_wish_auth_fail_test() throws Exception {
        //Given
        Long userId = 2L;
        List<Long> wishes = Arrays.asList(1L, 2L);

        WishRequest.DeleteWishInDTO deleteWishInDTO = new WishRequest.DeleteWishInDTO();
        deleteWishInDTO.setUserId(userId);
        deleteWishInDTO.setWishes(wishes);
        System.out.println("테스트 request : " + objectMapper.writeValueAsString(deleteWishInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(post("/s/wishlist/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteWishInDTO)));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }

    @Test
    @DisplayName("위시리스트 조회 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_wish_test() throws Exception {
        // Given
        Long id = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/wishlist", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("위시리스트 조회 성공 : 0개 조회")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_wish_0_test() throws Exception {
        // Given
        Long id = 3L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/wishlist", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("위시리스트 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void show_wish_auth_fail_test() throws Exception {
        // Given
        Long id = 2L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/wishlist", id));

        // Then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 response : " + responseBody);
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
    }
}
