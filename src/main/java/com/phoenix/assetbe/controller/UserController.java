package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.dto.UserOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeCheckOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.PasswordChangeOutDTO;
import com.phoenix.assetbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserInDTO.LoginInDTO loginInDTO, Errors errors){
        String jwt = userService.loginService(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(responseDTO);
    }

    @PostMapping("/send")
    public ResponseEntity<?> verifyingCodeSend(@RequestBody @Valid UserInDTO.CodeInDTO codeInDTO, Errors errors){
        CodeOutDTO codeOutDTO = userService.codeSending(codeInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(codeOutDTO));
    }

    @PostMapping("/check")
    public ResponseEntity<?> verifyingCodeCheck(@RequestBody @Valid UserInDTO.CodeCheckInDTO codeCheckInDTO, Errors errors){
        CodeCheckOutDTO codeCheckOutDTO = userService.codeChecking(codeCheckInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(codeCheckOutDTO));
    }

    @PostMapping("/login/change")
    public ResponseEntity<?> passwordChange(@RequestBody @Valid UserInDTO.PasswordChangeInDTO passwordChangeInDTO, Errors errors){
        PasswordChangeOutDTO passwordChangeOutDTO = userService.passwordChanging(passwordChangeInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(passwordChangeOutDTO));
    }
}
