package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception401;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.dto.UserInDTO.CodeCheckInDTO;
import com.phoenix.assetbe.dto.UserInDTO.EmailCheckInDTO;
import com.phoenix.assetbe.dto.UserInDTO.PasswordChangeInDTO;
import com.phoenix.assetbe.dto.UserOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeCheckOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.EmailCheckOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.PasswordChangeOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.SignupOutDTO;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public String loginService(UserInDTO.LoginInDTO loginInDTO) {
        User userPS = findUserByEmail(loginInDTO.getEmail());
        if(!userPS.isEmailVerified()){
            throw new Exception400("verified","이메일 인증이 필요합니다.");
        }

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            return MyJwtProvider.create(myUserDetails.getUser());
        }catch (Exception e){
            throw new Exception401("아이디 혹은 비밀번호를 확인해주세요.");
        }
    }

    @Transactional
    public CodeOutDTO codeSendingService(UserInDTO.CodeInDTO codeInDTO){
        User userPS = findUserByEmail(codeInDTO.getEmail());
        userPS.generateEmailCheckToken();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userPS.getEmail());
        mailMessage.setSubject("3D 에셋 스토어, 비밀번호 재설정을 위한 이메일 인증");
        mailMessage.setText(userPS.getEmailCheckToken());
        javaMailSender.send(mailMessage);

        return new CodeOutDTO(userPS);
    }

    @Transactional
    public CodeCheckOutDTO codeCheckingService(CodeCheckInDTO codeCheckInDTO) {
        Optional<User> userPS = userRepository.findByEmail(codeCheckInDTO.getEmail());
        if(!userPS.isPresent()){
            throw new Exception400("code", "먼저 이메일 인증코드를 전송해주세요.");
        }
        if(userPS.get().getEmailCheckToken().equals(codeCheckInDTO.getCode())){
            return new CodeCheckOutDTO(userPS.get().getEmail(), true);
        }
        throw new Exception400("code", "이메일 인증코드가 틀렸습니다.");
    }

    @Transactional
    public PasswordChangeOutDTO passwordChangingService(PasswordChangeInDTO passwordChangeInDTO) {
        User userPS = findUserByEmail(passwordChangeInDTO.getEmail());
        if(userPS.getEmailCheckToken()==null){
            throw new Exception400("email","이메일 인증을 먼저 해야 합니다.");
        }
        if(userPS.getEmailCheckToken().equals(passwordChangeInDTO.getCode())){
            userPS.setPassword(passwordEncoder.encode(passwordChangeInDTO.getPassword()));
            userPS.setEmailCheckToken("");
            userPS.setTokenCreatedAt();

            return new PasswordChangeOutDTO(userPS.getEmail());
        }
        throw new Exception400("code","이메일 인증 코드가 틀렸습니다.");
    }

    public EmailCheckOutDTO emailCheckingService(EmailCheckInDTO emailCheckInDTO) {
        existsUserByEmail(emailCheckInDTO.getEmail());
        return new EmailCheckOutDTO(emailCheckInDTO.getEmail());
    }

    @Transactional
    public UserOutDTO.SignupOutDTO signupService(UserInDTO.SignupInDTO signupInDTO) {
        existsUserByEmail(signupInDTO.getEmail());

        String encPassword = passwordEncoder.encode(signupInDTO.getPassword()); // 60Byte
        signupInDTO.setPassword(encPassword);
        System.out.println("encPassword : "+encPassword);

        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(signupInDTO.toEntity());
            userPS.generateEmailCheckToken();
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userPS.getEmail());
            mailMessage.setSubject("3D 에셋 스토어, 회원 가입 인증");
            mailMessage.setText("/check-email-token?token=" + userPS.getEmailCheckToken() + "&email=" + userPS.getEmail());
            javaMailSender.send(mailMessage);

            return new SignupOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : "+e.getMessage());
        }
    }

    public void checkPasswordService(UserInDTO.CheckPasswordInDTO checkPasswordInDTO, Long userId) {
        if (checkPasswordInDTO.getId().longValue() != userId) {
            throw new Exception400("id", "아이디가 일치하지 않습니다.");
        }
        User userPS = findUserById(userId);
        if (!passwordEncoder.matches(checkPasswordInDTO.getPassword(), userPS.getPassword())) {
            throw new Exception400("password", "비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void withdrawalService(UserInDTO.WithdrawalInDTO withdrawalInDTO, Long userId) {
        User userPS = findUserById(userId);

        userPS.changeStatus();
        userPS.changeWithdrawalMassage(withdrawalInDTO.getMessage());
        try {
            userRepository.save(userPS);
        } catch (Exception e) {
            throw new Exception500("회원탈퇴 실패 : "+e.getMessage());
        }
    }

    @Transactional
    public void updateService(UserInDTO.UpdateInDTO updateInDTO, Long userId) {
        User userPS = findUserById(userId);
        if (!updateInDTO.getFirstName().equals(userPS.getFirstName()) || !updateInDTO.getLastName().equals(userPS.getLastName())) {
            throw new Exception400("name", "이름이 일치하지 않습니다.");
        }

        userPS.changePassword(passwordEncoder.encode(updateInDTO.getNewPassword()));
        try {
            userRepository.save(userPS);
        } catch (Exception e) {
            throw new Exception500("회원정보 수정 실패 : "+e.getMessage());
        }
    }

    public UserOutDTO.FindMyInfoOutDTO findMyInfoService(Long userId) {
        User userPS = findUserById(userId);
        return new UserOutDTO.FindMyInfoOutDTO(new UserOutDTO.UserDTO(userPS));
    }

    /**
     * 요청한 사용자가 id의 주인인지 확인하는 공통 메소드
     */
    public User findUserById(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다.")
        );
        return userPS;
    }

    /**
     * 요청한 사용자가 email의 주인인지 확인하는 공통 메소드
     */
    public User findUserByEmail(String email) {
        User userPS = userRepository.findByEmail(email).orElseThrow(
                () -> new Exception400("email", "해당 유저를 찾을 수 없습니다.")
        );
        return userPS;
    }

    /**
     * 요청한 사용자 email이 존재하는지 확인하는 공통 메소드
     */
    public void existsUserByEmail(String email) {
        boolean userCheck = userRepository.existsByEmail(email);
        if(userCheck){
            throw new Exception400("email","이미 존재하는 이메일입니다.");
        }
    }
}
