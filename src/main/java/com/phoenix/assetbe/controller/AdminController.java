package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.phoenix.assetbe.service.AdminService;
import com.phoenix.assetbe.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    private final S3Service s3Service;

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

    @PostMapping("/s/admin/asset/active")
    public ResponseEntity<?> activeAsset(@RequestBody AdminRequest.ActiveAssetInDTO activeAssetInDTO) {
        adminService.activeAssetService(activeAssetInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * S3 관련
     */

    @PostMapping("/s/admin/file/{type}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("type") String type) {
        UserResponse.uploadOutDTO uploadOutDTO = s3Service.upload(file, type);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(uploadOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/s/admin/delete/{removeFile}")
    public ResponseEntity<?> deleteFile(@PathVariable("removeFile") String removeFile) {
        s3Service.removeFile(removeFile);
        return ResponseEntity.ok().body(null);
    }

    /**
     * 관리자 에셋 조회
     */
    @GetMapping("/s/admin/assets")
    public ResponseEntity<?> getAssetListByAdmin(
            @RequestParam(value = "num", required = false) Long assetNumber,
            @RequestParam(value = "name", required = false) String assetName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "subcategory", required = false) String subCategoryName,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        AdminResponse.AssetListOutDTO assetListOutDTO =
                adminService.getAssetListByAdminService(assetNumber, assetName, status, categoryName, subCategoryName, pageable);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(assetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}