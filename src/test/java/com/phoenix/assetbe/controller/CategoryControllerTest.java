//package com.phoenix.assetbe.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.phoenix.assetbe.model.asset.*;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//
//import java.util.Arrays;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@DisplayName("카테고리 컨트롤러 TEST")
//@ActiveProfiles("test")
//@Sql("classpath:db/teardown.sql")
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//@Transactional
//public class CategoryControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Autowired
//    private AssetRepository assetRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private SubCategoryRepository subCategoryRepository;
//
//    @Autowired
//    private TagRepository tagRepository;
//
//    @Autowired
//    private AssetTagRepository assetTagRepository;
//
//    @DisplayName("카테고리별 이름,에셋수,태그리스트")
//    @Test
//    public void get_category_list_test() throws Exception {
//        //given
//        Asset asset1 = Asset.builder().assetName("a").build();
//        Asset asset2 = Asset.builder().assetName("b").build();
//        Asset asset3 = Asset.builder().assetName("c").build();
//        Asset asset4 = Asset.builder().assetName("d").build();
//        Asset asset5 = Asset.builder().assetName("e").build();
//        Asset asset6 = Asset.builder().assetName("f").build();
//        Asset asset7 = Asset.builder().assetName("g").build();
//        Asset asset8 = Asset.builder().assetName("h").build();
//        Asset asset9 = Asset.builder().assetName("i").build();
//        assetRepository.saveAll(Arrays.asList(asset1,asset2,asset3,asset4,asset5,asset6,asset7,asset8,asset9));
//
//        Category category1 = Category.builder().categoryName("A").categoryCount(500L).build();
//        Category category2 = Category.builder().categoryName("B").categoryCount(600L).build();
//        Category category3 = Category.builder().categoryName("C").categoryCount(700L).build();
//        categoryRepository.saveAll(Arrays.asList(category1,category2,category3));
//
//        SubCategory subCategory1 = SubCategory.builder().subCategoryName("AA").subCategoryCount(100L).build();
//        SubCategory subCategory2 = SubCategory.builder().subCategoryName("AB").subCategoryCount(110L).build();
//        SubCategory subCategory3 = SubCategory.builder().subCategoryName("AC").subCategoryCount(120L).build();
//        SubCategory subCategory4 = SubCategory.builder().subCategoryName("BA").subCategoryCount(130L).build();
//        SubCategory subCategory5 = SubCategory.builder().subCategoryName("BB").subCategoryCount(140L).build();
//        SubCategory subCategory6 = SubCategory.builder().subCategoryName("BC").subCategoryCount(150L).build();
//        SubCategory subCategory7 = SubCategory.builder().subCategoryName("CA").subCategoryCount(160L).build();
//        SubCategory subCategory8 = SubCategory.builder().subCategoryName("CB").subCategoryCount(170L).build();
//        SubCategory subCategory9 = SubCategory.builder().subCategoryName("CC").subCategoryCount(180L).build();
//        subCategoryRepository.saveAll(Arrays.asList(subCategory1,subCategory2,subCategory3,subCategory4,subCategory5
//                ,subCategory6,subCategory7,subCategory8,subCategory9));
//
//        Tag tag1 = Tag.builder().tagName("tag1").tagCount(300L).build();
//        Tag tag2 = Tag.builder().tagName("tag2").tagCount(300L).build();
//        Tag tag3 = Tag.builder().tagName("tag3").tagCount(300L).build();
//        Tag tag4 = Tag.builder().tagName("tag4").tagCount(300L).build();
//        Tag tag5 = Tag.builder().tagName("tag5").tagCount(300L).build();
//        Tag tag6 = Tag.builder().tagName("tag6").tagCount(300L).build();
//        tagRepository.saveAll(Arrays.asList(tag1,tag2,tag3,tag4,tag5,tag6));
//
//        AssetTag assetTag1 = AssetTag.builder().asset(asset1).category(category1).subCategory(subCategory1).tag(tag1).build();
//        AssetTag assetTag2 = AssetTag.builder().asset(asset1).category(category1).subCategory(subCategory1).tag(tag2).build();
//        AssetTag assetTag3 = AssetTag.builder().asset(asset1).category(category1).subCategory(subCategory1).tag(tag3).build();
//        AssetTag assetTag4 = AssetTag.builder().asset(asset2).category(category1).subCategory(subCategory2).tag(tag4).build();
//        AssetTag assetTag5 = AssetTag.builder().asset(asset2).category(category1).subCategory(subCategory2).tag(tag5).build();
//        AssetTag assetTag6 = AssetTag.builder().asset(asset2).category(category1).subCategory(subCategory2).tag(tag6).build();
//        AssetTag assetTag7 = AssetTag.builder().asset(asset3).category(category1).subCategory(subCategory3).tag(tag1).build();
//        AssetTag assetTag8 = AssetTag.builder().asset(asset3).category(category1).subCategory(subCategory3).tag(tag2).build();
//        AssetTag assetTag9 = AssetTag.builder().asset(asset3).category(category1).subCategory(subCategory3).tag(tag3).build();
//        AssetTag assetTag10 = AssetTag.builder().asset(asset4).category(category2).subCategory(subCategory4).tag(tag4).build();
//        AssetTag assetTag11 = AssetTag.builder().asset(asset4).category(category2).subCategory(subCategory4).tag(tag5).build();
//        AssetTag assetTag12 = AssetTag.builder().asset(asset4).category(category2).subCategory(subCategory4).tag(tag6).build();
//        AssetTag assetTag13 = AssetTag.builder().asset(asset5).category(category2).subCategory(subCategory5).tag(tag1).build();
//        AssetTag assetTag14 = AssetTag.builder().asset(asset5).category(category2).subCategory(subCategory5).tag(tag2).build();
//        AssetTag assetTag15 = AssetTag.builder().asset(asset5).category(category2).subCategory(subCategory5).tag(tag3).build();
//        AssetTag assetTag16 = AssetTag.builder().asset(asset6).category(category2).subCategory(subCategory6).tag(tag4).build();
//        AssetTag assetTag17 = AssetTag.builder().asset(asset6).category(category2).subCategory(subCategory6).tag(tag5).build();
//        AssetTag assetTag18 = AssetTag.builder().asset(asset6).category(category2).subCategory(subCategory6).tag(tag6).build();
//        AssetTag assetTag19 = AssetTag.builder().asset(asset7).category(category3).subCategory(subCategory7).tag(tag1).build();
//        AssetTag assetTag20 = AssetTag.builder().asset(asset7).category(category3).subCategory(subCategory7).tag(tag2).build();
//        AssetTag assetTag21 = AssetTag.builder().asset(asset7).category(category3).subCategory(subCategory7).tag(tag3).build();
//        AssetTag assetTag22 = AssetTag.builder().asset(asset8).category(category3).subCategory(subCategory8).tag(tag4).build();
//        AssetTag assetTag23 = AssetTag.builder().asset(asset8).category(category3).subCategory(subCategory8).tag(tag5).build();
//        AssetTag assetTag24 = AssetTag.builder().asset(asset8).category(category3).subCategory(subCategory8).tag(tag6).build();
//        AssetTag assetTag25 = AssetTag.builder().asset(asset9).category(category3).subCategory(subCategory9).tag(tag1).build();
//        AssetTag assetTag26 = AssetTag.builder().asset(asset9).category(category3).subCategory(subCategory9).tag(tag2).build();
//        AssetTag assetTag27 = AssetTag.builder().asset(asset9).category(category3).subCategory(subCategory9).tag(tag3).build();
//        assetTagRepository.saveAll(Arrays.asList(assetTag1,assetTag2,assetTag3,assetTag4,assetTag5,assetTag6,assetTag7,assetTag8,assetTag9
//        ,assetTag10,assetTag11,assetTag12,assetTag13,assetTag14,assetTag15,assetTag16,assetTag17,assetTag18,assetTag19
//        ,assetTag20,assetTag21,assetTag22,assetTag23,assetTag24,assetTag25,assetTag26,assetTag27));
//
//        entityManager.clear();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/assets/count"));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        //then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.msg").value("성공"))
//                .andExpect(jsonPath("$.status").value(200));
//    }
//}
