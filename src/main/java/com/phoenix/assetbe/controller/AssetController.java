package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/assets/{id}")
    public ResponseEntity<?> getAssetDetails(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AssetResponse.AssetDetailsOutDTO assetDetailsOutDTO;

        if (authentication.getPrincipal() == "anonymousUser" || authentication.getPrincipal() == "anonymous"
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {

            assetDetailsOutDTO = assetService.getAssetDetailsService(id);

        }else {

            String userEmail = authentication.getName();
            assetDetailsOutDTO = assetService.getAssetDetailsWithUserService(id, userEmail);

        }

        ResponseDTO<?> responseDTO = new ResponseDTO<>(assetDetailsOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}