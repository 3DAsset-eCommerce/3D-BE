package com.phoenix.assetbe.dto.order;

import com.phoenix.assetbe.model.asset.Asset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        private List<?> orderList;
        private int size;
        private int currentPage;
        private int totalPage;
        private long totalElement;

        public OrderOutDTO(Page<?> orderList){
            this.orderList = orderList.getContent();
            this.size = orderList.getSize();
            this.currentPage = orderList.getNumber();
            this.totalPage = orderList.getTotalPages();
            this.totalElement = orderList.getTotalElements();
        }

        @Getter @Setter
        public static class OrderListOutDTO{
            private Long orderId;
            private String orderNumber;
            private LocalDate orderDate;
            private Double totalPrice;
            private Long assetCount;

            public OrderListOutDTO(Long orderId, LocalDateTime orderDate, Double totalPrice, Long assetCount) {
                this.orderId = orderId;
                String orderNumber = orderDate.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
                this.orderNumber = orderNumber + "-" + String.format("%06d", orderId);
                this.orderDate = LocalDate.from(orderDate);
                this.totalPrice = totalPrice;
                this.assetCount = assetCount;
            }
        }
    }

    @Getter
    @Setter
    public static class OrderProductWithDetailsOutDTO{
        private List<OrderProductOutDTO> orderProductList;
        private OrderProductWithDetailsOutDTO.OrderDetailsDTO orderDetails;

        public OrderProductWithDetailsOutDTO(List<OrderProductOutDTO> orderProductList, OrderProductWithDetailsOutDTO.OrderDetailsDTO orderDetails) {
            this.orderProductList = orderProductList;
            this.orderDetails = orderDetails;
        }

        @Getter
        @Setter
        public static class OrderProductOutDTO {
            private Long assetId;
            private String assetName;
            private String extension;
            private Double price;
            private Double discountPrice;
            private Double size;
            private String thumbnailUrl;

            public OrderProductOutDTO(Asset asset) {
                this.assetId = asset.getId();
                this.assetName = asset.getAssetName();
                this.extension = asset.getExtension();
                this.price = asset.getPrice();
                this.discountPrice = asset.getDiscountPrice();
                this.size = asset.getSize();
                this.thumbnailUrl = asset.getThumbnailUrl();
            }
        }

        @Getter
        @Setter
        public static class OrderDetailsDTO {
            private Long orderId;
            private String orderNumber;
            private LocalDate orderDate;
            private String paymentTool;
            private Double totalPrice;
            private Long assetCount;

            public OrderDetailsDTO(Long orderId, LocalDateTime orderDate, String paymentTool, Double totalPrice, Long assetCount) {
                this.orderId = orderId;
                String orderNumber = orderDate.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
                this.orderNumber = orderNumber + "-" + String.format("%06d", orderId);
                this.orderDate = LocalDate.from(orderDate);
                this.paymentTool = paymentTool;
                this.totalPrice = totalPrice;
                this.assetCount = assetCount;
            }
        }
    }
}
