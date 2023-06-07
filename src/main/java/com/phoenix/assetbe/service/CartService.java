package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final AssetService assetService;

    @Transactional
    public void addCart(CartRequest.AddCartDTO addCartDTO, MyUserDetails myUserDetails) {

        Long userId = addCartDTO.getUserId();
        List<Long> assets = addCartDTO.getAssets();

        if (!myUserDetails.getUser().getId().equals(userId)) {
            throw new Exception403("장바구니에 접근할 권한이 없습니다. ");
        }

        User userPS = userService.findUserById(userId);

        for (Long assetId : assets) {
            Asset assetPS = assetService.findAssetById(assetId);
            Cart cart = Cart.builder().user(userPS).asset(assetPS).build();
            cartRepository.save(cart);
        }
    }
}
