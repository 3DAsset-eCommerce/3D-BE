package com.phoenix.assetbe.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class AdminRequest {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class addCategoryDTO{
        private String categoryName;
    }
}
