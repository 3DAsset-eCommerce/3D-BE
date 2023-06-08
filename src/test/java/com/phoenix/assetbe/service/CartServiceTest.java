package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.service.AssetService;
import com.phoenix.assetbe.service.CartService;
import com.phoenix.assetbe.service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceTest extends DummyEntity {

    @Mock
    private CartRepository cartRepository;

    private CartService cartService;

    // 주입한 서비스
    @Mock
    private UserService userService;

    @Mock
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartService(cartRepository, userService, assetService); //주입
    }

    @Test
    public void testAddCart() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        User user  = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //when : ~을 했을 때 ~을 return 하도록 설정 후, 메서드 호출
        when(userService.findUserById(1L)).thenReturn(user);

        Asset asset1 = newAsset("에셋1");
        Asset asset2 = newAsset("에셋2");
        when(assetService.findAssetById(1L)).thenReturn(asset1);
        when(assetService.findAssetById(2L)).thenReturn(asset2);

        cartService.addCart(addCartInDTO, myUserDetails);

        //then : 메서드 호출 횟수 확인
        verify(userService, times(1)).findUserById(anyLong());
        verify(assetService, times(assets.size())).findAssetById(anyLong());
        verify(cartRepository, times(assets.size())).save(any(Cart.class));
    }

    @Test
    public void testAddCart_InvalidUser() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        User user  = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //when : ~했을 때 ~예외
        when(userService.findUserById(userId)).thenThrow(new Exception400("id", "존재하지 않는 사용자입니다. "));

        assertThrows(Exception400.class, () -> cartService.addCart(addCartInDTO, myUserDetails));

        //then
        verify(userService, times(1)).findUserById(anyLong());
        verify(assetService, never()).findAssetById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddCart_InvalidAsset() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(userId);
        addCartInDTO.setAssets(assets);

        User user  = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //when
        when(userService.findUserById(1L)).thenReturn(user);
        when(assetService.findAssetById(1L)).thenReturn(Asset.builder().build());
        when(assetService.findAssetById(2L)).thenThrow(new Exception400("id", "존재하지 않는 에셋입니다. "));

        //then
        assertThrows(Exception400.class, () -> cartService.addCart(addCartInDTO, myUserDetails));

        verify(userService, times(1)).findUserById(anyLong());
        verify(assetService, times(2)).findAssetById(anyLong());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testDeleteCart() {
        // given
        Long userId = 1L;
        List<Long> carts = Arrays.asList(1L, 2L);

        CartRequest.DeleteCartInDTO deleteCartInDTO  = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);

        User user  = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        Asset asset1 = newAsset("에셋1");
        Asset asset2 = newAsset("에셋2");
        Cart cart1 = Cart.builder().user(user).asset(asset1).build();
        Cart cart2 = Cart.builder().user(user).asset(asset2).build();
        cartRepository.save(cart1);
        cartRepository.save(cart2);

        when(cartRepository.findById(1L)).thenReturn(Optional.ofNullable(cart1));
        when(cartRepository.findById(2L)).thenReturn(Optional.ofNullable(cart2));

        //when : ~을 했을 때 ~을 return 하도록 설정 후, 메서드 호출
        cartService.deleteCart(deleteCartInDTO, myUserDetails);

        //then : 메서드 호출 횟수 확인
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(cartRepository, times(carts.size())).findById(anyLong());
        verify(cartRepository, times(carts.size())).deleteById(anyLong());
    }
}