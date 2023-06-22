package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private String description;

    private Integer discount;

    private Double discountPrice;

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

    @Builder
    public Asset(Long id, String assetName, Double price, String description, Integer discount, Double size, LocalDate releaseDate, String extension, String creator, Double rating, Long wishCount, Long visitCount, Long reviewCount, boolean status, LocalDateTime updatedAt, String fileUrl, String thumbnailUrl) {
        this.id = id;
        this.assetName = assetName;
        this.price = price;
        this.description = description;
        this.discount = discount;
        this.size = size;
        this.releaseDate = releaseDate;
        this.extension = extension;
        this.creator = creator;
        this.rating = rating;
        this.wishCount = wishCount;
        this.visitCount = visitCount;
        this.reviewCount = reviewCount;
        this.status = status;
        this.updatedAt = updatedAt;
        this.fileUrl = fileUrl;
        this.thumbnailUrl = thumbnailUrl;

        if(price != null && discount != null){
            this.discountPrice = price - (price * (discount / 100.0));
        }
    }

    /**
     * 메소드
     */
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

    public void calculateRatingOnDeleteReview(Asset asset, Double reviewRatingSum){
        this.rating = (double) Math.round(reviewRatingSum * 10 / (asset.getReviewCount() - 1)) / 10;
        this.reviewCount = asset.getReviewCount() - 1;
    }

    public void calculateRatingOnDeleteReview(Asset asset){
        this.rating = 0D;
        this.reviewCount = asset.getReviewCount() - 1;
    }

    public void changeStatusToINACTIVE() {
        this.status = false;
    }

    public void changeStatusToACTIVE() {
        this.status = true;
    }
}
