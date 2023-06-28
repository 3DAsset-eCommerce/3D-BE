package com.phoenix.assetbe.model.user;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER"),ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
