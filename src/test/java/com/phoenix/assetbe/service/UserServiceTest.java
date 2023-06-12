package com.phoenix.assetbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.dto.UserOutDTO;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

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

    /**
     * 마이페이지
     */
    @Test
    public void testCheckPasswordService() throws Exception {
        // given
        Long userId = 1L;

        UserInDTO.CheckPasswordInDTO checkPasswordInDTO = new UserInDTO.CheckPasswordInDTO();
        checkPasswordInDTO.setId(userId);
        checkPasswordInDTO.setPassword("1234");

        String requestBody = objectMapper.writeValueAsString(checkPasswordInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 송재근 = newMockUser(1L, "송", "재근");
        userRepository.save(송재근);

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // stub 1
        when(userRepository.findById(userId)).thenReturn(Optional.of(송재근));

        // when
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails);

        // then
        Assertions.assertThat(bCryptPasswordEncoder.matches(checkPasswordInDTO.getPassword(), 송재근.getPassword())).isTrue();
        Mockito.verify(userRepository, times(1)).findById(anyLong());
    }
}
