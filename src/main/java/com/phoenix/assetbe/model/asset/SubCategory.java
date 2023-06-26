package com.phoenix.assetbe.model.asset;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "sub_category_tb")
@Entity
@EqualsAndHashCode(of="id")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subCategoryName;

    /**
     * 메소드
     */
    public void changeSubCategory(String subcategoryName) {
        this.subCategoryName = subcategoryName;
    }
}
