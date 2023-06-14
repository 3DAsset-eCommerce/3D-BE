package com.phoenix.assetbe.dto.user;

import com.phoenix.assetbe.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

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
}
