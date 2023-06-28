package com.phoenix.assetbe.model.asset;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "category_tb")
@Entity
@EqualsAndHashCode(of="id")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String categoryName;


    /**
     * 메소드
     */
    public void changeCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
