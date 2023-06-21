package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    /**
     * 카테고리
     */
    @GetMapping("/s/admin/category")
    public ResponseEntity<?> getCategoryList() {
        AdminResponse.CategoryOutDTO categoryOutDTO = adminService.getCategoryListService();
        ResponseDTO<?> responseDTO = new ResponseDTO<>(categoryOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

}