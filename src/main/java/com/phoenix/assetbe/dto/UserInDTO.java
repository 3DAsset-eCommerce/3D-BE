package com.phoenix.assetbe.dto;

import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.SocialType;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        public User toEntity() {
            return User.builder()
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

    @Setter
    @Getter
    @NoArgsConstructor
    public static class EmailCheckInDTO {
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
    }

    @Getter
    @Setter
    public static class SignupInDTO {

        @NotEmpty
        private String firstname;

        @NotEmpty
        private String lastname;

        @NotEmpty
        @Size(min = 8, max = 20)
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,20}$", message = "영문/숫자/특수문자를 조합하여 8~20자 이내로 작성해주세요")
        private String password;

        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;


        public User toEntity() {
            return User.builder()
                    .firstname(firstname)
                    .lastname(lastname)
                    .password(password)
                    .email(email)
                    .role(Role.USER)
                    .provider(SocialType.COMMON)
                    .status(Status.ACTIVE)
                    .emailCheckToken(null)
                    .build();
        }
    }
}