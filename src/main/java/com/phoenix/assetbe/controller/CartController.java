package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.annotation.MyLog;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.cart.CartRequest;
import com.phoenix.assetbe.dto.cart.CartResponse;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @MyLog
    @PostMapping("/s/cart/add")
    public ResponseEntity<?> addCart(@RequestBody CartRequest.AddCartInDTO addCartInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        cartService.addCartService(addCartInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @PostMapping("/s/cart/delete")
    public ResponseEntity<?> deleteCart(@RequestBody CartRequest.DeleteCartInDTO deleteCartInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        cartService.deleteCartService(deleteCartInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @GetMapping("/s/user/{id}/cartCount")
    public ResponseEntity<?> countCart(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        CartResponse.CountCartOutDTO countCartOutDTO = cartService.countCartService(id, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(countCartOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @MyLog
    @GetMapping("/s/user/{id}/cart")
    public ResponseEntity<?> getCartList(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        List<CartResponse.GetCartWithOrderOutDTO> cartList = cartService.getCartListService(id, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(cartList);
        return ResponseEntity.ok().body(responseDTO);
    }
}
