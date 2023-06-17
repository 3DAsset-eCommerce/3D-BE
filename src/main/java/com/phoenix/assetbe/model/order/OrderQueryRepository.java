package com.phoenix.assetbe.model.order;

import com.phoenix.assetbe.dto.order.OrderResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.order.QOrder.order;
import static com.phoenix.assetbe.model.order.QOrderProduct.orderProduct;
import static com.phoenix.assetbe.model.order.QPayment.payment;

@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<OrderResponse.OrderOutDTO> getOrderListByUserId(Long userId) {
        return queryFactory
                .select(Projections.constructor(OrderResponse.OrderOutDTO.class,
                        order.id,
                        order.createdAt,
                        payment.totalPrice,
                        JPAExpressions
                                .select(orderProduct.count())
                                .from(orderProduct)
                                .where(orderProduct.order.eq(order))
                ))
                .from(order)
                .leftJoin(payment).on(order.eq(payment.order))
                .leftJoin(orderProduct).on(order.eq(orderProduct.order))
                .where(order.user.id.eq(userId))
                .groupBy(order.id)
                .fetch();
    }
}