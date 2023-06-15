package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.dto.order.OrderRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.order.OrderProductRepository;
import com.phoenix.assetbe.model.order.OrderRepository;
import com.phoenix.assetbe.model.order.PaymentRepository;
import com.phoenix.assetbe.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@DisplayName("주문 서비스 TEST")
public class OrderServiceTest extends DummyEntity {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private PaymentRepository paymentRepository;

    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, paymentRepository, orderProductRepository, userService, assetService);
    }

    @Test
    @DisplayName("주문 성공")
    void testOrderAssets() {
        // given
        Long userId = 1L;
        List<Long> orderAssetList = Arrays.asList(1L, 2L);
        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "유현주@nate.com", "현주", "유", "010-1234-1234", 20000D, "카드");

        // when
        when(userService.findUserByEmail("유현주@nate.com")).thenReturn(user);

        Asset asset1 = newAsset1("에셋1");
        Asset asset2 = newAsset1("에셋2");
        when(assetService.findAllAssetById(orderAssetList)).thenReturn(Arrays.asList(asset1, asset2));

        orderService.orderAssetsService(orderAssetsInDTO, myUserDetails);

        // then
        verify(userService, times(1)).findUserByEmail(anyString());
        verify(assetService, times(1)).findAllAssetById(anyList());
        verify(paymentRepository, times(1)).save(any());
        verify(orderRepository, times(1)).save(any());
        verify(orderProductRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 실패 : 금액 불일치")
    void testOrderAssets_priceCheckFail() {
        // given
        Long userId = 1L;
        List<Long> orderAssetList = Arrays.asList(1L, 2L);
        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        OrderRequest.OrderAssetsInDTO orderAssetsInDTO
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "유현주@nate.com", "현주", "유", "010-1234-1234", 10000D, "카드");

        // when
        when(userService.findUserByEmail("유현주@nate.com")).thenReturn(user);

        Asset asset1 = newAsset1("에셋1");
        Asset asset2 = newAsset1("에셋2");
        when(assetService.findAllAssetById(orderAssetList)).thenReturn(Arrays.asList(asset1, asset2));

        assertThrows(Exception400.class, () -> orderService.orderAssetsService(orderAssetsInDTO, myUserDetails));

        // then
        verify(userService, times(1)).findUserByEmail(anyString());
        verify(assetService, times(1)).findAllAssetById(anyList());
        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verify(orderProductRepository, never()).saveAll(anyList());
    }
}
