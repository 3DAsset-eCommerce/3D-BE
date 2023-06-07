package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserInDTO.LoginInDTO loginInDTO){
        String jwt = userService.loginService(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(responseDTO);
    }
}
