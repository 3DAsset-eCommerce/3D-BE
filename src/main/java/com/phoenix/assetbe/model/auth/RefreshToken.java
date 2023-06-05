package com.phoenix.assetbe.model.auth;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "refresh_token_tb")
@Entity
@EqualsAndHashCode(of="id")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //user 1:1

    private String refreshToken;
}
