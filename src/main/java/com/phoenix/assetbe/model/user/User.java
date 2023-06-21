package com.phoenix.assetbe.model.user;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "user_tb")
@Entity
@EqualsAndHashCode(of="id", callSuper=false)
public class User extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;

    private String firstName;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 활성화 여부

    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType provider;

    private String reason;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenCreatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 메소드
     */
    public void generateEmailCheckToken() {
        this.emailCheckToken= UUID.randomUUID().toString();
        this.emailCheckTokenCreatedAt=LocalDateTime.now();
    }
    public void setEmailCheckToken(String s) {this.emailCheckToken=s;}
    public void changeWithdrawalMassage(String message) {this.reason = message;}
    public void changePassword(String password) {this.password = password;}
    public void changeStatus() {
        this.status = Status.INACTIVE;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
