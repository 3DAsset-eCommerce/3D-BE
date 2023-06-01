package com.phoenix.assetbe.model.user;


import lombok.Getter;

@Getter
public enum SocialType {
    COMMON("일반"),GOOGLE("구글");

    private final String type;

    SocialType(String type) {
        this.type = type;
    }
}
