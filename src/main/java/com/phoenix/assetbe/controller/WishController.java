package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.cart.CartRequest;
import com.phoenix.assetbe.dto.cart.CartResponse;
import com.phoenix.assetbe.dto.wishList.WishRequest;
import com.phoenix.assetbe.dto.wishList.WishResponse;
import com.phoenix.assetbe.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @PostMapping("/s/wishlist/add")
    public ResponseEntity<?> addWish(@RequestBody WishRequest.AddWishInDTO addWishInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        wishService.addWishService(addWishInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/s/wishlist/delete")
    public ResponseEntity<?> deleteWish(@RequestBody WishRequest.DeleteWishInDTO deleteWishInDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        wishService.deleteWishService(deleteWishInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/user/{id}/wishlist")
    public ResponseEntity<?> getWishList(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        List<WishResponse.GetWishListWithOrderAndCartOutDTO> wishList = wishService.getWishListService(id, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(wishList);
        return ResponseEntity.ok().body(responseDTO);
    }

}
