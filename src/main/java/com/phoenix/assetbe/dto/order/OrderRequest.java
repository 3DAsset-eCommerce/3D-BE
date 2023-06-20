package com.phoenix.assetbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

public class OrderRequest {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderAssetsInDTO{

        @NotEmpty(message = "주문할 에셋을 입력해주세요. ")
        private List<Long> assetList;

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요. ")
        private String email;

        @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$", message = "영문/한글 2~20자 이내로 이름을 작성해주세요. ")
        @NotEmpty
        private String firstName;

        @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$", message = "영문/한글 2~20자 이내로 성을 작성해주세요. ")
        @NotEmpty
        private String lastName;

        private String phoneNumber;
        
        private Double totalPrice;

        private String paymentTool;

    }
}
