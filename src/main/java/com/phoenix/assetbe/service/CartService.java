package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
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
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    @Transactional
    public void addCart(Long userId, List<Long> assets, MyUserDetails myUserDetails) {
        // 요청한 사용자가 id의 주인인지 확인 -> UserService로
        // 담기를 요청한 에셋이 있는지 확인 -> AsserService로

        if (!myUserDetails.getUser().getId().equals(userId)) {
            throw new Exception403("장바구니에 접근할 권한이 없습니다. ");
        }

        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 사용자입니다. "));

        for (Long assetId : assets) {
            Asset assetPS = assetRepository.findById(assetId).orElseThrow(
                    () -> new Exception400("id", "존재하지 않는 에셋입니다. "));
            Cart cart = Cart.builder().user(userPS).asset(assetPS).build();
            cartRepository.save(cart);
        }
    }
}
