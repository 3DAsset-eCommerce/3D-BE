package com.phoenix.assetbe.dto.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CartRequest {

    @Getter
    @Setter
    public static class AddCartInDTO{

        @NotEmpty(message = "유저 id를 입력해주세요. ")
        private Long userId;

        @NotEmpty(message = "장바구니에 담을 에셋을 입력해주세요. ")
        private List<Long> assets;
    }

    @Getter
    @Setter
    public static class DeleteCartInDTO{

        @NotEmpty(message = "유저 id를 입력해주세요. ")
        private Long userId;

        @NotEmpty(message = "장바구니에서 삭제할 에셋을 입력해주세요. ")
        private List<Long> carts;
    }
}
