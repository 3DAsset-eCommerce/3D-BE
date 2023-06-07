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
                .provider(SocialType.COMMON)
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
                .build();
    }

    public Asset newAsset(String assetName){
        return Asset.builder().assetName(assetName).build();
    }
}
