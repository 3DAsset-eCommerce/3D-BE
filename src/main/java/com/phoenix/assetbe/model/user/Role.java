package com.phoenix.assetbe.model.user;

import lombok.Getter;

@Getter
public enum Role {
    USER("일반"),ADMIN("관리자");

    private final String role;

    Role(String role) {
        this.role = role;
    }
}
