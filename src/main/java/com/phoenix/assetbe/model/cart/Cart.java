package com.phoenix.assetbe.model.cart;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "cart_tb")
@Entity
@EqualsAndHashCode(of="id", callSuper=false)
public class Cart extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
}
