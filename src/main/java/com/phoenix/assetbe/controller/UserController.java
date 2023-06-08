package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.dto.UserOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeCheckOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.EmailCheckOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.PasswordChangeOutDTO;
import com.phoenix.assetbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login/send")
    public ResponseEntity<?> verifyingCodeSend(@RequestBody @Valid UserInDTO.CodeInDTO codeInDTO, Errors errors){
        CodeOutDTO codeOutDTO = userService.codeSendService(codeInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(codeOutDTO));
    }

    @PostMapping("/login/check")
    public ResponseEntity<?> verifyingCodeCheck(@RequestBody @Valid UserInDTO.CodeCheckInDTO codeCheckInDTO, Errors errors){
        CodeCheckOutDTO codeCheckOutDTO = userService.codeCheckService(codeCheckInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(codeCheckOutDTO));
    }

    @PostMapping("/login/change")
    public ResponseEntity<?> passwordChange(@RequestBody @Valid UserInDTO.PasswordChangeInDTO passwordChangeInDTO, Errors errors){
        PasswordChangeOutDTO passwordChangeOutDTO = userService.passwordChangeService(passwordChangeInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(passwordChangeOutDTO));
    }

    @PostMapping("/signup/duplicate")
    public ResponseEntity<?> emailIsDuplicate(@RequestBody @Valid UserInDTO.EmailCheckInDTO emailCheckInDTO, Errors errors){
        EmailCheckOutDTO emailCheckOutDTO = userService.emailCheckService(emailCheckInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(emailCheckOutDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserInDTO.SignupInDTO signupInDTO, Errors errors) {
        UserOutDTO.SignupOutDTO signupOutDTO = userService.signupService(signupInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(signupOutDTO));
    }

    @PostMapping("/s/user/check")
    public ResponseEntity<?> checkPassword(@RequestBody @Valid UserInDTO.CheckPasswordInDTO checkPasswordInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails.getUser().getId());
        return ResponseEntity.ok(new ResponseDTO<>(null));
    }

    @PostMapping("/s/user/{id}/withdrawal")
    public ResponseEntity<?> withdrawal(@PathVariable Long id, @RequestBody @Valid UserInDTO.WithdrawalInDTO withdrawalInDTO, Errors errors,
                                        @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdrawalService(id, withdrawalInDTO, myUserDetails.getUser().getId());
        return ResponseEntity.ok(new ResponseDTO<>(null));
    }

    @PostMapping("/s/user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UserInDTO.UpdateInDTO updateInDTO, Errors errors,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateService(id, updateInDTO, myUserDetails.getUser().getId());
        return ResponseEntity.ok(new ResponseDTO<>(null));
    }

    @GetMapping("/s/user/{id}")
    public ResponseEntity<?> findMyInfo(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserOutDTO.FindMyInfoOutDTO findMyInfoOutDTO = userService.findMyInfoService(id, myUserDetails.getUser().getId());
        return ResponseEntity.ok(new ResponseDTO<>(findMyInfoOutDTO));
    }
}