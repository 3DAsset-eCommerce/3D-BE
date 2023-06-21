package com.phoenix.assetbe.dto.user;

import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserResponse {

    /**
     * 로그인
     */
    @Getter
    @Setter
    public static class LoginOutDTOWithJWT {
        private Long id;
        private String jwt;

        public LoginOutDTOWithJWT(Long id, String jwt) {
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

    /**
     * 마이페이지
     */
    @Getter
    @Setter
    public static class GetMyInfoOutDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String createdAt;

        public GetMyInfoOutDTO(User user) {
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
            this.currentPage = myAssetList.getNumber();
            this.totalPage = myAssetList.getTotalPages();
            this.totalElement = myAssetList.getTotalElements();
        }

        @Getter
        @Setter
        public static class GetMyAssetOutDTO {
            private Long assetId;
            private String assetName;
            private String fileUrl;
            private String thumbnailUrl;

            public GetMyAssetOutDTO(Long assetId, String assetName, String fileUrl, String thumbnailUrl) {
                this.assetId = assetId;
                this.assetName = assetName;
                this.fileUrl = fileUrl;
                this.thumbnailUrl = thumbnailUrl;
            }
        }
    }

    @Getter
    @Setter
    public static class DownloadMyAssetListOutDTO {
        private List<MyAssetFileUrlOutDTO> myAssetList;

        public DownloadMyAssetListOutDTO(List<MyAssetFileUrlOutDTO> myAssetList) {
            this.myAssetList = myAssetList;
        }

        @Getter
        @Setter
        public static class MyAssetFileUrlOutDTO {
            private Long assetId;
            private String fileUrl;

            public MyAssetFileUrlOutDTO(Long assetId, String fileUrl) {
                this.assetId = assetId;
                this.fileUrl = fileUrl;
            }
        }
    }
}
