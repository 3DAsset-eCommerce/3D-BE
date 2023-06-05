package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception401;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.dto.UserInDTO.CodeCheckInDTO;
import com.phoenix.assetbe.dto.UserInDTO.PasswordChangeInDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeCheckOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.CodeOutDTO;
import com.phoenix.assetbe.dto.UserOutDTO.PasswordChangeOutDTO;
import com.phoenix.assetbe.model.auth.VerifiedCode;
import com.phoenix.assetbe.model.auth.VerifiedCodeRepository;
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

    private final VerifiedCodeRepository verifiedCodeRepository;
    private final UserRepository userRepository;


    public String loginService(UserInDTO.LoginInDTO loginInDTO) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            return MyJwtProvider.create(myUserDetails.getUser());
        }catch (Exception e){
            throw new Exception401("인증되지 않았습니다");
        }
    }

    @Transactional
    public CodeOutDTO codeSending(UserInDTO.CodeInDTO codeInDTO){
        VerifiedCode verifiedCode=codeInDTO.toEntity();
        verifiedCodeRepository.save(verifiedCode);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(verifiedCode.getEmail());
        mailMessage.setSubject("3D 에셋 스토어, 회원 가입 인증");
        mailMessage.setText(verifiedCode.getEmailCheckToken());
        javaMailSender.send(mailMessage);
        return new CodeOutDTO(verifiedCode);
    }

    @Transactional
    public CodeCheckOutDTO codeChecking(CodeCheckInDTO codeCheckInDTO) {
        Optional<VerifiedCode> verifiedCode = verifiedCodeRepository.findByEmail(codeCheckInDTO.getEmail());
        if(!verifiedCode.isPresent()){
            throw new Exception400("code", "먼저 이메일 인증코드를 전송해주세요.");
        }
        if(verifiedCode.get().getEmailCheckToken().equals(codeCheckInDTO.getCode())){
            return new CodeCheckOutDTO(verifiedCode.get().getEmail(), true);
        }

        throw new Exception400("code", "이메일 인증코드가 틀렸습니다.");
    }

    @Transactional
    public PasswordChangeOutDTO passwordChanging(PasswordChangeInDTO passwordChangeInDTO) {
        Optional<User> user = userRepository.findByEmail(passwordChangeInDTO.getEmail());
        if(user.isPresent()){
            user.get().setPassword(passwordEncoder.encode(passwordChangeInDTO.getPassword()));

            return new PasswordChangeOutDTO(user.get().getEmail());
        }
        throw new Exception400("none","패스워드 재설정 실패");
    }
}
