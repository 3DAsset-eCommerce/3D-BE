package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.jwt.MyJwtProvider;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception401;
import com.phoenix.assetbe.dto.UserInDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;


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
}
