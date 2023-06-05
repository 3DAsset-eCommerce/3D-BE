package com.phoenix.assetbe.core.dummy;

import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.SocialType;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String firstname, String lastname){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .password(passwordEncoder.encode("1234"))
                .email(firstname + lastname +"@nate.com")
                .provider(SocialType.COMMON)
                .role(Role.USER)
                .status(Status.ACTIVE)
<<<<<<< HEAD
                .provider(SocialType.COMMON)
=======
                .emailVerified(true)
>>>>>>> 63e7465 (feat #33 비밀번호 재설정 1차 구현 완료)
                .build();
    }

    public User newMockUser(String firstname, String lastname){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .password(passwordEncoder.encode("1234"))
                .provider(SocialType.COMMON)
                .email(firstname + lastname +"@nate.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();
    }

    public Asset newAsset(String assetName){
        return Asset.builder().assetName(assetName).build();
    }
}
