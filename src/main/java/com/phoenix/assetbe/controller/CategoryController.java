package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/assets/count")
    public ResponseEntity<?> getCategoryList() {
        AssetResponse.CategoryOutDTO categoryOutDTO = categoryService.getCategoryList();
        ResponseDTO<?> responseDTO = new ResponseDTO<>(categoryOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}
