package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "asset_tb")
@Entity
@EqualsAndHashCode(of="id")
public class Asset extends MyTimeBaseUtil{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assetName;

    private double price;

    private double size;

    private LocalDate releaseDate;

    private String extension;

    private double rating;

    private Long wishCount;

    private Long visitCount;

    private Long reviewCount;

    private boolean status; // 활성화 여부

    private LocalDateTime updatedAt; // 비즈니스 로직상 찍기 (최신 버전을 찍은 날짜)

    private String fileUrl;

    private String thumbnailUrl;

}
