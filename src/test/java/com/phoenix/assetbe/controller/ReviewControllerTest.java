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

import java.util.Arrays;
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

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyAssetRepository myAssetRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);

        User user1 = userRepository.findById(1L).orElseThrow();
        User user3 = userRepository.findById(3L).orElseThrow();
        Asset asset = Asset.builder().assetName("a").reviewCount(1L).rating(4D).status(true).build();
        Review review = Review.builder().rating(4D).content("좋아요").user(user3).asset(asset).build();
        MyAsset myAsset1 = MyAsset.builder().asset(asset).user(user3).build();
        MyAsset myAsset2 = MyAsset.builder().asset(asset).user(user1).build();
        assetRepository.save(asset);
        reviewRepository.save(review);
        myAssetRepository.saveAll(Arrays.asList(myAsset1, myAsset2));

        em.clear();
    }

    @DisplayName("리뷰보기 비로그인유저 성공")
    @Test
    public void get_reviews_test() throws Exception {
        // Given
        Long id = 31L; // 에셋 id

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{id}/reviews",id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.hasAsset").value(false))
                .andExpect(jsonPath("$.data.hasReview").value(false))
                .andExpect(jsonPath("$.data.hasWishlist").value(false));
    }

    @DisplayName("리뷰보기 로그인유저 성공")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_reviews_with_user_test() throws Exception {
        // Given
        Long id = 31L; // 에셋 id

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{id}/reviews",id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.hasReview").value(true));
    }

    @DisplayName("리뷰작성 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_review_test() throws Exception {
        // Given
        Long id = 31L; // 에셋 id
        Long userId = 1L;
        ReviewRequest.ReviewInDTO addReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 1D, "테스트입니다.");
        System.out.println("테스트 request : " + om.writeValueAsString(addReviewInDTO));

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/s/assets/{id}/reviews",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(addReviewInDTO)));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetId").value(31L))
                .andExpect(jsonPath("$.data.reviewId").value(2L))
                .andExpect(jsonPath("$.data.content").value("테스트입니다."))
                .andExpect(jsonPath("$.data.reviewRating").value(1D))
                .andExpect(jsonPath("$.data.assetRating").value(2.5D));

        Asset assetPS = assetRepository.findById(id).orElseThrow(
                () -> new Exception400("id", "잘못된 요청")
        );
        assertEquals(2L, assetPS.getReviewCount()); // 작성 후 ReviewCount는 증가한다.
        System.out.println("ReviewCount: "+assetPS.getReviewCount());
        System.out.println("AssetRating: "+assetPS.getRating());

    }

    @DisplayName("리뷰작성 실패 : 에셋 구매 안함")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_review_fail_hasAsset_false_test() throws Exception {
        // Given
        Long id = 31L; // 에셋 id
        Long userId = 2L;
        ReviewRequest.ReviewInDTO addReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 4D, "테스트입니다.");

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/s/assets/{id}/reviews",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(addReviewInDTO)));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("403"))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("이 에셋을 구매하지 않았습니다. "));
    }

    @DisplayName("리뷰작성 실패 : 이전에 리뷰 작성함")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_review_fail_hasReview_true_test() throws Exception {
        // Given
        Long id = 31L; // 에셋 id
        Long userId = 3L;
        ReviewRequest.ReviewInDTO addReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 4D, "테스트입니다.");

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/s/assets/{id}/reviews",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(addReviewInDTO)));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("403"))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("이미 이 에셋의 리뷰를 작성하셨습니다. "));
    }

    @DisplayName("리뷰수정 성공")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_review_test() throws Exception {
        // Given
        Long assetId = 31L; // 에셋 id
        Long reviewId = 1L;
        Long userId = 3L;
        ReviewRequest.ReviewInDTO updateReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 3D, "테스트입니다.");

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/s/assets/{assetid}/reviews/{reviewId}", assetId, reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateReviewInDTO)));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.assetId").value(31L))
                .andExpect(jsonPath("$.data.reviewId").value(1L))
                .andExpect(jsonPath("$.data.content").value("테스트입니다."))
                .andExpect(jsonPath("$.data.reviewRating").value(3D));

        Asset assetPS = assetRepository.findById(assetId).orElseThrow(
                () -> new Exception400("assetId", "잘못된 요청입니다. ")
        );
        assertEquals(1L, assetPS.getReviewCount()); // 수정 후 ReviewCount는 변하지 않아야 한다.
        System.out.println("ReviewCount: "+assetPS.getReviewCount());
        System.out.println("Asset Rating: "+assetPS.getRating());

    }

    @DisplayName("리뷰삭제 성공")
    @WithUserDetails(value = "yangjinho3@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_review_test() throws Exception {
        // Given
        Long assetId = 31L; // 에셋 id
        Long reviewId = 1L;
        Long userId = 3L;
        ReviewRequest.DeleteReviewInDTO deleteReviewInDTO =
                new ReviewRequest.DeleteReviewInDTO(userId);

        // When
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/s/assets/{assetId}/reviews/{reviewId}/delete", assetId, reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(deleteReviewInDTO)));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        Asset assetPS = assetRepository.findById(assetId).orElseThrow(
                () -> new Exception400("assetId", "잘못된 요청입니다. ")
        );
        assertEquals(0L, assetPS.getReviewCount()); // 삭제 후 ReviewCount는 1 줄어든다.
        assertEquals(0D, assetPS.getRating());
        System.out.println("ReviewCount: "+assetPS.getReviewCount());
        System.out.println("Asset Rating: "+assetPS.getRating());
    }
}
