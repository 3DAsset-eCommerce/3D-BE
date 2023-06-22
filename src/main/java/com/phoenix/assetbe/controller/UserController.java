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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors){
        UserResponse.LoginWithJWTOutDTO loginWithJWTOutDTO = userService.loginService(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(new LoginOutDTO(loginWithJWTOutDTO.getId()));
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, loginWithJWTOutDTO.getJwt()).body(responseDTO);
    }

    @PostMapping("/login/send")
    public ResponseEntity<?> verifyingCodeSend(@RequestBody @Valid UserRequest.CodeInDTO codeInDTO, Errors errors){
        UserResponse.CodeOutDTO codeOutDTO = userService.codeSendService(codeInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(codeOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/login/check")
    public ResponseEntity<?> verifyingCodeCheck(@RequestBody @Valid UserRequest.CodeCheckInDTO codeCheckInDTO, Errors errors){
        UserResponse.CodeCheckOutDTO codeCheckOutDTO = userService.codeCheckService(codeCheckInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(codeCheckOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/login/change")
    public ResponseEntity<?> passwordChange(@RequestBody @Valid UserRequest.PasswordChangeInDTO passwordChangeInDTO, Errors errors){
        UserResponse.PasswordChangeOutDTO passwordChangeOutDTO = userService.passwordChangeService(passwordChangeInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(passwordChangeOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup/duplicate")
    public ResponseEntity<?> emailIsDuplicate(@RequestBody @Valid UserRequest.EmailCheckInDTO emailCheckInDTO, Errors errors){
        UserResponse.EmailCheckOutDTO emailCheckOutDTO = userService.emailCheckService(emailCheckInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(emailCheckOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserRequest.SignupInDTO signupInDTO, Errors errors) {
        UserResponse.SignupOutDTO signupOutDTO = userService.signupService(signupInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(signupOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 마이페이지
     */
    @PostMapping("/s/user/check")
    public ResponseEntity<?> checkPassword(@RequestBody UserRequest.CheckPasswordInDTO checkPasswordInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody UserRequest.WithdrawInDTO withdrawInDTO, Errors errors,
                                      @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdrawService(id, withdrawInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserRequest.UpdateInDTO updateInDTO, Errors errors,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateService(id, updateInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/user")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.GetMyInfoOutDTO getMyInfoOutDTO = userService.getMyInfoService(myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(getMyInfoOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 나의 에셋
     */
    @GetMapping("/s/user/{id}/assets")
    public ResponseEntity<?> getMyAssetList(@PathVariable Long id,
                                            @PageableDefault(size = 14, page = 0, sort = "assetName", direction = Sort.Direction.ASC) Pageable pageable,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyAssetListOutDTO myAssetListOutDTO = userService.getMyAssetListService(id, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(myAssetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/user/{id}/assets/search")
    public ResponseEntity<?> searchMyAsset(@PathVariable Long id,
                                           @RequestParam(value = "keyword") List<String> keywordList,
                                           @PageableDefault(size = 14, page = 0) Pageable pageable,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyAssetListOutDTO myAssetListOutDTO = userService.searchMyAssetService(id, keywordList, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(myAssetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/user/download")
    public ResponseEntity<?> downloadMyAsset(@RequestBody UserRequest.DownloadMyAssetInDTO downloadMyAssetInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.DownloadMyAssetListOutDTO downloadMyAssetListOutDTO = userService.downloadMyAssetService(downloadMyAssetInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(downloadMyAssetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}