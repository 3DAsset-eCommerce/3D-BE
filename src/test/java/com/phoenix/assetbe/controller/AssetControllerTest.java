package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.*;
import com.phoenix.assetbe.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("에셋 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class AssetControllerTest {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetService assetService;

    @Autowired
    private PreviewRepository previewRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);
    }
}
