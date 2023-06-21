package com.phoenix.assetbe.model.user;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("ACTIVE"),INACTIVE("INACTIVE");

    private final String status;

    Status(String status) {
        this.status = status;
    }
}
