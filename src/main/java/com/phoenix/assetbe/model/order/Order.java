package com.phoenix.assetbe.model.order;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import com.phoenix.assetbe.model.user.User;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "order_tb")
@Entity
@EqualsAndHashCode(of="id", callSuper=false)
public class Order extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String phoneNumber;

    @OneToOne(mappedBy = "order")
    private Payment payment;

}
