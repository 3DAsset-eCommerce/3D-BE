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

    private Long subCategoryCount;
}
