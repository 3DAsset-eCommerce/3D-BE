package com.phoenix.assetbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.asset.Category;
import com.phoenix.assetbe.model.asset.MyAssetQueryRepository;
import com.phoenix.assetbe.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("어드민 서비스 TEST")
public class AdminServiceTest extends DummyEntity {

    private AdminService adminService;
    @Mock
    private CategoryService categoryService;
    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminService(categoryService);
    }


    @Test
    public void testGetCategoryListService() throws Exception {
        // given
        List<Category> mockCategoryList = new ArrayList<>();

        // stub 1
        when(categoryService.getCategoryList()).thenReturn(mockCategoryList);

        // when
        AdminResponse.CategoryOutDTO result = adminService.getCategoryListService();

        // then
        verify(categoryService, times(1)).getCategoryList();
    }
}
