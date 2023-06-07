package com.phoenix.assetbe.module.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
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

public class CartServiceTest {

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

        User user = User.builder().id(userId).role(Role.USER).build();
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //when : ~을 했을 때 ~을 return 하도록 설정 후, 메서드 호출
        when(userService.findUserById(1L)).thenReturn(user);

        Asset asset1 = Asset.builder().build();
        Asset asset2 = Asset.builder().build();
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

        User user = User.builder().id(userId).role(Role.USER).build();
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

        User user = User.builder().id(userId).role(Role.USER).build();
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
}