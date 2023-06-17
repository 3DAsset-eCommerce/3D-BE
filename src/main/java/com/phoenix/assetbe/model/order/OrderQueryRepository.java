package com.phoenix.assetbe.model.order;

import com.phoenix.assetbe.dto.order.OrderResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.order.QOrder.order;
import static com.phoenix.assetbe.model.order.QOrderProduct.orderProduct;
import static com.phoenix.assetbe.model.order.QPayment.payment;

@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<OrderResponse.OrderOutDTO.OrderListOutDTO> getOrderListByUserIdWithPaging(Long userId, Pageable pageable) {
        List<OrderResponse.OrderOutDTO.OrderListOutDTO> result = queryFactory
                .select(Projections.constructor(OrderResponse.OrderOutDTO.OrderListOutDTO.class,
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.id.desc())
                .fetch();

        long totalCount = queryFactory
                .select(order.count())
                .from(order)
                .where(order.user.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }


    public OrderResponse.OrderProductWithDetailsOutDTO getOrderDetailsByUserIdAndOrderId(Long userId, Long orderId) {
        OrderResponse.OrderProductWithDetailsOutDTO.OrderDetailsDTO orderDetails = queryFactory
                 .select(Projections.constructor(OrderResponse.OrderProductWithDetailsOutDTO.OrderDetailsDTO.class,
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
                 .where(order.id.eq(orderId))
                 .fetchFirst();

        List<OrderResponse.OrderProductWithDetailsOutDTO.OrderProductOutDTO> orderProductList = queryFactory
                .select(Projections.constructor(OrderResponse.OrderProductWithDetailsOutDTO.OrderProductOutDTO.class,
                        orderProduct.asset))
                .from(orderProduct)
                .innerJoin(orderProduct.asset, asset)
                .where(orderProduct.order.id.eq(orderId))
                .fetch();

        return new OrderResponse.OrderProductWithDetailsOutDTO(orderProductList, orderDetails);
    }
}