package com.phoenix.assetbe.dto;

import com.phoenix.assetbe.model.auth.VerifiedCode;
import com.phoenix.assetbe.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

public class UserInDTO {
    @Setter
    @Getter
    public static class LoginInDTO {
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
        @NotEmpty(message = "패스워드를 입력해주세요.")
        @Size(min = 4, max = 20)
        private String password;
    }
    @Setter
    @Getter
    public static class CodeInDTO {
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
        public VerifiedCode toEntity() {
            return VerifiedCode.builder()
                    .email(email)
                    .emailCheckToken(UUID.randomUUID().toString())
                    .build();
        }
    }

    @Setter
    @Getter
    public static class CodeCheckInDTO {
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;

        @NotEmpty(message = "인증코드를 입력해주세요.")
        private String code;
    }
    @Setter
    @Getter
    @NoArgsConstructor
    public static class PasswordChangeInDTO {
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
        @NotEmpty(message = "패스워드를 입력해주세요.")
        @Size(min = 4, max = 20)
        private String password;

        @NotEmpty(message = "인증코드를 입력해주세요.")
        private String code;
    }
}