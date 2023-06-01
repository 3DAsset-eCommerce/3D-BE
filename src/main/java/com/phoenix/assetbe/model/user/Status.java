package com.phoenix.assetbe.model.user;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("활성"),INACTIVE("탈퇴");

    private final String status;

    Status(String status) {
        this.status = status;
    }
}
