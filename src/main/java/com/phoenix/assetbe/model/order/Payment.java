package com.phoenix.assetbe.model.order;

import com.phoenix.assetbe.core.util.MyTimeBaseUtil;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "payment_tb")
@Entity
@EqualsAndHashCode(of="id", callSuper=false)
public class Payment extends MyTimeBaseUtil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String receiptURL;

    @NotNull
    private Double totalPrice;

    @NotBlank
    private String paymentTool;

    public void mappingOrder(Order order){
        this.order = order;
    }

}
