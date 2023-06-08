package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.core.exception.Exception500;
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
    public void addCart(CartRequest.AddCartInDTO addCartInDTO, MyUserDetails myUserDetails) {

        Long userId = addCartInDTO.getUserId();
        List<Long> assets = addCartInDTO.getAssets();

        userService.authCheck(myUserDetails, userId);
        User userPS = userService.findUserById(userId);

        for (Long assetId : assets) {
            Asset assetPS = assetService.findAssetById(assetId);
            Cart cart = Cart.builder().user(userPS).asset(assetPS).build();
            try {
                cartRepository.save(cart);
            } catch (Exception e) {
                throw new Exception500("장바구니 담기 실패 : "+e.getMessage());
            }
        }
    }

    @Transactional
    public void deleteCart(CartRequest.DeleteCartInDTO deleteCartInDTO, MyUserDetails myUserDetails) {
        Long userId = deleteCartInDTO.getUserId();
        List<Long> carts = deleteCartInDTO.getCarts();

        userService.authCheck(myUserDetails, userId);

        for (Long cartId : carts) {
            findCartById(cartId);
            try {
                cartRepository.deleteById(cartId);
            } catch (Exception e) {
                throw new Exception500("장바구니 삭제 실패 : "+e.getMessage());
            }
        }
    }

    private void findCartById(Long cartId){
        cartRepository.findById(cartId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 장바구니입니다. "));
    }
}
