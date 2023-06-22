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
            this.currentPage = assetList.getPageable().getPageNumber();
            this.totalPage = assetList.getTotalPages();
            this.totalElement = assetList.getTotalElements();
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter @Setter
        public static class AssetOutDTO {
            private Long assetNumber;
            private String assetName;
            private Double price;
            private String categoryName;
            private String subCategoryName;
            private LocalDate releaseDate;
            private LocalDate updatedAt;

            public AssetOutDTO(Long assetId, String assetName, Double price, String categoryName, String subCategoryName, LocalDate releaseDate, LocalDateTime updatedAt) {
                this.assetNumber = Long.valueOf(releaseDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + String.format("%06d", assetId));
                this.assetName = assetName;
                this.price = price;
                this.categoryName = categoryName;
                this.subCategoryName = subCategoryName;
                this.releaseDate = releaseDate;
                this.updatedAt = LocalDate.from(updatedAt);
            }
        }
    }
}