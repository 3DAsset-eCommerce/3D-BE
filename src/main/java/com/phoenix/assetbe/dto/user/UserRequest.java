package com.phoenix.assetbe.dto.user;

import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.SocialType;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;
import java.util.UUID;

public class UserRequest {
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
        @NotEmpty(message = "firstName을 작성해주세요. ")
        private String firstName;

        @NotEmpty(message = "lastName을 작성해주세요. ")
        private String lastName;

        @NotEmpty(message = "비밀번호를 형식에 맞게 작성해주세요. ")
        @Size(min = 8, max = 20)
        private String password;

        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요. ")
        private String email;


        public User toEntity() {
            return User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(password)
                    .email(email)
                    .role(Role.USER)
                    .provider(SocialType.COMMON)
                    .status(Status.ACTIVE)
                    .emailVerified(false)
                    .build();
        }
    }

    /**
     * 마이페이지
     */
    @Getter
    @Setter
    public static class CheckPasswordInDTO {
        @NotEmpty
        private Long id;

        @Size(min = 8, max = 20)
        @NotEmpty(message = "패스워드를 입력해주세요. ")
        private String password;
    }

    @Getter
    @Setter
    public static class WithdrawInDTO {
        @NotEmpty(message = "탈퇴 사유를 적어주세요. ")
        private String message;
        private boolean deleteConfirm; // true -> 탈퇴된 상태
    }

    @Getter
    @Setter
    public static class UpdateInDTO {
        @NotEmpty(message = "새로운 비밀번호를 입력해주세요. ")
        private String newPassword;
    }

    /**
     * 나의 에셋
     */
    @Getter
    @Setter
    public static class DownloadMyAssetInDTO {
        @NotEmpty
        private Long userId;
        @NotEmpty(message = "내 에셋에서 다운로드할 목록을 입력해주세요. ")
        private List<Long> assets;
    }
}