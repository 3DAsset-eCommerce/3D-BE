package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import com.phoenix.assetbe.dto.asset.ReviewRequest;
import com.phoenix.assetbe.model.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "review_tb")
@Entity
@EqualsAndHashCode(of="id", callSuper=false)
public class Review extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @NotBlank
    private String content;

    @NotNull
    private Double rating;

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updatedReview(ReviewRequest.ReviewInDTO updateReviewInDTO) {
        this.content = updateReviewInDTO.getContent();
        this.rating = updateReviewInDTO.getRating();
    }
}
