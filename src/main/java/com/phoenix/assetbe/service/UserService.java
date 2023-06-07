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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public User findUserById(Long userId){

        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 사용자입니다. "));

        return userPS;
    }

    public String loginService(UserInDTO.LoginInDTO loginInDTO) {
        Optional<User> userOP = userRepository.findByEmail(loginInDTO.getEmail());
        if(userOP.isPresent()&&!userOP.get().isEmailVerified()){
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
    public CodeOutDTO codeSending(UserInDTO.CodeInDTO codeInDTO){
        User userPS = userRepository.findByEmail(codeInDTO.getEmail()).get();
        userPS.generateEmailCheckToken();
//        userRepository.save(userPS);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userPS.getEmail());
        mailMessage.setSubject("3D 에셋 스토어, 비밀번호 재설정을 위한 이메일 인증");
        mailMessage.setText(userPS.getEmailCheckToken());
        javaMailSender.send(mailMessage);

        return new CodeOutDTO(userPS);
    }

    @Transactional
    public CodeCheckOutDTO codeChecking(CodeCheckInDTO codeCheckInDTO) {
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
    public PasswordChangeOutDTO passwordChanging(PasswordChangeInDTO passwordChangeInDTO) {
        Optional<User> userOP = userRepository.findByEmail(passwordChangeInDTO.getEmail());
        if(userOP.isPresent()&&userOP.get().getEmailCheckToken()==null){
            throw new Exception400("email","이메일 인증을 먼저 해야 합니다.");
        }
        if(userOP.isPresent()&&userOP.get().getEmailCheckToken().equals(passwordChangeInDTO.getCode())){
            userOP.get().setPassword(passwordEncoder.encode(passwordChangeInDTO.getPassword()));
            userOP.get().setEmailCheckToken("");
            userOP.get().setTokenCreatedAt();

            return new PasswordChangeOutDTO(userOP.get().getEmail());
        }
        throw new Exception400("code","이메일 인증 코드가 틀렸습니다.");
    }

    public EmailCheckOutDTO emailChecking(EmailCheckInDTO emailCheckInDTO) {
        Optional<User> user = userRepository.existsByEmail(emailCheckInDTO.getEmail());
        if(user.isPresent()){
            throw new Exception400("email","이미 존재하는 이메일입니다.");
        }

        return new EmailCheckOutDTO(emailCheckInDTO.getEmail());
    }

    @Transactional
    public UserOutDTO.SignupOutDTO signupService(UserInDTO.SignupInDTO signupInDTO) {
        Optional<User> userOP =userRepository.findByEmail(signupInDTO.getEmail());
        if(userOP.isPresent()){
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("email", "이미 이메일이 존재합니다");
        }
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

    public void checkPassword(UserInDTO.CheckPasswordInDTO checkPasswordInDTO, Long userId) {
        if (checkPasswordInDTO.getId().longValue() != userId) {
            throw new Exception400("id", "아이디가 일치하지 않습니다.");
        }

        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다.")
        );

        if (!passwordEncoder.matches(checkPasswordInDTO.getPassword(), userPS.getPassword())) {
            throw new Exception400("password", "비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void withdrawal(UserInDTO.WithdrawalInDTO withdrawalInDTO, Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다.")
        );

        userPS.changeWithdrawalMassage(withdrawalInDTO.getMessage());
        try {
            userRepository.save(userPS);
        } catch (Exception e) {
            throw new Exception500("회원탈퇴 실패 : "+e.getMessage());
        }
    }

    @Transactional
    public void update(UserInDTO.UpdateInDTO updateInDTO, Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다.")
        );

        // 성과 이름 구분하지 않고 Exception
        if (!updateInDTO.getFirstName().equals(userPS.getFirstName()) ||
        !updateInDTO.getLastName().equals(userPS.getLastName())) {
            throw new Exception400("name", "이름이 일치하지 않습니다.");
        }

        userPS.changePassword(passwordEncoder.encode(updateInDTO.getNewPassword()));
        try {
            userRepository.save(userPS);
        } catch (Exception e) {
            throw new Exception500("회원정보 수정 실패 : "+e.getMessage());
        }
    }
}
