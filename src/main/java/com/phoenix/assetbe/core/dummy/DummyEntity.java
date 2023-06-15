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
    /**
     * User 더미데이터
     */
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

    /**
     * Asset 더미데이터
     */
    public Asset newAsset(String assetName){
        return Asset.builder()
                .assetName(assetName)
                .build();
    }

    public Asset newAsset1(String assetName) {
        return Asset.builder()
                .assetName(assetName)
                .price(10000.0)
                .discount(0)
                .size(10.7)
                .extension("확장자입니다.")
                .releaseDate(LocalDate.now())
                .creator("네이션에이")
                .rating(4.5)
                .wishCount(1111L)
                .visitCount(2222L)
                .reviewCount(3333L)
                .status(true)
                .updatedAt(LocalDateTime.now())
                .fileUrl(assetName + " fileUrl 입니다.")
                .thumbnailUrl(assetName + " thumbnail 입니다.")
                .build();
    }

    public Asset newAsset2(String assetName) {
        return Asset.builder()
                .assetName(assetName)
                .price(10000.0)
                .size(10.7)
                .discount(0)
                .extension("확장자입니다.")
                .releaseDate(LocalDate.now())
                .creator("네이션에이")
                .rating(4.5)
                .wishCount(1111L)
                .visitCount(2222L)
                .reviewCount(3333L)
                .status(true)
                .updatedAt(LocalDateTime.now())
                .fileUrl(assetName + " fileUrl 입니다.")
                .thumbnailUrl(assetName + " thumbnail 입니다.")
                .build();
    }

    /**
     * MyAsset 더미 데이터
     */
    public MyAsset newMyAsset1(User user, Asset asset) {
        return MyAsset.builder()
                .user(newUser(user.getFirstName(), user.getLastName()))
                .asset(newAsset(asset.getAssetName()))
                .build();
    }

    public MyAsset newMyAsset2(User user, Asset asset) {
        return MyAsset.builder()
                .user(newUser(user.getFirstName(), user.getLastName()))
                .asset(newAsset(asset.getAssetName()))
                .build();
    }
}
