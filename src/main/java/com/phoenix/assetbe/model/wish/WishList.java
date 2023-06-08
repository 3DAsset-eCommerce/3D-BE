package com.phoenix.assetbe.model.wish;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "wishlist_tb")
@Entity
@EqualsAndHashCode(of="id")
public class WishList extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

}
