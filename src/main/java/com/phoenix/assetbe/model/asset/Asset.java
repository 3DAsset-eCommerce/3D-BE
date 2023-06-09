package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import com.phoenix.assetbe.dto.admin.AdminRequest;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank
    private String assetName;

    @NotNull
    private Double price;

    @NotBlank
    private String description;

    @NotNull
    private Integer discount;

    @NotNull
    private Double discountPrice;

    @NotNull
    private Double size; // v

    @NotNull
    private LocalDate releaseDate; // v

    @NotBlank
    private String extension; // v

    @NotBlank
    private String creator; // v

    private Double rating;

    private Long wishCount;

    private Long visitCount;

    private Long reviewCount;

    @NotNull
    private boolean status; // 활성화 여부

    private LocalDateTime updatedAt; // 비즈니스 로직상 찍기 (최신 버전을 찍은 날짜)

    @NotBlank
    private String fileUrl;

    @NotBlank
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
    public void increaseVisitCount() {
        if(visitCount == null){
            this.visitCount = 1L;
        }
        this.visitCount++;
    }
    public void calculateRatingAndIncreaseReviewCount(Asset asset, Double reviewRatingSum){
        this.rating = (double) Math.round(reviewRatingSum * 10 / (asset.getReviewCount() + 1)) / 10;
        this.reviewCount = asset.getReviewCount() + 1;
    }
    public void calculateRatingOnUpdateReview(Asset asset, Double reviewRatingSum){this.rating = (double) Math.round(reviewRatingSum * 10 / asset.getReviewCount()) / 10;}
    public void calculateRatingOnDeleteReview(Asset asset, Double reviewRatingSum){
        this.rating = (double) Math.round(reviewRatingSum * 10 / (asset.getReviewCount() - 1)) / 10;
        this.reviewCount = asset.getReviewCount() - 1;
    }
    public void calculateRatingOnDeleteReview(Asset asset){
        this.rating = 0D;
        this.reviewCount = asset.getReviewCount() - 1;
    }

    // Asset status 변경 메소드
    public void changeStatusToINACTIVE() {this.status = false;}
    public void changeStatusToACTIVE() {this.status = true;}

    // Asset Url 묶음 변경 메소드
    public void changeFileUrl(String fileUrl) {this.fileUrl = fileUrl;}
    public void changeThumbnailUrl(String thumbnailUrl) {this.thumbnailUrl = thumbnailUrl;}

    // Asset Name, description, price, discount, extension, size 변경 메소드
    public void changeAssetName(String assetName) {this.assetName = assetName;}
    public void changeAssetDescription(String assetDescription) {this.description = assetDescription;}
    public void changePrice(Double price) {this.price = price;}
    public void changeExtension(String extension) {this.extension = extension;}
    public void changeFileSize(Double fileSize) {this.size = fileSize;}
    public void changeDiscountAndDiscountPrice(Integer discount) {
        this.discount = discount;
        this.discountPrice = this.price * ((100 - discount) / 100); // 할인 후 가격 저장
    }

    // WishCount 변경 메소드
    public void increaseWishCount(){
        if(wishCount == null){this.wishCount = 1L;}
        this.wishCount++;
    }
    public void decreaseWishCount(){this.wishCount--;}

    // updatedAt 메소드
    public void onUpdate(){this.updatedAt = LocalDateTime.now();}

    // asset 등록 메소드
    public void addAssetDetails(AdminRequest.AddAssetInDTO addAssetInDTO){
        this.assetName = addAssetInDTO.getAssetName();
        this.price = addAssetInDTO.getPrice();
        this.description = addAssetInDTO.getAssetDescription();
        this.discount = addAssetInDTO.getDiscount();
        this.size = addAssetInDTO.getFileSize();
        this.releaseDate = LocalDate.now();
        this.extension = addAssetInDTO.getExtension();
        this.creator = "NationA";
        this.status = true;
        this.fileUrl = addAssetInDTO.getFileUrl();
        this.thumbnailUrl = addAssetInDTO.getThumbnailUrl();

        if(price != null && discount != null){
            this.discountPrice = price - (price * (discount / 100.0));
        }
    }
}
