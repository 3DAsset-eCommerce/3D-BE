package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.dto.order.OrderRequest;
import com.phoenix.assetbe.dto.order.OrderResponse;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.MyAssetRepository;
import com.phoenix.assetbe.model.order.*;
import com.phoenix.assetbe.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private MyAssetRepository myAssetRepository;

    private OrderService orderService;

    @Mock
    private OrderQueryRepository orderQueryRepository;
    @Mock
    private UserService userService;
    @Mock
    private AssetService assetService;
    @Mock
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, paymentRepository, orderProductRepository, myAssetRepository, orderQueryRepository, userService, assetService, cartService);
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
                = new OrderRequest.OrderAssetsInDTO(orderAssetList, "유현주@nate.com", "현주", "유", "010-1234-1234", 2000D, "카드");

        // when
        when(userService.findValidUserByEmail("유현주@nate.com")).thenReturn(user);

        Asset asset1 = newAsset("에셋1", 1000D, 1D, LocalDate.now(), 1D, 1L);
        Asset asset2 = newAsset("에셋2", 1000D, 1D, LocalDate.now(), 1D, 1L);
        when(assetService.findAllAssetById(orderAssetList)).thenReturn(Arrays.asList(asset1, asset2));

        orderService.orderAssetsService(orderAssetsInDTO, myUserDetails);

        // then
        verify(userService, times(1)).findValidUserByEmail(anyString());
        verify(assetService, times(1)).findAllAssetById(anyList());
        verify(paymentRepository, times(1)).save(any());
        verify(orderRepository, times(1)).save(any());
        verify(orderProductRepository, times(1)).saveAll(anyList());
        verify(myAssetRepository, times(1)).saveAll(anyList());
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
        when(userService.findValidUserByEmail("유현주@nate.com")).thenReturn(user);

        Asset asset1 = newAsset("에셋1", 1000D, 1D, LocalDate.now(), 1D, 1L);
        Asset asset2 = newAsset("에셋2", 1000D, 1D, LocalDate.now(), 1D, 1L);
        when(assetService.findAllAssetById(orderAssetList)).thenReturn(Arrays.asList(asset1, asset2));

        assertThrows(Exception400.class, () -> orderService.orderAssetsService(orderAssetsInDTO, myUserDetails));

        // then
        verify(userService, times(1)).findValidUserByEmail(anyString());
        verify(assetService, times(1)).findAllAssetById(anyList());
        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verify(orderProductRepository, never()).saveAll(anyList());
        verify(myAssetRepository,  never()).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 내역 조회 성공")
    void testGetOrderList() {
        // given
        Long userId = 1L;
        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 10);

        // when
        Page<OrderResponse.OrderOutDTO.OrderListOutDTO> orderList = new PageImpl<>(new ArrayList<OrderResponse.OrderOutDTO.OrderListOutDTO>());
        when(orderQueryRepository.getOrderListByUserIdWithPaging(userId, pageable, startDate, endDate)).thenReturn(orderList);

        orderService.getOrderListService(userId, pageable, startDate, endDate, myUserDetails);

        // then
        verify(userService, times(1)).authCheck(any(), anyLong());
        verify(orderQueryRepository, times(1)).getOrderListByUserIdWithPaging(anyLong(), any(), any(), any());
    }

    @Test
    @DisplayName("주문 내역 조회 실패 : 권한 체크 실패")
    void testGetOrderList_AuthCheckFail() {
        // given
        Long userId = 2L;
        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 10);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> orderService.getOrderListService(userId, pageable, startDate, endDate, myUserDetails));


        // then
        verify(userService, times(1)).authCheck(any(), anyLong());
        verify(orderQueryRepository, never()).getOrderListByUserIdWithPaging(anyLong(), any(), any(), any());
    }

    @Test
    @DisplayName("주문 내역 상세 조회 성공")
    void testGetOrderDetails() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        when(orderRepository.findOrderByUserIdAndOrderId(userId, orderId)).thenReturn(Optional.of(Order.builder().id(orderId).build()));
        orderService.getOrderDetailsService(userId, orderId, myUserDetails);

        // then
        verify(userService, times(1)).authCheck(any(), anyLong());
        verify(orderRepository, times(1)).findOrderByUserIdAndOrderId(anyLong(), any());
        verify(orderQueryRepository, times(1)).getOrderDetailsByUserIdAndOrderId(anyLong(), any());
    }

    @Test
    @DisplayName("주문 내역 상세 조회 실패 : 권한 체크 실패")
    void testGetOrderDetails_AuthCheckFail() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> orderService.getOrderDetailsService(userId, orderId, myUserDetails));

                // then
        verify(userService, times(1)).authCheck(any(), anyLong());
        verify(orderRepository, never()).findOrderByUserIdAndOrderId(anyLong(), any());
        verify(orderQueryRepository, never()).getOrderDetailsByUserIdAndOrderId(anyLong(), any());
    }

    @Test
    @DisplayName("주문 내역 상세 조회 실패 : 잘못된 요청")
    void testGetOrderDetails_wrongRequestFail() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        assertThrows(Exception400.class, () -> orderService.getOrderDetailsService(userId, orderId, myUserDetails));

        // then
        verify(userService, times(1)).authCheck(any(), anyLong());
        verify(orderRepository, times(1)).findOrderByUserIdAndOrderId(anyLong(), anyLong());
        verify(orderQueryRepository, never()).getOrderDetailsByUserIdAndOrderId(anyLong(), anyLong());
    }
}
