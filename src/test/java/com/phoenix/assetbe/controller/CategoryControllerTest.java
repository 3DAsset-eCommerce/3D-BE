package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.model.asset.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("카테고리 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AssetTagRepository assetTagRepository;

    @DisplayName("카테고리별 이름,에셋수,태그리스트")
    @Test
    public void get_category_list_test() throws Exception {
        //given
        Asset asset1 = Asset.builder().assetName("a").build();
        Asset asset2 = Asset.builder().assetName("b").build();
        Asset asset3 = Asset.builder().assetName("c").build();
        Asset asset4 = Asset.builder().assetName("d").build();
        Asset asset5 = Asset.builder().assetName("e").build();
        Asset asset6 = Asset.builder().assetName("f").build();
        Asset asset7 = Asset.builder().assetName("g").build();
        Asset asset8 = Asset.builder().assetName("h").build();
        Asset asset9 = Asset.builder().assetName("i").build();
        assetRepository.save(asset1);
        assetRepository.save(asset2);
        assetRepository.save(asset3);
        assetRepository.save(asset4);
        assetRepository.save(asset5);
        assetRepository.save(asset6);
        assetRepository.save(asset7);
        assetRepository.save(asset8);
        assetRepository.save(asset9);
        Category category1 = Category.builder().categoryName("A").categoryCount(500L).build();
        Category category2 = Category.builder().categoryName("B").categoryCount(600L).build();
        Category category3 = Category.builder().categoryName("C").categoryCount(700L).build();
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        SubCategory subCategory1 = SubCategory.builder().subCategoryName("AA").subCategoryCount(100L).build();
        SubCategory subCategory2 = SubCategory.builder().subCategoryName("AB").subCategoryCount(110L).build();
        SubCategory subCategory3 = SubCategory.builder().subCategoryName("AC").subCategoryCount(120L).build();
        SubCategory subCategory4 = SubCategory.builder().subCategoryName("BA").subCategoryCount(130L).build();
        SubCategory subCategory5 = SubCategory.builder().subCategoryName("BB").subCategoryCount(140L).build();
        SubCategory subCategory6 = SubCategory.builder().subCategoryName("BC").subCategoryCount(150L).build();
        SubCategory subCategory7 = SubCategory.builder().subCategoryName("CA").subCategoryCount(160L).build();
        SubCategory subCategory8 = SubCategory.builder().subCategoryName("CB").subCategoryCount(170L).build();
        SubCategory subCategory9 = SubCategory.builder().subCategoryName("CC").subCategoryCount(180L).build();
        subCategoryRepository.save(subCategory1);
        subCategoryRepository.save(subCategory2);
        subCategoryRepository.save(subCategory3);
        subCategoryRepository.save(subCategory4);
        subCategoryRepository.save(subCategory5);
        subCategoryRepository.save(subCategory6);
        subCategoryRepository.save(subCategory7);
        subCategoryRepository.save(subCategory8);
        subCategoryRepository.save(subCategory9);

        Tag tag1 = Tag.builder().tagName("tag1").tagCount(300L).build();
        Tag tag2 = Tag.builder().tagName("tag2").tagCount(300L).build();
        Tag tag3 = Tag.builder().tagName("tag3").tagCount(300L).build();
        Tag tag4 = Tag.builder().tagName("tag4").tagCount(300L).build();
        Tag tag5 = Tag.builder().tagName("tag5").tagCount(300L).build();
        Tag tag6 = Tag.builder().tagName("tag6").tagCount(300L).build();
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
        tagRepository.save(tag4);
        tagRepository.save(tag5);
        tagRepository.save(tag6);

        AssetTag assetTag1 = AssetTag.builder().asset(asset1).category(category1).subCategory(subCategory1).tag(tag1).build();
        AssetTag assetTag2 = AssetTag.builder().asset(asset1).category(category1).subCategory(subCategory1).tag(tag2).build();
        AssetTag assetTag3 = AssetTag.builder().asset(asset1).category(category1).subCategory(subCategory1).tag(tag3).build();
        AssetTag assetTag4 = AssetTag.builder().asset(asset2).category(category1).subCategory(subCategory2).tag(tag4).build();
        AssetTag assetTag5 = AssetTag.builder().asset(asset2).category(category1).subCategory(subCategory2).tag(tag5).build();
        AssetTag assetTag6 = AssetTag.builder().asset(asset2).category(category1).subCategory(subCategory2).tag(tag6).build();
        AssetTag assetTag7 = AssetTag.builder().asset(asset3).category(category1).subCategory(subCategory3).tag(tag1).build();
        AssetTag assetTag8 = AssetTag.builder().asset(asset3).category(category1).subCategory(subCategory3).tag(tag2).build();
        AssetTag assetTag9 = AssetTag.builder().asset(asset3).category(category1).subCategory(subCategory3).tag(tag3).build();
        AssetTag assetTag10 = AssetTag.builder().asset(asset4).category(category2).subCategory(subCategory4).tag(tag4).build();
        AssetTag assetTag11 = AssetTag.builder().asset(asset4).category(category2).subCategory(subCategory4).tag(tag5).build();
        AssetTag assetTag12 = AssetTag.builder().asset(asset4).category(category2).subCategory(subCategory4).tag(tag6).build();
        AssetTag assetTag13 = AssetTag.builder().asset(asset5).category(category2).subCategory(subCategory5).tag(tag1).build();
        AssetTag assetTag14 = AssetTag.builder().asset(asset5).category(category2).subCategory(subCategory5).tag(tag2).build();
        AssetTag assetTag15 = AssetTag.builder().asset(asset5).category(category2).subCategory(subCategory5).tag(tag3).build();
        AssetTag assetTag16 = AssetTag.builder().asset(asset6).category(category2).subCategory(subCategory6).tag(tag4).build();
        AssetTag assetTag17 = AssetTag.builder().asset(asset6).category(category2).subCategory(subCategory6).tag(tag5).build();
        AssetTag assetTag18 = AssetTag.builder().asset(asset6).category(category2).subCategory(subCategory6).tag(tag6).build();
        AssetTag assetTag19 = AssetTag.builder().asset(asset7).category(category3).subCategory(subCategory7).tag(tag1).build();
        AssetTag assetTag20 = AssetTag.builder().asset(asset7).category(category3).subCategory(subCategory7).tag(tag2).build();
        AssetTag assetTag21 = AssetTag.builder().asset(asset7).category(category3).subCategory(subCategory7).tag(tag3).build();
        AssetTag assetTag22 = AssetTag.builder().asset(asset8).category(category3).subCategory(subCategory8).tag(tag4).build();
        AssetTag assetTag23 = AssetTag.builder().asset(asset8).category(category3).subCategory(subCategory8).tag(tag5).build();
        AssetTag assetTag24 = AssetTag.builder().asset(asset8).category(category3).subCategory(subCategory8).tag(tag6).build();
        AssetTag assetTag25 = AssetTag.builder().asset(asset9).category(category3).subCategory(subCategory9).tag(tag1).build();
        AssetTag assetTag26 = AssetTag.builder().asset(asset9).category(category3).subCategory(subCategory9).tag(tag2).build();
        AssetTag assetTag27 = AssetTag.builder().asset(asset9).category(category3).subCategory(subCategory9).tag(tag3).build();
        assetTagRepository.save(assetTag1);
        assetTagRepository.save(assetTag2);
        assetTagRepository.save(assetTag3);
        assetTagRepository.save(assetTag4);
        assetTagRepository.save(assetTag5);
        assetTagRepository.save(assetTag6);
        assetTagRepository.save(assetTag7);
        assetTagRepository.save(assetTag8);
        assetTagRepository.save(assetTag9);
        assetTagRepository.save(assetTag10);
        assetTagRepository.save(assetTag11);
        assetTagRepository.save(assetTag12);
        assetTagRepository.save(assetTag13);
        assetTagRepository.save(assetTag14);
        assetTagRepository.save(assetTag15);
        assetTagRepository.save(assetTag16);
        assetTagRepository.save(assetTag17);
        assetTagRepository.save(assetTag18);
        assetTagRepository.save(assetTag19);
        assetTagRepository.save(assetTag20);
        assetTagRepository.save(assetTag21);
        assetTagRepository.save(assetTag22);
        assetTagRepository.save(assetTag23);
        assetTagRepository.save(assetTag24);
        assetTagRepository.save(assetTag25);
        assetTagRepository.save(assetTag26);
        assetTagRepository.save(assetTag27);


        entityManager.clear();

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/assets/count"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }
}
