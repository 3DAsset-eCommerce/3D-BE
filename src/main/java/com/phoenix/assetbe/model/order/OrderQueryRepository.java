package com.phoenix.assetbe.model.order;

import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.phoenix.assetbe.dto.order.OrderResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.order.QOrder.order;
import static com.phoenix.assetbe.model.user.QUser.user;
import static com.phoenix.assetbe.model.order.QOrderProduct.orderProduct;
import static com.phoenix.assetbe.model.order.QPayment.payment;

@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<OrderResponse.OrderOutDTO.OrderListOutDTO> getOrderListByUserIdWithPaging(Long userId, Pageable pageable, LocalDate startDate, LocalDate endDate) {
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
                .where(
                        order.user.id.eq(userId),
                        order.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
                )
                .groupBy(order.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.id.desc())
                .fetch();

        long totalCount = queryFactory
                .select(order.count())
                .from(order)
                .where(
                        order.user.id.eq(userId),
                        order.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }


    public OrderResponse.OrderProductWithDetailsOutDTO getOrderDetailsByUserIdAndOrderId(Long userId, Long orderId) {
        OrderResponse.OrderProductWithDetailsOutDTO.OrderDetailsDTO orderDetails = queryFactory
                 .select(Projections.constructor(OrderResponse.OrderProductWithDetailsOutDTO.OrderDetailsDTO.class,
                         order.id,
                         order.createdAt,
                         payment.paymentTool,
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

    public Page<AdminResponse.OrderListOutDTO.OrderOutDTO> findOrderListByAdmin(String orderPeriod, String startDate, String endDate, String orderNumber, String assetNumber, String assetName, String email, Pageable pageable){
        List<AdminResponse.OrderListOutDTO.OrderOutDTO> result = queryFactory
                .selectDistinct(Projections.constructor(AdminResponse.OrderListOutDTO.OrderOutDTO.class,
                        order.id,
                        order.createdAt,
                        JPAExpressions.select(assetName(assetName))
                                .from(orderProduct)
                                .innerJoin(asset).on(asset.id.eq(orderProduct.asset.id))
                                .where(order.id.eq(orderProduct.order.id), assetNameEq(assetName)),
                        JPAExpressions.select(orderProduct.id.count())
                                .from(orderProduct)
                                .where(order.id.eq(orderProduct.order.id)),
                        user.email,
                        payment.totalPrice,
                        payment.paymentTool,
                        payment.id)
                )
                .from(order)
                .innerJoin(user).on(user.id.eq(order.user.id))
                .innerJoin(payment).on(payment.id.eq(order.payment.id))
                .innerJoin(orderProduct).on(orderProduct.order.id.eq(order.id))
                .innerJoin(asset).on(asset.id.eq(orderProduct.asset.id))
                .where(orderPeriodBetween(orderPeriod, startDate, endDate), orderNumberEq(orderNumber), emailEq(email), assetNameEq(assetName), assetNumberEq(assetNumber))
                .groupBy(order.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(order.id.countDistinct())
                .from(order)
                .innerJoin(user).on(user.id.eq(order.user.id))
                .innerJoin(orderProduct).on(orderProduct.order.id.eq(order.id))
                .innerJoin(asset).on(asset.id.eq(orderProduct.asset.id))
                .where(orderPeriodBetween(orderPeriod, startDate, endDate), orderNumberEq(orderNumber), emailEq(email), assetNameEq(assetName), assetNumberEq(assetNumber))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    BooleanExpression orderPeriodBetween(String orderPeriod, String startDate, String endDate) {
        LocalDateTime end = LocalDateTime.now();

        if(orderPeriod == null){
            return null;
        }
        if (orderPeriod.equals("today")) {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            return order.createdAt.between(start, end);
        }
        if (orderPeriod.equals("oneWeek")) {
            LocalDateTime start = end.minusWeeks(1);
            return order.createdAt.between(start, end);
        }
        if (orderPeriod.equals("oneMonth")) {
            LocalDateTime start = end.minusMonths(1);
            return order.createdAt.between(start, end);
        }
        if (orderPeriod.equals("threeMonth")) {
            LocalDateTime start = end.minusMonths(3);
            return order.createdAt.between(start, end);
        }
        if (orderPeriod.equals("oneYear")) {
            LocalDateTime start = end.minusYears(1);
            return order.createdAt.between(start, end);
        }

        if(startDate == null || endDate == null) {
            return null;
        }else{
            LocalDateTime from = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            LocalDateTime to = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
            return order.createdAt.between(from, to);
        }
    }

    private BooleanExpression orderNumberEq(String orderNumber) {
        if(orderNumber == null){
            return null;
        }
        Long orderId = Long.parseLong(orderNumber.substring(9));
        return order.id.eq(orderId);
    }

    private BooleanExpression emailEq(String email){
        if(email == null){
            return null;
        }
        return user.email.eq(email);
    }

    private BooleanExpression assetNameEq(String assetName) {
        if(assetName == null){
            return null;
        }
        return asset.assetName.eq(assetName);
    }

    private BooleanExpression assetNumberEq(String assetNumber) {
        if(assetNumber == null){
            return null;
        }
        Long assetId = Long.parseLong(assetNumber.substring(9));
        return asset.id.eq(assetId);
    }

    private StringExpression assetName(String assetName) {
        if(assetName == null){
            return asset.assetName.min();
        }
        return asset.assetName;
    }

    private OrderSpecifier<?> orderSort(Pageable pageable) {

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                com.querydsl.core.types.Order direction = order.getDirection().isAscending() ? com.querydsl.core.types.Order.ASC : Order.DESC;
                switch (order.getProperty()){
                    case "createdAt":
                        return new OrderSpecifier<>(direction, QOrder.order.createdAt);
                }
            }
        }
        return null;
    }
}