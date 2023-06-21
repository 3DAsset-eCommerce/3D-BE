package com.phoenix.assetbe.dto.admin;

import com.phoenix.assetbe.model.asset.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AdminResponse {

    @Getter @Setter
    public static class CategoryOutDTO {
        private List<Category> categoryList;

        public CategoryOutDTO(List<Category> categoryList) {
            this.categoryList = categoryList;
        }
    }
}