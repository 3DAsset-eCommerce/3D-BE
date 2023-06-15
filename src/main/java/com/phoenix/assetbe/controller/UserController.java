package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.user.UserRequest;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.phoenix.assetbe.dto.user.UserResponse.CodeCheckOutDTO;
import com.phoenix.assetbe.dto.user.UserResponse.CodeOutDTO;
import com.phoenix.assetbe.dto.user.UserResponse.EmailCheckOutDTO;
import com.phoenix.assetbe.dto.user.UserResponse.LoginOutDTO;
import com.phoenix.assetbe.dto.user.UserResponse.LoginWithJWTOutDTO;
import com.phoenix.assetbe.dto.user.UserResponse.PasswordChangeOutDTO;
import com.phoenix.assetbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors){
        LoginWithJWTOutDTO loginWithJWTOutDTO = userService.loginService(loginInDTO);
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, loginWithJWTOutDTO.getJwt()).body(new ResponseDTO<>(new LoginOutDTO(loginWithJWTOutDTO.getId())));
    }

    @PostMapping("/login/send")
    public ResponseEntity<?> verifyingCodeSend(@RequestBody @Valid UserRequest.CodeInDTO codeInDTO, Errors errors){
        CodeOutDTO codeOutDTO = userService.codeSendService(codeInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(codeOutDTO));
    }

    @PostMapping("/login/check")
    public ResponseEntity<?> verifyingCodeCheck(@RequestBody @Valid UserRequest.CodeCheckInDTO codeCheckInDTO, Errors errors){
        CodeCheckOutDTO codeCheckOutDTO = userService.codeCheckService(codeCheckInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(codeCheckOutDTO));
    }

    @PostMapping("/login/change")
    public ResponseEntity<?> passwordChange(@RequestBody @Valid UserRequest.PasswordChangeInDTO passwordChangeInDTO, Errors errors){
        PasswordChangeOutDTO passwordChangeOutDTO = userService.passwordChangeService(passwordChangeInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(passwordChangeOutDTO));
    }

    @PostMapping("/signup/duplicate")
    public ResponseEntity<?> emailIsDuplicate(@RequestBody @Valid UserRequest.EmailCheckInDTO emailCheckInDTO, Errors errors){
        EmailCheckOutDTO emailCheckOutDTO = userService.emailCheckService(emailCheckInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(emailCheckOutDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserRequest.SignupInDTO signupInDTO, Errors errors) {
        UserResponse.SignupOutDTO signupOutDTO = userService.signupService(signupInDTO);
        return ResponseEntity.ok(new ResponseDTO<>(signupOutDTO));
    }

    /**
     * 마이페이지
     */
    @PostMapping("/s/user/check")
    public ResponseEntity<?> checkPassword(@RequestBody @Valid UserRequest.CheckPasswordInDTO checkPasswordInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails);
        return ResponseEntity.ok(new ResponseDTO<>(null));
    }

    @PostMapping("/s/user/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody @Valid UserRequest.WithdrawInDTO withdrawInDTO, Errors errors,
                                      @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdrawService(id, withdrawInDTO, myUserDetails);
        return ResponseEntity.ok(new ResponseDTO<>(null));
    }

    @PostMapping("/s/user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UserRequest.UpdateInDTO updateInDTO, Errors errors,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateService(id, updateInDTO, myUserDetails);
        return ResponseEntity.ok(new ResponseDTO<>(null));
    }

    @GetMapping("/s/user/{id}")
    public ResponseEntity<?> getMyInfo(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.GetMyInfoOutDTO getMyInfoOutDTO = userService.getMyInfoService(id, myUserDetails);
        return ResponseEntity.ok(new ResponseDTO<>(getMyInfoOutDTO));
    }


    /**
     * 나의 에셋
     */
    @GetMapping("/s/user/{id}/assets")
    public ResponseEntity<?> getMyAssetList(@PathVariable Long id,
                                            @PageableDefault(size = 14, page = 0, sort = "assetName", direction = Sort.Direction.DESC) Pageable pageable,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyAssetListOutDTO myAssetListOutDTO = userService.getMyAssetListService(pageable, id, myUserDetails);
        return ResponseEntity.ok(new ResponseDTO<>(myAssetListOutDTO));
    }

//    @GetMapping("/s/user/{id}/assets/search")
//    public ResponseEntity<?> searchMyAsset(@PathVariable Long id,
//                                           @PageableDefault(size = 14, page = 0, sort = "") {
//
//        return ResponseEntity.ok(new ResponseDTO<>());
//    }
}