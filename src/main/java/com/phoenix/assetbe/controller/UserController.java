package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.annotation.MyLog;
import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.user.UserRequest;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.phoenix.assetbe.dto.user.UserResponse.LoginOutDTO;
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
    @MyLog
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors){
        UserResponse.LoginOutDTOWithJWT loginOutDTOWithJWT = userService.loginService(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(new LoginOutDTO(loginOutDTOWithJWT.getId()));
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, loginOutDTOWithJWT.getJwt()).body(responseDTO);
    }

    @MyLog
    @PostMapping("/login/send")
    public ResponseEntity<?> sendPasswordChangeCode(@RequestBody @Valid UserRequest.SendCodeInDTO sendCodeInDTO, Errors errors){
        UserResponse.SendCodeOutDTO sendCodeOutDTO = userService.sendPasswordChangeCodeService(sendCodeInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(sendCodeOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/login/check")
    public ResponseEntity<?> checkPasswordChangeCode(@RequestBody @Valid UserRequest.CheckCodeInDTO CheckCodeInDTO, Errors errors){
        userService.checkPasswordChangeCodeService(CheckCodeInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/login/change")
    public ResponseEntity<?> changePassword(@RequestBody @Valid UserRequest.ChangePasswordInDTO changePasswordInDTO, Errors errors){
        userService.changePasswordService(changePasswordInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 회원가입
     */
    @MyLog
    @PostMapping("/signup/duplicate")
    public ResponseEntity<?> checkEmailDuplicate(@RequestBody @Valid UserRequest.CheckEmailInDTO checkEmailInDTO, Errors errors){
        userService.checkEmailDuplicateService(checkEmailInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/signup/send")
    public ResponseEntity<?> sendSignupCode(@RequestBody @Valid UserRequest.SendCodeInDTO sendCodeInDTO, Errors errors){
        UserResponse.SendCodeOutDTO sendCodeOutDTO = userService.sendSignupCodeService(sendCodeInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(sendCodeOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/signup/check")
    public ResponseEntity<?> checkSignupCode(@RequestBody @Valid UserRequest.CheckCodeInDTO CheckCodeInDTO, Errors errors){
        userService.checkSignupCodeService(CheckCodeInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserRequest.SignupInDTO signupInDTO, Errors errors) {
        userService.signupService(signupInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 마이페이지
     */
    @MyLog
    @PostMapping("/s/user/check")
    public ResponseEntity<?> checkPassword(@RequestBody UserRequest.CheckPasswordInDTO checkPasswordInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/s/user/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody UserRequest.WithdrawInDTO withdrawInDTO, Errors errors,
                                      @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdrawService(id, withdrawInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/s/user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserRequest.UpdateInDTO updateInDTO, Errors errors,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateService(id, updateInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @GetMapping("/s/user")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.GetMyInfoOutDTO getMyInfoOutDTO = userService.getMyInfoService(myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(getMyInfoOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 나의 에셋
     */
    @MyLog
    @GetMapping("/s/user/{id}/assets")
    public ResponseEntity<?> getMyAssetList(@PathVariable Long id,
                                            @PageableDefault(size = 14, page = 0, sort = "assetName", direction = Sort.Direction.ASC) Pageable pageable,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyAssetListOutDTO myAssetListOutDTO = userService.getMyAssetListService(id, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(myAssetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @GetMapping("/s/user/{id}/assets/search")
    public ResponseEntity<?> searchMyAsset(@PathVariable Long id,
                                           @RequestParam(value = "keyword") List<String> keywordList,
                                           @PageableDefault(size = 14, page = 0) Pageable pageable,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyAssetListOutDTO myAssetListOutDTO = userService.searchMyAssetService(id, keywordList, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(myAssetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/s/user/download")
    public ResponseEntity<?> downloadMyAsset(@RequestBody UserRequest.DownloadMyAssetInDTO downloadMyAssetInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.DownloadMyAssetListOutDTO downloadMyAssetListOutDTO = userService.downloadMyAssetService(downloadMyAssetInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(downloadMyAssetListOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}