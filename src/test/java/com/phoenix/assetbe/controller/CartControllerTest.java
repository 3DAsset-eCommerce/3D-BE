package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.dto.CartRequest;
import com.phoenix.assetbe.model.user.Role;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.service.CartService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class CartControllerTest {

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
        // Prepare test data
        CartRequest.AddCartDTO addCartDto = new CartRequest.AddCartDTO();
        addCartDto.setUserId(1L);
        addCartDto.setAssets(Collections.singletonList(1L));

        User user  = User.builder().id(1L).role(Role.USER).build();
        MyUserDetails myUserDetails = new MyUserDetails(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        myUserDetails,
                        myUserDetails.getPassword(),
                        myUserDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock service method
        doNothing().when(cartService).addCart(addCartDto.getUserId(), addCartDto.getAssets(), (MyUserDetails) authentication.getPrincipal());

        // Perform the POST request
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/s/cart/add")
                .content(new ObjectMapper().writeValueAsString(addCartDto))
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the request and assert the response
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
    }
}
