package com.phoenix.assetbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class OrderResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OrderAssetsOutDTO{
        private Long orderId;
    }
}
