package com.phoenix.assetbe.dto.admin;

import com.phoenix.assetbe.model.asset.Category;
import com.phoenix.assetbe.model.asset.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminResponse {

    /**
     * 카테고리
     */
    @Getter @Setter
    public static class GetCategoryListOutDTO {
        private List<Category> categoryList;

        public GetCategoryListOutDTO(List<Category> categoryList) {
            this.categoryList = categoryList;
        }
    }

    /**
     * 서브 카테고리
     */
    @Getter @Setter
    public static class GetSubCategoryListOutDTO {
        private List<SubCategory> subCategoryList;

        public GetSubCategoryListOutDTO(List<SubCategory> subCategoryList) {
            this.subCategoryList = subCategoryList;
        }
    }

    /**
     * 에셋
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class AssetListOutDTO {
        private List<?> assetList;
        private int size;
        private int currentPage;
        private int totalPage;
        private long totalElement;

        public AssetListOutDTO(Page<?> assetList) {
            this.assetList = assetList.getContent();
            this.size = assetList.getSize();
            this.currentPage = assetList.getNumber();
            this.totalPage = assetList.getTotalPages();
            this.totalElement = assetList.getTotalElements();
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter @Setter
        public static class AssetOutDTO {
            private String assetNumber;
            private String assetName;
            private String status;
            private Double price;
            private String categoryName;
            private String subCategoryName;
            private LocalDate releaseDate;
            private LocalDate updatedAt;

            public AssetOutDTO(Long assetId, String assetName, boolean status, Double price, String categoryName, String subCategoryName, LocalDate releaseDate, LocalDateTime updatedAt) {
                this.assetNumber = releaseDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + String.format("%06d", assetId);
                this.assetName = assetName;
                this.status = status ? "active" : "inactive";
                this.price = price;
                this.categoryName = categoryName;
                this.subCategoryName = subCategoryName;
                this.releaseDate = releaseDate;
                if(updatedAt == null){
                    this.updatedAt = null;
                }else{
                    this.updatedAt = LocalDate.from(updatedAt);
                }
            }
        }
    }

    /**
     * 주문
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class OrderListOutDTO {
        private List<?> orderList;
        private int size;
        private int currentPage;
        private int totalPage;
        private long totalElement;

        public OrderListOutDTO(Page<?> orderList) {
            this.orderList = orderList.getContent();
            this.size = orderList.getSize();
            this.currentPage = orderList.getNumber();
            this.totalPage = orderList.getTotalPages();
            this.totalElement = orderList.getTotalElements();
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter @Setter
        public static class OrderOutDTO {
            private String orderNumber;
            private LocalDate orderDate;
            private String assetName;
            private Long assetCount;
            private String email;
            private Double price;
            private String paymentTool;
            private boolean status;

            public OrderOutDTO (Long orderId, LocalDateTime orderDate, String assetName, Long assetCount, String email, Double price, String paymentTool, Long paymentId) {
                this.orderNumber = orderDate.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + String.format("%06d", orderId);
                this.orderDate = orderDate.toLocalDate();
                this.assetName = assetName + " 외 " + (assetCount-1) + "건";
                this.assetCount = assetCount;
                this.email = email;
                this.price = price;
                this.paymentTool = paymentTool;
                this.status = paymentId != null;
            }
        }
    }
}