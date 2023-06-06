package com.phoenix.assetbe.module.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.service.CartService;
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
    private UserRepository userRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCart() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        User user = User.builder().id(userId).role(Role.USER).build();
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.of(Asset.builder().build()));

        cartService.addCart(userId, assets, myUserDetails);

        //then
        verify(userRepository, times(1)).findById(userId);
        verify(assetRepository, times(assets.size())).findById(any(Long.class));
        verify(cartRepository, times(assets.size())).save(any(Cart.class));
    }

    @Test
    public void testAddCart_InvalidUser() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        User user = User.builder().id(1L).role(Role.USER).build();
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Exception403 exception = assertThrows(Exception403.class,
                () -> cartService.addCart(2L, assets, myUserDetails)); //요청한 사용자 id와 다른 id를 요청

        //then
        assertEquals("장바구니에 접근할 권한이 없습니다. ", exception.getMessage());

        verify(userRepository, times(0)).findById(userId);
        verifyNoInteractions(assetRepository);
        verifyNoInteractions(cartRepository);
    }

    @Test
    public void testAddCart_InvalidAsset() {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L);

        User user = User.builder().id(userId).role(Role.USER).build();
        MyUserDetails myUserDetails = new MyUserDetails(user);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Exception400 exception = assertThrows(Exception400.class,
                () -> cartService.addCart(userId, assets, myUserDetails));

        //then
        assertEquals("존재하지 않는 에셋입니다. ", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(assetRepository, times(1)).findById(1L);
        verifyNoInteractions(cartRepository);
    }
}