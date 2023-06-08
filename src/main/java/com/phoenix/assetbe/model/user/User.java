package com.phoenix.assetbe.model.user;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
@Entity
@EqualsAndHashCode(of="id")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType provider;

    private String reason;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean emailVerified;

    private String emailCheckToken;

    @UpdateTimestamp
    private LocalDateTime tokenCreatedAt;


    public void setPassword(String password) {
        this.password = password;
    }

    public void setTokenCreatedAt() {
        this.tokenCreatedAt = null;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    @Builder
    public User(Long id, String lastName, String firstName, String email, String password, Status status, Role role, SocialType provider, String reason, LocalDateTime updatedAt, boolean emailVerified, String emailCheckToken, LocalDateTime tokenCreatedAt) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
        this.provider = provider;
        this.reason = reason;
        this.updatedAt = updatedAt;
        this.emailVerified = emailVerified;
        this.emailCheckToken = emailCheckToken;
        this.tokenCreatedAt = tokenCreatedAt;
    }



    public void generateEmailCheckToken() {this.emailCheckToken= UUID.randomUUID().toString();}
    public void setEmailCheckToken(String s) {this.emailCheckToken=s;}
    public void changeWithdrawalMassage(String message) {this.reason = message;}
    public void changePassword(String password) {this.password = password;}
    public void changeStatus() {
        this.status = Status.INACTIVE;
    }
}
