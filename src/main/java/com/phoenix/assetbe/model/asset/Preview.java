package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;

import javax.persistence.*;
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
    @JoinColumn(name = "asset_id")
    private Asset asset;

    private String previewUrl;

    /**
     * 메소드
     */
    public void changePreviewUrl(String previewUrl) { // 1, 3, 4
        this.previewUrl = previewUrl;
    }

}