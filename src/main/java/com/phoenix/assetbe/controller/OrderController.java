package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.ResponseDTO;
import com.phoenix.assetbe.dto.order.OrderRequest;
import com.phoenix.assetbe.dto.order.OrderResponse;
import com.phoenix.assetbe.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/s/order")
    public ResponseEntity<?> orderAssets(@RequestBody OrderRequest.OrderAssetsInDTO orderAssetsInDTO , @AuthenticationPrincipal MyUserDetails myUserDetails){
        OrderResponse.OrderAssetsOutDTO orderAssetsOutDTO = orderService.orderAssetsService(orderAssetsInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(orderAssetsOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/s/user/{id}/orders")
    public ResponseEntity<?> getOrderList(@PageableDefault(size = 7, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("id") Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        OrderResponse.OrderOutDTO orderOutDTO = orderService.getOrderListService(id, pageable, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(orderOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }
}
