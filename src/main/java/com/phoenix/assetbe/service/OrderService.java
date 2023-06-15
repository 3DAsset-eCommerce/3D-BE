package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.order.OrderRequest;
import com.phoenix.assetbe.dto.order.OrderResponse;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.order.*;
import com.phoenix.assetbe.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserService userService;
    private final AssetService assetService;

    @Transactional
    public OrderResponse.OrderOutDTO orderAssetsService(OrderRequest.OrderAssetsInDTO orderAssetsInDTO, MyUserDetails myUserDetails) {
        User user = userService.findUserByEmail(orderAssetsInDTO.getEmail());

        Payment payment = Payment.builder()
                .totalPrice(orderAssetsInDTO.getTotalPrice())
                .paymentTool(orderAssetsInDTO.getPaymentTool())
                .build();
        Order order = Order.builder().user(user).phoneNumber(orderAssetsInDTO.getPhoneNumber()).payment(payment).build();
        payment.mappingOrder(order);

        List<Asset> orderAssetList = assetService.findAllAssetById(orderAssetsInDTO.getAssetList());
        Double totalPrice = 0D;
        List<OrderProduct> orderProductList = new ArrayList<>();
        for(Asset orderAsset : orderAssetList){
            OrderProduct orderProduct = OrderProduct.builder().order(order).asset(orderAsset).build();
            orderProductList.add(orderProduct);
            totalPrice += orderAsset.getPrice();
        }

        if (!totalPrice.equals(payment.getTotalPrice())) {
            throw new Exception400("totalPrice", "정확한 금액을 입력해주세요");
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
        orderProductRepository.saveAll(orderProductList);

        return new OrderResponse.OrderOutDTO(order.getId());
    }
}
