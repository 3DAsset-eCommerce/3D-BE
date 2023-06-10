package com.phoenix.assetbe.dto;

import lombok.Getter;
import lombok.Setter;

public class CartResponse {

    @Getter
    @Setter
    public static class CountCartOutDTO{
        private Long cartCount;

        public CountCartOutDTO(Long cartCount) {
            this.cartCount = cartCount;
        }
    }
}
