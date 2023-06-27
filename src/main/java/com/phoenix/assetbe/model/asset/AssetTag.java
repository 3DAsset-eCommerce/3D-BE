package com.phoenix.assetbe.model.asset;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "asset_tag_tb")
@Entity
@EqualsAndHashCode(of="id")
public class AssetTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    /**
     * 메소드
     */
    public void changeTag(Tag tag) {
        this.tag = tag;
    }

    public void changeAssetTag(Tag tag, Category category, SubCategory subCategory) {
        if (category != null && subCategory != null) {
            this.category = category;
            this.subCategory = subCategory;
        } else if (category == null && subCategory != null) {
            this.subCategory = subCategory;
        } else if (subCategory == null && category != null) {
            this.category = category;
        }
        this.tag = tag;
    }

}
