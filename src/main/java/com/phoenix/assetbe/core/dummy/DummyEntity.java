package com.phoenix.assetbe.core.dummy;

import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.MyAsset;
import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.SocialType;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DummyEntity {

    public User newUser(String lastName, String firstName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode("1234"))
                .email(lastName + firstName +"@nate.com")
                .provider(SocialType.COMMON)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .emailCheckToken(null)
                .build();
    }

    public User newMockUser(Long id, String firstName, String lastName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
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

    public Asset newAsset(String assetName, Double price, Double size, LocalDate date, Double rating) {
        return Asset.builder()
                .assetName(assetName)
                .price(price)
                .discount(0)
                .size(size)
                .extension(".FBX")
                .releaseDate(date)
                .creator("NationA")
                .rating(rating)
                .wishCount(1111L)
                .visitCount(2222L)
                .reviewCount(3333L)
                .status(true)
                .updatedAt(LocalDateTime.now())
                .fileUrl(assetName + ".fileUrl")
                .thumbnailUrl(assetName + ".thumbnailUrl")
                .build();
    }
}
