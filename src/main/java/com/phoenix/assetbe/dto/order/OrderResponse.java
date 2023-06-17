package com.phoenix.assetbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OrderAssetsOutDTO{
        private Long orderId;
    }

    @Getter
    @Setter
    public static class OrderOutDTO{
        private Long orderId;
        private String orderNumber;
        private LocalDate orderDate;
        private Double totalPrice;
        private Long assetCount;

        public OrderOutDTO(Long orderId, LocalDateTime orderDate, Double totalPrice, Long assetCount) {
            this.orderId = orderId;
            String orderNumber = orderDate.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
            this.orderNumber = orderNumber + "-" + String.format("%06d", orderId);;
            this.orderDate = LocalDate.from(orderDate);
            this.totalPrice = totalPrice;
            this.assetCount = assetCount;
        }
    }
}
