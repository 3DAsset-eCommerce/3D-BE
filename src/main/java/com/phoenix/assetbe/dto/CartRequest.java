package com.phoenix.assetbe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CartRequest {

    @Getter
    @Setter
    public static class AddCartDTO{

        private Long userId;

        private List<Long> assets;
    }
}
