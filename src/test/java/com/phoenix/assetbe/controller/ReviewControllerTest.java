package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.*;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;
    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetTagRepository assetTagRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyAssetRepository myAssetRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() throws Exception {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User u1 = User.builder().email("user1@gmail.com").firstName("일").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        User u2 = User.builder().email("user2@gmail.com").firstName("이").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        User u3 = User.builder().email("user3@gmail.com").firstName("삼").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        User u4 = User.builder().email("user4@gmail.com").firstName("사").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));

        Asset a1 = Asset.builder().assetName("a").size(4.0).fileUrl("address-asset1.FBX").extension(".FBX").price(10000D).rating(4.0).releaseDate(LocalDate.parse("2023-05-01")).reviewCount(3L).visitCount(10000L).wishCount(1000L).creator("NationA").build();
        Asset a2 = Asset.builder().assetName("b").size(4.1).fileUrl("address-asset2.FBX").extension(".FBX").price(10001D).rating(4.1).releaseDate(LocalDate.parse("2023-05-02")).reviewCount(101L).visitCount(10001L).wishCount(1001L).creator("NationA").build();
        Asset a3 = Asset.builder().assetName("c").size(4.2).fileUrl("address-asset3.FBX").extension(".FBX").price(10002D).rating(4.2).releaseDate(LocalDate.parse("2023-05-03")).reviewCount(102L).visitCount(10002L).wishCount(1002L).creator("NationA").build();
        Asset a4 = Asset.builder().assetName("d").size(4.3).fileUrl("address-asset4.FBX").extension(".FBX").price(10003D).rating(4.3).releaseDate(LocalDate.parse("2023-05-04")).reviewCount(103L).visitCount(10003L).wishCount(1003L).creator("NationA").build();
        Asset a5 = Asset.builder().assetName("e").size(4.4).fileUrl("address-asset5.FBX").extension(".FBX").price(10004D).rating(4.4).releaseDate(LocalDate.parse("2023-05-05")).reviewCount(104L).visitCount(10004L).wishCount(1004L).creator("NationA").build();
        Asset a6 = Asset.builder().assetName("f").size(4.5).fileUrl("address-asset6.FBX").extension(".FBX").price(10005D).rating(4.5).releaseDate(LocalDate.parse("2023-05-06")).reviewCount(105L).visitCount(10005L).wishCount(1005L).creator("NationA").build();
        Asset a7 = Asset.builder().assetName("g").size(4.6).fileUrl("address-asset7.FBX").extension(".FBX").price(10006D).rating(4.6).releaseDate(LocalDate.parse("2023-05-07")).reviewCount(106L).visitCount(10006L).wishCount(1006L).creator("NationA").build();
        Asset a8 = Asset.builder().assetName("h").size(4.7).fileUrl("address-asset8.FBX").extension(".FBX").price(10007D).rating(4.7).releaseDate(LocalDate.parse("2023-05-08")).reviewCount(107L).visitCount(10007L).wishCount(1007L).creator("NationA").build();
        Asset a9 = Asset.builder().assetName("i").size(4.8).fileUrl("address-asset9.FBX").extension(".FBX").price(10008D).rating(4.8).releaseDate(LocalDate.parse("2023-05-09")).reviewCount(108L).visitCount(10008L).wishCount(1008L).creator("NationA").build();
        assetRepository.saveAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9));

        Category c1 = Category.builder().categoryName("A").categoryCount(500L).build();
        Category c2 = Category.builder().categoryName("B").categoryCount(600L).build();
        Category c3 = Category.builder().categoryName("C").categoryCount(700L).build();
        categoryRepository.saveAll(Arrays.asList(c1, c2, c3));

        SubCategory sc1 = SubCategory.builder().subCategoryName("AA").subCategoryCount(100L).build();
        SubCategory sc2 = SubCategory.builder().subCategoryName("AB").subCategoryCount(110L).build();
        SubCategory sc3 = SubCategory.builder().subCategoryName("AC").subCategoryCount(120L).build();
        SubCategory sc4 = SubCategory.builder().subCategoryName("BA").subCategoryCount(130L).build();
        SubCategory sc5 = SubCategory.builder().subCategoryName("BB").subCategoryCount(140L).build();
        SubCategory sc6 = SubCategory.builder().subCategoryName("BC").subCategoryCount(150L).build();
        SubCategory sc7 = SubCategory.builder().subCategoryName("CA").subCategoryCount(160L).build();
        SubCategory sc8 = SubCategory.builder().subCategoryName("CB").subCategoryCount(170L).build();
        SubCategory sc9 = SubCategory.builder().subCategoryName("CC").subCategoryCount(180L).build();
        subCategoryRepository.saveAll(Arrays.asList(sc1, sc2, sc3, sc4, sc5, sc6, sc7, sc8, sc9));

        Tag t1 = Tag.builder().tagName("tag1").tagCount(300L).build();
        Tag t2 = Tag.builder().tagName("tag2").tagCount(300L).build();
        Tag t3 = Tag.builder().tagName("tag3").tagCount(300L).build();
        Tag t4 = Tag.builder().tagName("tag4").tagCount(300L).build();
        Tag t5 = Tag.builder().tagName("tag5").tagCount(300L).build();
        Tag t6 = Tag.builder().tagName("tag6").tagCount(300L).build();
        tagRepository.saveAll(Arrays.asList(t1, t2, t3, t4, t5, t6));

        AssetTag at1 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t1).build();
        AssetTag at2 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t2).build();
        AssetTag at3 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t3).build();
        AssetTag at4 = AssetTag.builder().asset(a2).category(c1).subCategory(sc2).tag(t4).build();
        AssetTag at5 = AssetTag.builder().asset(a2).category(c1).subCategory(sc2).tag(t5).build();
        AssetTag at6 = AssetTag.builder().asset(a2).category(c1).subCategory(sc2).tag(t6).build();
        AssetTag at7 = AssetTag.builder().asset(a3).category(c1).subCategory(sc3).tag(t1).build();
        AssetTag at8 = AssetTag.builder().asset(a3).category(c1).subCategory(sc3).tag(t2).build();
        AssetTag at9 = AssetTag.builder().asset(a3).category(c1).subCategory(sc3).tag(t3).build();
        AssetTag at10 = AssetTag.builder().asset(a4).category(c2).subCategory(sc4).tag(t4).build();
        AssetTag at11 = AssetTag.builder().asset(a4).category(c2).subCategory(sc4).tag(t5).build();
        AssetTag at12 = AssetTag.builder().asset(a4).category(c2).subCategory(sc4).tag(t6).build();
        AssetTag at13 = AssetTag.builder().asset(a5).category(c2).subCategory(sc5).tag(t1).build();
        AssetTag at14 = AssetTag.builder().asset(a5).category(c2).subCategory(sc5).tag(t2).build();
        AssetTag at15 = AssetTag.builder().asset(a5).category(c2).subCategory(sc5).tag(t3).build();
        AssetTag at16 = AssetTag.builder().asset(a6).category(c2).subCategory(sc6).tag(t4).build();
        AssetTag at17 = AssetTag.builder().asset(a6).category(c2).subCategory(sc6).tag(t5).build();
        AssetTag at18 = AssetTag.builder().asset(a6).category(c2).subCategory(sc6).tag(t6).build();
        AssetTag at19 = AssetTag.builder().asset(a7).category(c3).subCategory(sc7).tag(t1).build();
        AssetTag at20 = AssetTag.builder().asset(a7).category(c3).subCategory(sc7).tag(t2).build();
        AssetTag at21 = AssetTag.builder().asset(a7).category(c3).subCategory(sc7).tag(t3).build();
        AssetTag at22 = AssetTag.builder().asset(a8).category(c3).subCategory(sc8).tag(t4).build();
        AssetTag at23 = AssetTag.builder().asset(a8).category(c3).subCategory(sc8).tag(t5).build();
        AssetTag at24 = AssetTag.builder().asset(a8).category(c3).subCategory(sc8).tag(t6).build();
        AssetTag at25 = AssetTag.builder().asset(a9).category(c3).subCategory(sc9).tag(t1).build();
        AssetTag at26 = AssetTag.builder().asset(a9).category(c3).subCategory(sc9).tag(t2).build();
        AssetTag at27 = AssetTag.builder().asset(a9).category(c3).subCategory(sc9).tag(t3).build();
        AssetTag at28 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t6).build();
        assetTagRepository.saveAll(Arrays.asList(at1, at2, at3, at4, at5, at6, at7, at8, at9, at10, at11, at12,
                at13, at14, at15, at16, at17, at18, at19, at20, at21, at22, at23, at24, at25, at26, at27, at28));

        WishList w1 = WishList.builder().asset(a1).user(u1).build();
        WishList w2 = WishList.builder().asset(a3).user(u1).build();
        WishList w3 = WishList.builder().asset(a3).user(u2).build();
        WishList w4 = WishList.builder().asset(a4).user(u2).build();
        WishList w5 = WishList.builder().asset(a7).user(u1).build();
        WishList w6 = WishList.builder().asset(a7).user(u2).build();
        WishList w7 = WishList.builder().asset(a7).user(u3).build();
        WishList w8 = WishList.builder().asset(a8).user(u3).build();
        WishList w9 = WishList.builder().asset(a1).user(u4).build();
        wishListRepository.saveAll(Arrays.asList(w1, w2, w3, w4, w5, w6, w7, w8, w9));

        MyAsset m1 = MyAsset.builder().asset(a1).user(u1).build();
        MyAsset m2 = MyAsset.builder().asset(a3).user(u1).build();
        MyAsset m3 = MyAsset.builder().asset(a5).user(u1).build();
        MyAsset m4 = MyAsset.builder().asset(a7).user(u1).build();
        MyAsset m5 = MyAsset.builder().asset(a1).user(u2).build();
        MyAsset m6 = MyAsset.builder().asset(a3).user(u2).build();
        MyAsset m7 = MyAsset.builder().asset(a1).user(u3).build();
        MyAsset m8 = MyAsset.builder().asset(a1).user(u4).build();
        myAssetRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7, m8));

        Review r1 = Review.builder().rating(4D).content("만족").asset(a1).user(u1).build();
        Review r2 = Review.builder().rating(3D).content("평범").asset(a1).user(u2).build();
        Review r3 = Review.builder().rating(5D).content("완전만족").asset(a1).user(u3).build();
        Review r4 = Review.builder().rating(5D).content("완전만족").asset(a3).user(u2).build();
        reviewRepository.saveAll(Arrays.asList(r1, r2, r3, r4));

        em.clear();
    }

    @DisplayName("리뷰보기 비로그인유저 성공")
    @Test
    public void get_reviews_test() throws Exception {
        // given
        Long id = 1L; // 에셋 id

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{id}/reviews",id).with(anonymous()));
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
    @WithUserDetails(value = "user4@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_reviews_with_user_test() throws Exception {
        // given
        Long id = 1L; // 에셋 id

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{id}/reviews",id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.hasAsset").value(true))
                .andExpect(jsonPath("$.data.hasReview").value(false));
    }

    @DisplayName("리뷰작성 성공")
    @WithUserDetails(value = "user4@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_review_test() throws Exception {
        // given
        Long id = 1L; // 에셋 id
        Long userId = 4L;
        ReviewRequest.ReviewInDTO addReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 1D, "테스트입니다.");

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/s/assets/{id}/reviews",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(addReviewInDTO)));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        Asset assetPS = assetRepository.findById(id).orElseThrow(
                () -> new Exception400("id", "잘못된 요청")
        );
        assertEquals(4L, assetPS.getReviewCount());
        System.out.println("ReviewCount: "+assetPS.getReviewCount());
        System.out.println("Asset Rating: "+assetPS.getRating());

    }

    @DisplayName("리뷰작성 실패 에셋구매X")
    @WithUserDetails(value = "user4@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_review_fail_hasAsset_false_test() throws Exception {
        // given
        Long id = 2L; // 에셋 id
        Long userId = 4L;
        ReviewRequest.ReviewInDTO addReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 4D, "테스트입니다.");

        // when
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
                .andExpect(jsonPath("$.data").value("이 에셋을 구매하지 않았습니다."));


    }

    @DisplayName("리뷰작성 실패 이미 리뷰 쓴 경우")
    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_review_fail_hasReview_true_test() throws Exception {
        // given
        Long id = 1L; // 에셋 id
        Long userId = 3L;
        ReviewRequest.ReviewInDTO addReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 4D, "테스트입니다.");

        // when
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
                .andExpect(jsonPath("$.data").value("이미 이 에셋의 리뷰를 작성하셨습니다."));
    }

    @DisplayName("리뷰수정 성공")
    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_review_test() throws Exception {
        // given
        Long assetId = 1L; // 에셋 id
        Long reviewId = 3L;
        Long userId = 3L;
        ReviewRequest.ReviewInDTO updateReviewInDTO =
                new ReviewRequest.ReviewInDTO(userId, 1D, "테스트입니다.");

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/s/assets/{assetid}/reviews/{reviewId}", assetId, reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateReviewInDTO)));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));

        Asset assetPS = assetRepository.findById(assetId).orElseThrow(
                () -> new Exception400("assetId", "잘못된 요청입니다. ")
        );
        assertEquals(3L, assetPS.getReviewCount()); // 수정 후 ReviewCount는 변하지 않아야 한다.
        System.out.println("ReviewCount: "+assetPS.getReviewCount());
        System.out.println("Asset Rating: "+assetPS.getRating());

    }

    @DisplayName("리뷰삭제 성공")
    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_review_test() throws Exception {
        // given
        Long assetId = 1L; // 에셋 id
        Long reviewId = 3L;
        Long userId = 3L;
        ReviewRequest.DeleteReviewInDTO deleteReviewInDTO =
                new ReviewRequest.DeleteReviewInDTO(userId);

        // when
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
        assertEquals(2L, assetPS.getReviewCount()); // 삭제 후 ReviewCount는 1 줄어든다.
        assertEquals(3.5D, assetPS.getRating());
        System.out.println("ReviewCount: "+assetPS.getReviewCount());
        System.out.println("Asset Rating: "+assetPS.getRating());

    }
}
