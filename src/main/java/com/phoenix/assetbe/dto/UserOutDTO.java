package com.phoenix.assetbe.dto;

import com.phoenix.assetbe.model.auth.VerifiedCode;
import com.phoenix.assetbe.model.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserOutDTO {
    @Setter
    @Getter
    public static class CodeOutDTO {
        private Long id;
        private String email;
        private String emailCheckToken;

        public CodeOutDTO(VerifiedCode verifiedCode) {
            this.id = verifiedCode.getId();
            this.email = verifiedCode.getEmail();
            this.emailCheckToken = verifiedCode.getEmailCheckToken();
        }
    }
}
