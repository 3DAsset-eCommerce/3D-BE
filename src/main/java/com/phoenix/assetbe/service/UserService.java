package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception401;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.core.util.MailUtils;
import com.phoenix.assetbe.dto.user.UserRequest;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.phoenix.assetbe.model.asset.MyAssetQueryRepository;
import com.phoenix.assetbe.model.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
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

    public UserResponse.SendCodeOutDTO sendPasswordChangeCodeService(UserRequest.SendCodeInDTO sendCodeInDTO){
        User userPS = findValidUserByEmail(sendCodeInDTO.getEmail());
        if(!userPS.getFirstName().equals(sendCodeInDTO.getFirstName()) || !userPS.getLastName().equals(sendCodeInDTO.getLastName())){
            throw new Exception400("name", "잘못된 요청입니다. ");
        }
        userPS.generateEmailCheckToken();
        String html = createPasswordChangeHTML(userPS);

        try {
            MailUtils.send(userPS.getEmail(), "3D 에셋 스토어, 비밀번호 재설정을 위한 이메일 인증", html);
        } catch (MailException e) {
            throw new Exception500("이메일 전송 실패 : " + e.getMessage());
        }

        return new UserResponse.SendCodeOutDTO(userPS.getId());
    }

    @Transactional
    public void checkPasswordChangeCodeService(UserRequest.CheckCodeInDTO checkCodeInDTO) {
        User userPS = findValidUserByEmail(checkCodeInDTO.getEmail());

        LocalDateTime emailTokenCreatedAt = userPS.getEmailCheckTokenCreatedAt();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime tenMinutesLater = emailTokenCreatedAt.plusMinutes(10);

        if(currentTime.isBefore(tenMinutesLater)) {
            if (!userPS.getEmailCheckToken().equals(checkCodeInDTO.getCode())){
                throw new Exception400("code", "잘못된 인증코드 입니다. ");
            }
        }else{
            throw new Exception400("code", "유효하지 않은 인증코드 입니다. ");
        }
    }

    @Transactional
    public void changePasswordService(UserRequest.ChangePasswordInDTO changePasswordInDTO) {
        User userPS = findValidUserByEmail(changePasswordInDTO.getEmail());

        if (changePasswordInDTO.getCode() == null) {
            throw new Exception400("code", "인증코드를 입력해주세요. ");
        }

        if (userPS.getEmailCheckToken().equals(changePasswordInDTO.getCode())) {
            userPS.changePassword(passwordEncoder.encode(changePasswordInDTO.getPassword()));

        }else{
            throw new Exception400("code", "잘못된 인증코드 입니다. ");
        }
    }

    /**
     * 회원가입
     */
    public void checkEmailDuplicateService(UserRequest.CheckEmailInDTO checkEmailInDTO) {
        boolean emailExist = existsUserByEmail(checkEmailInDTO.getEmail());
        if (emailExist){
            throw new Exception400("email", "이미 존재하는 이메일입니다. ");
        }
    }

    @Transactional
    public UserResponse.SendCodeOutDTO sendSignupCodeService(UserRequest.SendCodeInDTO sendCodeInDTO) {
        User user = User.builder()
                .firstName(sendCodeInDTO.getFirstName())
                .lastName(sendCodeInDTO.getLastName())
                .email(sendCodeInDTO.getEmail())
                .role(Role.USER.getRole())
                .provider(SocialType.COMMON)
                .status(Status.INACTIVE)
                .build();

        user.generateEmailCheckToken();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }

        String html = createSignupHTML(user);
        try {
            MailUtils.send(user.getEmail(), "3D 에셋 스토어, 회원가입을 위한 이메일 인증", html);
        } catch (MailException e) {
            throw new Exception500("이메일 전송 실패 : " + e.getMessage());
        }

        return new UserResponse.SendCodeOutDTO(user.getId());
    }

    @Transactional
    public void checkSignupCodeService(UserRequest.CheckCodeInDTO checkCodeInDTO) {
        User userPS = findUserById(checkCodeInDTO.getUserId());

        LocalDateTime emailTokenCreatedAt = userPS.getEmailCheckTokenCreatedAt();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime tenMinutesLater = emailTokenCreatedAt.plusMinutes(10);

        if(currentTime.isBefore(tenMinutesLater)) {
            if (!userPS.getEmailCheckToken().equals(checkCodeInDTO.getCode())){
                throw new Exception400("code", "잘못된 인증코드 입니다. ");
            }else{
                userPS.changeStatusToACTIVE();
            }
        }else{
            throw new Exception400("code", "유효하지 않은 인증코드 입니다. ");
        }
    }

    @Transactional
    public void signupService(UserRequest.SignupInDTO signupInDTO) {
        User userPS = findValidUserByEmail(signupInDTO.getEmail());
        if(!userPS.getFirstName().equals(signupInDTO.getFirstName()) || !userPS.getLastName().equals(signupInDTO.getLastName())){
            throw new Exception400("name", "잘못된 요청입니다. ");
        }

        userPS.changePassword(passwordEncoder.encode(signupInDTO.getPassword()));
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
        userPS.changeStatusToINACTIVE();
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
    private boolean existsUserByEmail(String email) {
        boolean emailExist = userRepository.existsByEmailAndStatus(email, Status.ACTIVE);
        return emailExist;
    }

    // 요청한 사용자가 권한이 있는지 확인하는 공통 메소드
    public void authCheck(MyUserDetails myUserDetails, Long userId){
        if (!myUserDetails.getUser().getId().equals(userId)) {
            throw new Exception403("권한이 없습니다. ");
        }
    }

    // 비밀번호 재설정 메일 본문 생성 메서드
    private String createSignupHTML(User user){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 mm월 dd일 HH시 mm분 ss초");
        LocalDateTime expiredTime = user.getEmailCheckTokenCreatedAt().plusMinutes(10);
        String expiredTimeStr = expiredTime.format(formatter);

        return "<body>\n" +
                "    <table\n" +
                "      style=\"\n" +
                "        background-color: #ffffff;\n" +
                "        width: 630px;\n" +
                "        height: 572px;\n" +
                "        padding: 5%;\n" +
                "      \"\n" +
                "    >\n" +
                "      <tr>\n" +
                "        <td style=\"text-align: center;\">\n" +
                "          <img src='cid:logo' style='width: 225px; height: 54px;' />"+
                "          <hr\n" +
                "            style=\"\n" +
                "              width: 100%;\n" +
                "              height: 1px;\n" +
                "              background-color: #9fadbc;\n" +
                "              margin: 32px 0 32px 0;\n" +
                "            \"\n" +
                "          />\n" +
                "          <div style=\"height: 358px; width: 566px;\">\n" +
                "            <h1>Neuroid Asset 회원 가입 인증</h1>\n" +
                "            <p>Neuroid Asset Store에 가입하신 걸 환영합니다!</p>\n" +
                "            <p>\n" +
                "              아래 인증 코드를 입력하면 Neuroid Asset의 서비스를 이용하실 수\n" +
                "              있습니다.\n" +
                "            </p>\n" +
                "            <h2>인증 코드: [" + user.getEmailCheckToken()+ "]</h2>\n" +
                "            <strong\n" +
                "              >인증 코드는 대소문자를 구분합니다. 정확히 입력해 주세요.</strong\n" +
                "            >\n" +
                "            <p>\n" +
                "              인증메일의 유효기간 내에 인증이 완료되지 않으면 가입이 취소됩니다.\n" +
                "            </p>\n" +
                "          </div>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>";
    }

    // 회원가입 메일 본문 생성 메서드
    private String createPasswordChangeHTML(User user){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 mm월 dd일 HH시 mm분 ss초");
        LocalDateTime expiredTime = user.getEmailCheckTokenCreatedAt().plusMinutes(10);
        String expiredTimeStr = expiredTime.format(formatter);

        return "<body>\n" +
                "    <table\n" +
                "      style=\"\n" +
                "        background-color: #ffffff;\n" +
                "        width: 630px;\n" +
                "        height: 572px;\n" +
                "        padding: 5%;\n" +
                "      \"\n" +
                "    >\n" +
                "      <tr>\n" +
                "        <td style=\"text-align: center;\">\n" +
                "          <img src='cid:logo' style='width: 225px; height: 54px;' />"+
                "          <hr\n" +
                "            style=\"\n" +
                "              width: 100%;\n" +
                "              height: 1px;\n" +
                "              background-color: #9fadbc;\n" +
                "              margin: 32px 0 32px 0;\n" +
                "            \"\n" +
                "          />\n" +
                "          <div style=\"height: 358px; width: 566px;\">\n" +
                "            <h1>Neuroid Asset 비밀번호 재설정 인증</h1>\n" +
                "            <p>안녕하세요. Neuroid Asset Store입니다!</p>\n" +
                "            <p>\n" +
                "              아래 인증 코드를 입력하면 비밀번호 재설정을 하실 수 있습니다.\n" +
                "            </p>\n" +
                "            <h2>인증 코드: [" + user.getEmailCheckToken()+ "]</h2>\n" +
                "            <strong\n" +
                "              >인증 코드는 대소문자를 구분합니다. 정확히 입력해 주세요.</strong\n" +
                "            >\n" +
                "            <p>\n" +
                "              인증메일의 유효기간 내에 인증이 완료되지 않으면 인증이 취소됩니다.\n" +
                "            </p>\n" +
                "          </div>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>";
    }
}