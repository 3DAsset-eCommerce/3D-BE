package com.phoenix.assetbe.model.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.id = :orderId")
    Optional<Order> findByUserIdAndOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);
}
