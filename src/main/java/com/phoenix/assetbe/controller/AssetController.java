package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/assets/count")
    public ResponseEntity<?> countByCategory(){
        AssetResponse.CountOutDTO countOutDTO = assetService.countByCategory();
        ResponseDTO<?> responseDTO = new ResponseDTO<>(countOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}
