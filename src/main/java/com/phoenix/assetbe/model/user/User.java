package com.phoenix.assetbe.model.user;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
@Entity
@EqualsAndHashCode(of="id")
public class User extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastname;

    private String firstname;

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

    private boolean emailVerified;

    public void setPassword(String password) {
        this.password = password;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    @Builder
    public User(Long id, String lastname, String firstname, String email, String password, Status status, Role role, SocialType provider, String reason, LocalDateTime updatedAt, boolean emailVerified) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
        this.provider = provider;
        this.reason = reason;
        this.updatedAt = updatedAt;
        this.emailVerified = emailVerified;
    }
}
