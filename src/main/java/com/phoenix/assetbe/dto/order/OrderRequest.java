package com.phoenix.assetbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

public class OrderRequest {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderAssetsInDTO{

        @NotEmpty(message = "주문할 에셋을 입력해주세요. ")
        private List<Long> assetList;

        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;

        @Pattern(regexp = "^[가-힣a-zA-Z]*$", message = "한글 또는 영어만 입력하세요.")
        @NotEmpty(message = "이름을 입력해주세요")
        private String firstName;

        @Pattern(regexp = "^[가-힣a-zA-Z]*$", message = "한글 또는 영어만 입력하세요.")
        @NotEmpty(message = "성을 입력해주세요")
        private String lastName;

        private String phoneNumber;
        
        private Double totalPrice;

        private String paymentTool;

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
