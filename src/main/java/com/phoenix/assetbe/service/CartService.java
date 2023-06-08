package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
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

        List<Asset> assetList = assetService.findAllAssetById(assets);
        List<Cart> cartList = new ArrayList<>();

        for (Asset asset : assetList) {
            Cart cart = Cart.builder().user(userPS).asset(asset).build();
            cartList.add(cart);
        }

        try {
            cartRepository.saveAll(cartList);
        } catch (Exception e) {
            throw new Exception500("장바구니 담기 실패 : "+e.getMessage());
        }
    }

    @Transactional
    public void deleteCart(CartRequest.DeleteCartInDTO deleteCartInDTO, MyUserDetails myUserDetails) {
        Long userId = deleteCartInDTO.getUserId();
        List<Long> carts = deleteCartInDTO.getCarts();

        userService.authCheck(myUserDetails, userId);

        try {
            cartRepository.deleteAllById(carts);
        } catch (Exception e) {
            throw new Exception500("장바구니 삭제 실패 : "+e.getMessage());
        }
    }

    public void findCartById(Long cartId){
        cartRepository.findById(cartId).orElseThrow(
                () -> new Exception400("id", "존재하지 않는 장바구니입니다. "));
    }

    public List<Cart> findAllCartById(List<Long> cartIds){
        List<Cart> cartList = cartRepository.findAllById(cartIds);
        return cartList;
    }
}
