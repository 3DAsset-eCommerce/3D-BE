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
        AdminResponse.GetCategoryListOutDTO getCategoryListOutDTO = adminService.getCategoryListService();
        ResponseDTO<?> responseDTO = new ResponseDTO<>(getCategoryListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 서브 카테고리
     */
    @GetMapping("/s/admin/{categoryName}/subcategory")
    public ResponseEntity<?> getSubCategoryList(@PathVariable String categoryName) {
        AdminResponse.GetSubCategoryListOutDTO getSubCategoryListOutDTO = adminService.getSubCategoryListService(categoryName);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(getSubCategoryListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 에셋
     */
    @PostMapping("/s/admin/asset/inactive")
    public ResponseEntity<?> inactiveAsset(@RequestBody AdminRequest.InactiveAssetInDTO inactiveAssetInDTO) {
        adminService.inactiveAssetService(inactiveAssetInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }
}