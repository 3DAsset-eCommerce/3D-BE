package com.phoenix.assetbe.dto.admin;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class AdminRequest {

    /**
     * 내 에셋
     */
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

    @Getter @Setter
    public static class UpdateAssetInDTO {

        @NotEmpty(message = "에셋 id를 입력해주세요. ")
        private Long assetId;
        @NotEmpty(message = "에셋 이름을 입력해주세요. ")
        private String assetName;
        @NotEmpty(message = "에셋 설명을 입력해주세요. ")
        private String assetDescription;
        @NotEmpty(message = "에셋 가격을 입력해주세요. ")
        private Double price;
        @NotEmpty(message = "에셋 할인률을 입력해주세요. ")
        private Integer discount;

        @NotEmpty(message = "에셋 카테고리를 선택헤주세요. ")
        private String category;
        @NotEmpty(message = "에셋 서브 카테고리를 선택해주세요. ")
        private String subCategory;

        private List<String> deleteTagList;
        private List<String> addTagList;

        @NotEmpty(message = "에셋 파일을 넣어주세요. ")
        private String fileUrl;
        @NotEmpty(message = "에셋 썸네일을 넣어주세요. ")
        private String thumbnailUrl;
        @NotEmpty(message = "에셋 프리뷰를 넣어주세요. ")
        private List<String> previewUrlList;
    }
}
