package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception401;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.user.UserRequest;
import com.phoenix.assetbe.dto.user.UserRequest.CodeCheckInDTO;
import com.phoenix.assetbe.dto.user.UserRequest.EmailCheckInDTO;
import com.phoenix.assetbe.dto.user.UserRequest.PasswordChangeInDTO;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.phoenix.assetbe.model.asset.MyAssetQueryRepository;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final MyAssetQueryRepository myAssetQueryRepository;

    private final AssetService assetService;

    /**
     * 로그인
     */
    public UserResponse.LoginOutDTOWithJWT loginService(UserRequest.LoginInDTO loginInDTO) {
        User userPS = findValidUserByEmail(loginInDTO.getEmail());

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            return new UserResponse.LoginOutDTOWithJWT(myUserDetails.getUser().getId(), MyJwtProvider.create(myUserDetails.getUser()));
        }catch (Exception e){
            throw new Exception401("아이디 혹은 비밀번호를 확인해주세요. ");
        }
    }

    @Transactional
    public void verifyingCodeSendService(UserRequest.CodeSendInDTO codeSendInDTO){
        User userPS = findValidUserByEmail(codeSendInDTO.getEmail());
        userPS.generateEmailCheckToken();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userPS.getEmail());
        mailMessage.setSubject("3D 에셋 스토어, 비밀번호 재설정을 위한 이메일 인증");
        mailMessage.setText(userPS.getEmailCheckToken());
        javaMailSender.send(mailMessage);
    }

    @Transactional
    public void verifyingCodeCheckService(CodeCheckInDTO codeCheckInDTO) {
        User userPS = findValidUserByEmail(codeCheckInDTO.getEmail());

        LocalDateTime emailTokenCreatedAt = userPS.getEmailCheckTokenCreatedAt();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime tenMinutesLater = emailTokenCreatedAt.plusMinutes(10);

        if(currentTime.isBefore(tenMinutesLater)) {
            if (!userPS.getEmailCheckToken().equals(codeCheckInDTO.getCode())){
                throw new Exception400("code", "잘못된 인증코드 입니다. ");
            }
        }else{
            throw new Exception400("code", "유효하지 않은 인증코드 입니다. ");
        }
    }

    @Transactional
    public void passwordChangeService(PasswordChangeInDTO passwordChangeInDTO) {
        User userPS = findValidUserByEmail(passwordChangeInDTO.getEmail());

        if (passwordChangeInDTO.getCode() == null) {
            throw new Exception400("code", "인증코드를 입력해주세요. ");
        }

        if (userPS.getEmailCheckToken().equals(passwordChangeInDTO.getCode())) {
            userPS.changePassword(passwordEncoder.encode(passwordChangeInDTO.getPassword()));

        }else{
            throw new Exception400("code", "잘못된 인증코드 입니다. ");
        }
    }

    /**
     * 회원가입
     */
    public void emailDuplicateCheckService(EmailCheckInDTO emailCheckInDTO) {
        boolean emailExist = existsUserByEmail(emailCheckInDTO.getEmail());
        if (emailExist){
            throw new Exception400("email", "이미 존재하는 이메일입니다. ");
        }
    }

    @Transactional
    public void signupService(UserRequest.SignupInDTO signupInDTO) {
        boolean exists = existsUserByEmail(signupInDTO.getEmail());
        if (exists){
            throw new Exception400("email", "이미 존재하는 이메일입니다. ");
        }

        User userPS = signupInDTO.toEntity();
        userPS.changePassword(userPS.getPassword());

        try {
            userRepository.save(signupInDTO.toEntity());
        } catch (Exception e) {
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }
    }

    /**
     * 마이페이지
     */
    public void checkPasswordService(UserRequest.CheckPasswordInDTO checkPasswordInDTO, MyUserDetails myUserDetails) {
        Long userId = checkPasswordInDTO.getUserId();
        authCheck(myUserDetails, checkPasswordInDTO.getUserId());
        User userPS = findUserById(userId);
        if (!passwordEncoder.matches(checkPasswordInDTO.getPassword(), userPS.getPassword())) {
            throw new Exception400("password", "비밀번호가 일치하지 않습니다. ");
        }
    }

    @Transactional
    public void withdrawService(Long userId, UserRequest.WithdrawInDTO withdrawInDTO, MyUserDetails myUserDetails) {
        authCheck(myUserDetails, userId);
        User userPS = findUserById(userId);
        if (withdrawInDTO.isDeleteConfirm()) { // true 상태
            throw new Exception400("deleteConfirm", "이미 탈퇴되어 있습니다. ");
        }
        userPS.changeStatus();
        userPS.changeWithdrawalMassage(withdrawInDTO.getMessage());

        try {
            userRepository.save(userPS);
        } catch (Exception e) {
            throw new Exception500("회원탈퇴 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public void updateService(Long userId, UserRequest.UpdateInDTO updateInDTO, MyUserDetails myUserDetails) {
        authCheck(myUserDetails, userId);
        User userPS = findUserById(userId);

        userPS.changePassword(passwordEncoder.encode(updateInDTO.getNewPassword()));
        try {
            userRepository.save(userPS);
        } catch (Exception e) {
            throw new Exception500("회원정보 수정 실패 : " + e.getMessage());
        }
    }

    public UserResponse.GetMyInfoOutDTO getMyInfoService(MyUserDetails myUserDetails) {
        User userPS = findUserById(myUserDetails.getUser().getId());
        return new UserResponse.GetMyInfoOutDTO(userPS);
    }

    /**
     * 나의 에셋
     */
    public UserResponse.MyAssetListOutDTO getMyAssetListService(Long userId, Pageable pageable, MyUserDetails myUserDetails) {
        authCheck(myUserDetails, userId);
        findUserById(userId);
        Page<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> getMyAssetOutDTO = myAssetQueryRepository.getMyAssetListWithUserIdAndPaging(userId, pageable);
        return new UserResponse.MyAssetListOutDTO(getMyAssetOutDTO);
    }

    public UserResponse.MyAssetListOutDTO searchMyAssetService(Long userId, List<String> keywordList, Pageable pageable, MyUserDetails myUserDetails) {
        authCheck(myUserDetails, userId);
        findUserById(userId);
        Page<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> getMyAssetOutDTO = myAssetQueryRepository.searchMyAssetListWithUserIdAndPagingAndKeyword(userId, keywordList, pageable);
        return new UserResponse.MyAssetListOutDTO(getMyAssetOutDTO);
    }

    public UserResponse.DownloadMyAssetListOutDTO downloadMyAssetService(UserRequest.DownloadMyAssetInDTO downloadMyAssetInDTO, MyUserDetails myUserDetails) {
        Long userId = downloadMyAssetInDTO.getUserId();
        authCheck(myUserDetails, userId);
        findUserById(userId);
        myAssetQueryRepository.validateMyAssets(userId, downloadMyAssetInDTO.getAssets());
        List<UserResponse.DownloadMyAssetListOutDTO.MyAssetFileUrlOutDTO> myAssetFileUrlOutDTO = myAssetQueryRepository.downloadMyAssetByAssetId(downloadMyAssetInDTO.getAssets());
        return new UserResponse.DownloadMyAssetListOutDTO(myAssetFileUrlOutDTO);
    }

    /**
     * 공통 메소드
     */
    // 요청한 사용자가 id의 주인인지 확인하는 공통 메소드
    public User findUserById(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 유저입니다. ")
        );
        System.out.println("출력됨: " + userPS.getEmail());
        return userPS;
    }

    // 요청한 사용자가 email의 주인인지 확인하는 공통 메소드
    public User findValidUserByEmail(String email) {
        User userPS = userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE).orElseThrow(
                () -> new Exception400("email", "존재하지 않는 유저입니다. ")
        );
        return userPS;
    }

    // 요청한 사용자 email이 존재하는지 확인하는 공통 메소드
    public boolean existsUserByEmail(String email) {
        boolean emailExist = userRepository.existsByEmailAndStatus(email, Status.ACTIVE);
        return emailExist;
    }

    // 요청한 사용자가 권한이 있는지 확인하는 공통 메소드
    public void authCheck(MyUserDetails myUserDetails, Long userId){
        if (!myUserDetails.getUser().getId().equals(userId)) {
            throw new Exception403("권한이 없습니다. ");
        }
    }
}