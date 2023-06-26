package com.phoenix.assetbe.dto.wishList;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class WishRequest {

    @Getter
    @Setter
    public static class AddWishInDTO{

        @NotEmpty(message = "유저 id를 입력해주세요. ")
        private Long userId;

        @NotEmpty(message = "위시리스트에 담을 에셋을 입력해주세요. ")
        private Long assetId;
    }

    @Getter
    @Setter
    public static class DeleteWishInDTO{

        @NotEmpty(message = "유저 id를 입력해주세요. ")
        private Long userId;

        @NotEmpty(message = "위시리스트에서 삭제할 에셋을 입력해주세요. ")
        private List<Long> wishes;
    }
}
