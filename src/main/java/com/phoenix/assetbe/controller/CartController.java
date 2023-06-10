package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/s/cart/add")
    public ResponseEntity<?> addCart(@RequestBody CartRequest.AddCartInDTO addCartInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        cartService.addCart(addCartInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/cart/delete")
    public ResponseEntity<?> deleteCart(@RequestBody CartRequest.DeleteCartInDTO deleteCartInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        cartService.deleteCart(deleteCartInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/user/{id}/cartCount")
    public ResponseEntity<?> countCart(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        cartService.countCartService(id, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }
}
