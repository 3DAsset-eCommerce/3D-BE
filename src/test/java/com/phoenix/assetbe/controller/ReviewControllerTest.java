package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("리뷰 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class ReviewControllerTest {
//
//    private DummyEntity dummy = new DummyEntity();
//
//    @Autowired
//    private MyTestSetUp myTestSetUp;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private ObjectMapper om;
//    @Autowired
//    private AssetRepository assetRepository;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        List<User> userList = myTestSetUp.saveUser();
//        List<Asset> assetList = myTestSetUp.saveAsset();
//
//        myTestSetUp.saveUserScenario(userList, assetList);
//        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);
//    }
//
//    @DisplayName("리뷰보기 비로그인유저 성공")
//    @Test
//    public void get_reviews_test() throws Exception {
//        // given
//        Long id = 1L; // 에셋 id
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
//                .get("/assets/{id}/reviews",id));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.msg").value("성공"))
//                .andExpect(jsonPath("$.status").value(200))
//                .andExpect(jsonPath("$.data.hasAsset").value(false))
//                .andExpect(jsonPath("$.data.hasReview").value(false))
//                .andExpect(jsonPath("$.data.hasWishlist").value(false));
//    }
//
//    @DisplayName("리뷰보기 로그인유저 성공")
//    @WithUserDetails(value = "양진호@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void get_reviews_with_user_test() throws Exception {
//        // given
//        Long id = 1L; // 에셋 id
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
//                .get("/assets/{id}/reviews",id));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.msg").value("성공"))
//                .andExpect(jsonPath("$.status").value(200));
//    }
//
//    @DisplayName("리뷰작성 성공")
//    @WithUserDetails(value = "양진호@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void add_review_test() throws Exception {
//        // given
//        Long id = 1L; // 에셋 id
//        Long userId = 2L;
//        ReviewRequest.ReviewInDTO addReviewInDTO =
//                new ReviewRequest.ReviewInDTO(userId, 1D, "테스트입니다.");
//        System.out.println("테스트 request : " + om.writeValueAsString(addReviewInDTO));
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
//                .post("/s/assets/{id}/reviews",id)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(addReviewInDTO)));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.msg").value("성공"))
//                .andExpect(jsonPath("$.status").value(200));
//
//        Asset assetPS = assetRepository.findById(id).orElseThrow(
//                () -> new Exception400("id", "잘못된 요청")
//        );
//
//        System.out.println("ReviewCount: "+assetPS.getReviewCount());
//        System.out.println("Asset Rating: "+assetPS.getRating());
//
//    }
//
//    @DisplayName("리뷰작성 실패 에셋구매X")
//    @WithUserDetails(value = "양진호@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void add_review_fail_hasAsset_false_test() throws Exception {
//        // given
//        Long id = 2L; // 에셋 id
//        Long userId = 4L;
//        ReviewRequest.ReviewInDTO addReviewInDTO =
//                new ReviewRequest.ReviewInDTO(userId, 4D, "테스트입니다.");
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
//                .post("/s/assets/{id}/reviews",id)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(addReviewInDTO)));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // Then
//        resultActions.andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.status").value("403"))
//                .andExpect(jsonPath("$.msg").value("forbidden"))
//                .andExpect(jsonPath("$.data").value("이 에셋을 구매하지 않았습니다. "));
//
//
//    }
//
//    @DisplayName("리뷰작성 실패 이미 리뷰 쓴 경우")
//    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void add_review_fail_hasReview_true_test() throws Exception {
//        // given
//        Long id = 1L; // 에셋 id
//        Long userId = 3L;
//        ReviewRequest.ReviewInDTO addReviewInDTO =
//                new ReviewRequest.ReviewInDTO(userId, 4D, "테스트입니다.");
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
//                .post("/s/assets/{id}/reviews",id)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(addReviewInDTO)));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // Then
//        resultActions.andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.status").value("403"))
//                .andExpect(jsonPath("$.msg").value("forbidden"))
//                .andExpect(jsonPath("$.data").value("이미 이 에셋의 리뷰를 작성하셨습니다. "));
//    }
//
//    @DisplayName("리뷰수정 성공")
//    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void update_review_test() throws Exception {
//        // given
//        Long assetId = 1L; // 에셋 id
//        Long reviewId = 3L;
//        Long userId = 3L;
//        ReviewRequest.ReviewInDTO updateReviewInDTO =
//                new ReviewRequest.ReviewInDTO(userId, 1D, "테스트입니다.");
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
//                .post("/s/assets/{assetid}/reviews/{reviewId}", assetId, reviewId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(updateReviewInDTO)));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.msg").value("성공"))
//                .andExpect(jsonPath("$.status").value(200));
//
//        Asset assetPS = assetRepository.findById(assetId).orElseThrow(
//                () -> new Exception400("assetId", "잘못된 요청입니다. ")
//        );
//        assertEquals(3L, assetPS.getReviewCount()); // 수정 후 ReviewCount는 변하지 않아야 한다.
//        System.out.println("ReviewCount: "+assetPS.getReviewCount());
//        System.out.println("Asset Rating: "+assetPS.getRating());
//
//    }
//
//    @DisplayName("리뷰삭제 성공")
//    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void delete_review_test() throws Exception {
//        // given
//        Long assetId = 1L; // 에셋 id
//        Long reviewId = 3L;
//        Long userId = 3L;
//        ReviewRequest.DeleteReviewInDTO deleteReviewInDTO =
//                new ReviewRequest.DeleteReviewInDTO(userId);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
//                .post("/s/assets/{assetId}/reviews/{reviewId}/delete", assetId, reviewId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(deleteReviewInDTO)));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // Then
//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.msg").value("성공"))
//                .andExpect(jsonPath("$.status").value(200));
//
//        Asset assetPS = assetRepository.findById(assetId).orElseThrow(
//                () -> new Exception400("assetId", "잘못된 요청입니다. ")
//        );
//        assertEquals(2L, assetPS.getReviewCount()); // 삭제 후 ReviewCount는 1 줄어든다.
//        assertEquals(3.5D, assetPS.getRating());
//        System.out.println("ReviewCount: "+assetPS.getReviewCount());
//        System.out.println("Asset Rating: "+assetPS.getRating());
//
//    }
}
