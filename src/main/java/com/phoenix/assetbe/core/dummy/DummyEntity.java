package com.phoenix.assetbe.core.dummy;

import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.SocialType;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String firstName, String lastName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode("1234"))
                .email(firstName + lastName +"@nate.com")
                .provider(SocialType.COMMON)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .emailCheckToken(null)
                .build();
    }

    public User newMockUser(String firstName, String lastName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode("1234"))
                .provider(SocialType.COMMON)
                .email(firstName + lastName +"@nate.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .emailCheckToken(null)
                .build();
    }

    public Asset newAsset(String assetName){
        return Asset.builder().assetName(assetName).build();
    }
}
