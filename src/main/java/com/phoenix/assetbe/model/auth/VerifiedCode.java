package com.phoenix.assetbe.model.auth;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import com.phoenix.assetbe.model.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Table(name = "verified_code_tb")
@Entity
@EqualsAndHashCode(of="id")
public class VerifiedCode extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 인증 번호, 인증 상태(인증 전, 인증 됨), 이메일
    private String email;
    private String emailCheckToken;
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public VerifiedCode(Long id, String email,String emailCheckToken, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.emailCheckToken = emailCheckToken;
        this.updatedAt = updatedAt;
    }
}
