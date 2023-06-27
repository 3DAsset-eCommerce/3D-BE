package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "preview_tb")
@Entity
@EqualsAndHashCode(of="id", callSuper=false)
public class Preview extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @NotBlank
    private String previewUrl;

    /**
     * 메소드
     */
    public void changePreviewUrl(String previewUrl) { // 1, 3, 4
        this.previewUrl = previewUrl;
    }

}