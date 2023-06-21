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

    /**
     * 로그인
     */
    @Getter @Setter
    public static class LoginInDTO {

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요. ")
        private String email;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요. ")
        @NotEmpty
        private String password;

        @NotEmpty
        private Boolean keepLogin;
    }

    @Getter @Setter
    public static class CodeSendInDTO {

        @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$", message = "영문/한글 2~20자 이내로 이름을 작성해주세요. ")
        @NotEmpty
        private String firstName;

        @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$", message = "영문/한글 2~20자 이내로 성을 작성해주세요. ")
        @NotEmpty
        private String lastName;

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요. ")
        private String email;

    }

    @Getter @Setter
    public static class CodeCheckInDTO {

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요. ")
        private String email;

        @NotEmpty(message = "인증코드를 입력해주세요.")
        private String code;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class PasswordChangeInDTO {

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요. ")
        private String email;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요. ")
        @NotEmpty
        private String password;

        @NotEmpty(message = "인증코드를 입력해주세요.")
        private String code;
    }

    /**
     * 회원가입
     */
    @Getter @Setter
    @NoArgsConstructor
    public static class EmailCheckInDTO {

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요. ")
        private String email;
    }

    @Getter @Setter
    public static class SignupInDTO {

        @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$", message = "영문/한글 2~20자 이내로 이름을 작성해주세요. ")
        @NotEmpty
        private String firstName;

        @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$", message = "영문/한글 2~20자 이내로 성을 작성해주세요. ")
        @NotEmpty
        private String lastName;

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요. ")
        private String email;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요. ")
        @NotEmpty
        private String password;

    }

    /**
     * 마이페이지
     */
    @Getter @Setter
    public static class CheckPasswordInDTO {

        @NotEmpty(message = "유저 id를 입력해주세요. ")
        private Long userId;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요. ")
        @NotEmpty
        private String password;
    }

    @Getter @Setter
    public static class WithdrawInDTO {

        @Size(min = 0, max = 200, message = "0~200자 이내로 작성해주세요. ")
        private String message;

        @NotEmpty
        private boolean deleteConfirm; // true -> 탈퇴된 상태
    }

    @Getter @Setter
    public static class UpdateInDTO {

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요. ")
        @NotEmpty
        private String newPassword;
    }

    /**
     * 나의 에셋
     */
    @Getter @Setter
    public static class DownloadMyAssetInDTO {

        @NotEmpty(message = "유저 id를 입력해주세요. ")
        private Long userId;

        @NotEmpty(message = "내 에셋에서 다운로드할 목록을 선택해주세요. ")
        private List<Long> assets;
    }
}