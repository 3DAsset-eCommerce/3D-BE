package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.controller.CartController;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.service.CartService;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

public class CartControllerUnitTest extends DummyEntity {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    public void testAddCart() throws Exception {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L);

        CartRequest.AddCartInDTO addCartInDTO = new CartRequest.AddCartInDTO();
        addCartInDTO.setUserId(1L);
        addCartInDTO.setAssets(assets);

        User user  = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        myUserDetails,
                        myUserDetails.getPassword(),
                        myUserDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        doNothing().when(cartService).addCart(addCartInDTO, (MyUserDetails) authentication.getPrincipal());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/s/cart/add")
                .content(new ObjectMapper().writeValueAsString(addCartInDTO))
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
    }

    @Test
    public void testDeleteCart() throws Exception {
        // given
        Long userId = 1L;
        List<Long> carts = Arrays.asList(1L);

        CartRequest.DeleteCartInDTO deleteCartInDTO = new CartRequest.DeleteCartInDTO();
        deleteCartInDTO.setUserId(userId);
        deleteCartInDTO.setCarts(carts);

        User user  = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        myUserDetails,
                        myUserDetails.getPassword(),
                        myUserDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        doNothing().when(cartService).deleteCart(deleteCartInDTO, (MyUserDetails) authentication.getPrincipal());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/s/cart/delete")
                .content(new ObjectMapper().writeValueAsString(deleteCartInDTO))
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
    }
}
