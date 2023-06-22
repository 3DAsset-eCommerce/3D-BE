package com.phoenix.assetbe.model.user;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;
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
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }
        this.emailCheckToken = sb.toString();
        this.emailCheckTokenCreatedAt = LocalDateTime.now();
    }
    public void changeWithdrawalMassage(String message) {
        this.reason = message;
    }
    public void changePassword(String password) {
        this.password = password;
    }
    public void changeStatusToINACTIVE() {
        this.status = Status.INACTIVE;
    }
    public void changeStatusToACTIVE() {
        this.status = Status.ACTIVE;
    }
}
