package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "asset_tb")
@Entity
@EqualsAndHashCode(of="id", callSuper=false)
public class Asset extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assetName;

    private Double price;

    private Double size;

    private LocalDate releaseDate;

    private String extension;

    private String creator;

    private Double rating;

    private Long wishCount;

    private Long visitCount;

    private Long reviewCount;

    private boolean status; // 활성화 여부

    private LocalDateTime updatedAt; // 비즈니스 로직상 찍기 (최신 버전을 찍은 날짜)

    private String fileUrl;

    private String thumbnailUrl;

    public void increaseVisitCount(){
        this.visitCount++;
    }

    public void calculateRatingAndIncreaseReviewCount(Asset asset, Double reviewRatingSum){
        this.rating = (double) Math.round(reviewRatingSum * 10 / (asset.getReviewCount() + 1)) / 10;
        this.reviewCount = asset.getReviewCount() + 1;
    }

    public void calculateRatingOnUpdateReview(Asset asset, Double reviewRatingSum){
        this.rating = (double) Math.round(reviewRatingSum * 10 / asset.getReviewCount()) / 10;
    }
}
