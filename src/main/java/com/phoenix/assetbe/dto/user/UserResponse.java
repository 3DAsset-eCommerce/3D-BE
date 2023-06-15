package com.phoenix.assetbe.dto.user;

import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserResponse {
    @Getter
    @Setter
    public static class LoginWithJWTOutDTO {
        private Long id;
        private String jwt;

        public LoginWithJWTOutDTO(Long id, String jwt) {
            this.id = id;
            this.jwt = jwt;
        }
    }
    @Getter
    @Setter
    public static class LoginOutDTO {
        private Long userId;
        public LoginOutDTO(Long userId) {
            this.userId = userId;
        }
    }
    @Setter
    @Getter
    public static class CodeOutDTO {
        private Long id;
        private String email;
        private String emailCheckToken;

        public CodeOutDTO(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.emailCheckToken = user.getEmailCheckToken();
        }
    }

    @Setter
    @Getter
    public static class CodeCheckOutDTO {
        private String email;
        private boolean verified;

        public CodeCheckOutDTO(String email, boolean verified) {
            this.email = email;
            this.verified = verified;
        }
    }

    @Setter
    @Getter
    public static class PasswordChangeOutDTO{
        private String email;

        public PasswordChangeOutDTO(String email) {
            this.email = email;
        }
    }

    /**
     * 마이페이지
     */
    @Setter
    @Getter
    public static class EmailCheckOutDTO {
        private String email;

        public EmailCheckOutDTO(String email) {
            this.email = email;
        }
    }

    @Getter
    @Setter
    public static class SignupOutDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;

        public SignupOutDTO(User user) {
            this.id = user.getId();
            this.firstName=user.getFirstName();
            this.lastName=user.getLastName();
            this.email = user.getEmail();
        }
    }

    @Getter
    @Setter
    public static class FindMyInfoOutDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String createdAt;

        public FindMyInfoOutDTO(User user) {
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();

            if (user.getCreatedAt() != null) {
                this.createdAt = user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                this.createdAt = null;
            }
        }
    }

    /**
     * 나의 에셋
     */
    @Getter
    @Setter
    public static class MyAssetListOutDTO {
        private List<?> myAssetList;
        private int size;
        private int currentPage;
        private int totalPage;
        private long totalElement;

        public MyAssetListOutDTO(Page<?> myAssetList) {
            this.myAssetList = myAssetList.getContent();
            this.size = myAssetList.getSize();
            this.currentPage = myAssetList.getPageable().getPageNumber();
            this.totalPage = myAssetList.getTotalPages();
            this.totalElement = myAssetList.getTotalElements();
        }

        @Getter
        @Setter
        public static class FindMyAssetOutDTO {
            private Long assetId;
            private String assetName;
            private String fileUrl;
            private String thumbnailUrl;

            public FindMyAssetOutDTO(Long assetId, String assetName, String fileUrl, String thumbnailUrl) {
                this.assetId = assetId;
                this.assetName = assetName;
                this.fileUrl = fileUrl;
                this.thumbnailUrl = thumbnailUrl;
            }
        }
    }
}
