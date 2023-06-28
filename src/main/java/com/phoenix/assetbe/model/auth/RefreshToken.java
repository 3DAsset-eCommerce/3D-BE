package com.phoenix.assetbe.model.auth;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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
    @NotBlank
    private String refreshToken;
}
