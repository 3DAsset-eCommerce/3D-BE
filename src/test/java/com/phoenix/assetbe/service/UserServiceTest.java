package com.phoenix.assetbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserServiceTest extends DummyEntity {

    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JavaMailSender javaMailSender;

    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 해당 서비스 필드 전부 주입
        userService = new UserService(authenticationManager,  javaMailSender,
                bCryptPasswordEncoder, userRepository);
    }
}
