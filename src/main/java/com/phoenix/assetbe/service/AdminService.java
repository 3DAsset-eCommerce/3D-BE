package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.model.asset.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final CategoryService categoryService;

    public AdminResponse.CategoryOutDTO getCategoryListService(){
        List<Category> categoryList = categoryService.getCategoryList();
        AdminResponse.CategoryOutDTO categoryOutDTO = new AdminResponse.CategoryOutDTO(categoryList);
        return categoryOutDTO;
    }
}
