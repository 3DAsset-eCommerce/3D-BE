package com.phoenix.assetbe.model.auth.code;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
