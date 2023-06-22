package com.phoenix.assetbe.dto.admin;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class AdminRequest {

    @Getter @Setter
    public static class InactiveAssetInDTO {

        @NotEmpty(message = "내 에셋에서 비활성화를 할 목록을 선택해주세요. ")
        private List<Long> assets;
    }

    @Getter @Setter
    public static class ActiveAssetInDTO {

        @NotEmpty(message = "내 에셋에서 활성화를 할 목록을 선택해주세요. ")
        private List<Long> assets;
    }
}
