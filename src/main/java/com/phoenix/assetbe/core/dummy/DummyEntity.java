package com.phoenix.assetbe.core.dummy;

import com.phoenix.assetbe.model.asset.Asset;
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
                .password(passwordEncoder.encode("qwe123!@#"))
                .email(lastName + firstName +"@nate.com")
                .provider(SocialType.COMMON)
                .role(Role.USER.getRole())
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
                .password(passwordEncoder.encode("qwe123!@#"))
                .provider(SocialType.COMMON)
                .email(firstName + lastName +"@nate.com")
                .role(Role.USER.getRole())
                .status(Status.ACTIVE)
                .emailVerified(true)
                .emailCheckToken(null)
                .build();
    }

    public User newAdmin(String lastName, String firstName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode("qwe123!@#"))
                .email(lastName + firstName +"@nate.com")
                .provider(SocialType.COMMON)
                .role(Role.ADMIN.getRole())
                .status(Status.ACTIVE)
                .emailVerified(true)
                .emailCheckToken(null)
                .build();
    }

    public Asset newAsset(String assetName, Double price, Double size, LocalDate date, Double rating) {
        return Asset.builder()
                .assetName(assetName)
                .price(price)
                .description("assetName은 price원입니다. ")
                .discount(0)
                .size(size)
                .extension(".FBX")
                .releaseDate(date)
                .creator("NationA")
                .rating(rating)
                .wishCount(1111L)
                .visitCount(2222L)
                .reviewCount(0L)
                .status(true)
                .updatedAt(LocalDateTime.now())
                .fileUrl(assetName + ".fileUrl")
                .thumbnailUrl(assetName + ".thumbnailUrl")
                .build();
    }
}
