package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartQueryRepository;
import com.phoenix.assetbe.model.cart.CartRepository;
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

@DisplayName("장바구니 서비스 TEST")
public class CartServiceTest extends DummyEntity {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartQueryRepository cartQueryRepository;

    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartService(cartRepository, cartQueryRepository, userService, assetService);
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    void testAddCart() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        when(userService.findUserById(1L)).thenReturn(user);

        Asset asset1 = newAsset("에셋1");
        Asset asset2 = newAsset("에셋2");
        when(assetService.findAssetById(1L)).thenReturn(asset1);
        when(assetService.findAssetById(2L)).thenReturn(asset2);

        cartService.addCart(addCartInDTO, myUserDetails);

        // then
        verify(userService, times(1)).findUserById(anyLong());
        verify(assetService, times(1)).findAllAssetById(anyList());
        verify(cartRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("장바구니 담기 실패 : 존재하지 않는 유저")
    void testAddCart_InvalidUser() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        when(userService.findUserById(userId)).thenThrow(new Exception400("id", "존재하지 않는 사용자입니다."));

        assertThrows(Exception400.class, () -> cartService.addCart(addCartInDTO, myUserDetails));

        // then
        verify(userService, times(1)).findUserById(anyLong());
        verify(assetService, never()).findAllAssetById(anyList());
        verify(cartRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("장바구니 담기 실패 : 권한 체크 실패")
    void testDAddCart_AuthCheckFail() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        User user = newUser("유", "현주");
        User user2 = newUser("김", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user2);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> cartService.addCart(addCartInDTO, myUserDetails));

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(cartRepository, never()).deleteAllById(anyList());
    }

    @Test
    @DisplayName("장바구니 삭제 성공")
    void testDeleteCart() {
        // given
        Long userId = 1L;
        List<Long> carts = Arrays.asList(1L, 2L);

        CartRequest.DeleteCartInDTO deleteCartInDTO = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        Asset asset1 = newAsset("에셋1");
        Asset asset2 = newAsset("에셋2");
        Cart cart1 = Cart.builder().user(user).asset(asset1).build();
        Cart cart2 = Cart.builder().user(user).asset(asset2).build();
        cartRepository.save(cart1);
        cartRepository.save(cart2);

        // when
        when(cartRepository.findAllById(carts)).thenReturn(Arrays.asList(cart1, cart2));

        cartService.deleteCart(deleteCartInDTO, myUserDetails);

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(cartRepository, times(1)).deleteAllById(anyList());
    }

    @Test
    @DisplayName("장바구니 삭제 실패 : 권한 체크 실패")
    void testDeleteCart_AuthCheckFail() {
        // given
        Long userId = 1L;
        List<Long> carts = Arrays.asList(1L, 2L);

        CartRequest.DeleteCartInDTO deleteCartInDTO = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);

        User user = newUser("유", "현주");
        User user2 = newUser("김", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user2);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> cartService.deleteCart(deleteCartInDTO, myUserDetails));

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(cartRepository, never()).deleteAllById(anyList());
    }

    @Test
    @DisplayName("장바구니 개수 조회 성공")
    void testCountCart() {
        // given
        Long userId = 1L;

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        Asset asset1 = newAsset("에셋1");
        Asset asset2 = newAsset("에셋2");
        Cart cart1 = Cart.builder().user(user).asset(asset1).build();
        Cart cart2 = Cart.builder().user(user).asset(asset2).build();
        cartRepository.save(cart1);
        cartRepository.save(cart2);

        // when
        cartService.countCartService(userId, myUserDetails);

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(cartQueryRepository, times(1)).countByUserId(anyLong());
    }

    @Test
    @DisplayName("장바구니 개수 조회 실패 : 권한 체크 실패")
    void testCountCart_AuthCheckFail() {
        // given
        Long userId = 1L;

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        Asset asset1 = newAsset("에셋1");
        Asset asset2 = newAsset("에셋2");
        Cart cart1 = Cart.builder().user(user).asset(asset1).build();
        Cart cart2 = Cart.builder().user(user).asset(asset2).build();
        cartRepository.save(cart1);
        cartRepository.save(cart2);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> cartService.countCartService(userId, myUserDetails));

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(cartQueryRepository, never()).countByUserId(anyLong());
    }
}