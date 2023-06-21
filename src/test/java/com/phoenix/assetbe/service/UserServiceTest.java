package com.phoenix.assetbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.user.UserRequest;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.asset.MyAssetQueryRepository;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.mockito.Mock;

import javax.mail.AuthenticationFailedException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@DisplayName("유저 서비스 TEST")
public class UserServiceTest extends DummyEntity {

    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private MyAssetQueryRepository myAssetQueryRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private AssetService assetService;
    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(authenticationManager, javaMailSender, bCryptPasswordEncoder, userRepository, myAssetQueryRepository, assetService);
    }

    /**
     * 마이페이지
     */
    @Test
    public void testCheckPasswordService() throws Exception {
        // given
        Long userId = 1L;
        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setUserId(userId);
        checkPasswordInDTO.setPassword("qwe123!@#");

        String requestBody = objectMapper.writeValueAsString(checkPasswordInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 송재근 = newMockUser(1L, "송", "재근");
        when(userRepository.findById(userId)).thenReturn(Optional.of(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails);

        // then
        assertTrue(bCryptPasswordEncoder.matches(checkPasswordInDTO.getPassword(), 송재근.getPassword()));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testWithdrawService() throws Exception {
        // given
        Long userId = 1L;
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setMessage("아파서 쉽니다.");

        String requestBody = objectMapper.writeValueAsString(withdrawInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 송재근 = newMockUser(1L, "송", "재근");
        when(userRepository.findById(userId)).thenReturn(Optional.of(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        userService.withdrawService(userId, withdrawInDTO, myUserDetails);

        // then
        assertEquals(withdrawInDTO.getMessage(), 송재근.getReason());
        assertEquals(Status.INACTIVE, 송재근.getStatus());
    }

    @Test
    public void testUpdateService() throws Exception {
        // given
        Long userId = 1L;

        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setNewPassword("5678");

        String requestBody = objectMapper.writeValueAsString(updateInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 송재근 = newMockUser(1L, "송", "재근");
        userRepository.save(송재근);

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // stub 1
        when(userRepository.findById(userId)).thenReturn(Optional.of(송재근));

        // when
        userService.updateService(userId, updateInDTO, myUserDetails);

        // then
        Assertions.assertThat(bCryptPasswordEncoder.matches(updateInDTO.getNewPassword(), 송재근.getPassword()));
    }

    @Test
    public void testGetMyInfoService() throws Exception {
        // given
        Long userId = 1L;

        User 송재근 = newMockUser(1L, "송", "재근");

        when(userRepository.findById(any())).thenReturn(Optional.of(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        UserResponse.GetMyInfoOutDTO getMyInfoOutDTO = userService.getMyInfoService(userId, myUserDetails);

        // then
        verify(userRepository, times(1)).findById(userId);
    }

    /**
     * 내 에셋
     */
    @Test
    public void testGetMyAssetListService() throws Exception {
        // given
        Long userId = 1L;

        User 송재근 = newMockUser(1L, "송", "재근");

        when(userRepository.findById(any())).thenReturn(Optional.of(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO asset1 = new UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO(1L, "Asset 1", "fileUrl1", "thumbnailUrl1");
        UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO asset2 = new UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO(2L, "Asset 2", "fileUrl2", "thumbnailUrl2");
        UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO asset3 = new UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO(3L, "Asset 3", "fileUrl3", "thumbnailUrl3");
        Page<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> fakePage = new PageImpl<>(List.of(asset1, asset2, asset3));

        Pageable pageable = PageRequest.of(0, 2); // 예시로 페이지 번호 0, 페이지 크기 10으로 설정

        // when
        when(myAssetQueryRepository.getMyAssetListWithUserIdAndPaging(anyLong(), any(Pageable.class))).thenReturn(fakePage);

        UserResponse.MyAssetListOutDTO result = userService.getMyAssetListService(userId, pageable, myUserDetails);

        // then
        assertEquals(fakePage.getContent().size(), result.getMyAssetList().size());
        assertEquals(fakePage.getNumber(), result.getCurrentPage());
        assertEquals(fakePage.getTotalPages(), result.getTotalPage());
        assertEquals(fakePage.getTotalElements(), result.getTotalElement());

        verify(myAssetQueryRepository, times(1)).getMyAssetListWithUserIdAndPaging(userId, pageable);
    }

    @Test
    public void testSearchMyAsset() throws Exception {
        // Given
        Long userId = 2L;

        User 송재근 = newMockUser(2L, "송", "재근");

        when(userRepository.findById(any())).thenReturn(Optional.of(송재근));

        List<String> keywordList = new ArrayList<>();
        UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO asset1 = new UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO(1L, "Asset 1", "fileUrl1", "thumbnailUrl1");
        UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO asset2 = new UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO(2L, "Asset 2", "fileUrl2", "thumbnailUrl2");
        UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO asset3 = new UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO(3L, "Asset 3", "fileUrl3", "thumbnailUrl3");
        Page<UserResponse.MyAssetListOutDTO.GetMyAssetOutDTO> fakePage = new PageImpl<>(List.of(asset1, asset2, asset3));

        Pageable pageable = PageRequest.of(0, 2); // 예시로 페이지 번호 0, 페이지 크기 10으로 설정

        when(myAssetQueryRepository.searchMyAssetListWithUserIdAndPagingAndKeyword(anyLong(), anyList(), any(Pageable.class))).thenReturn(fakePage);

        // When
        UserResponse.MyAssetListOutDTO result = userService.searchMyAssetService(userId, keywordList, pageable, new MyUserDetails(송재근));

        // Then
        verify(myAssetQueryRepository, times(1))
                .searchMyAssetListWithUserIdAndPagingAndKeyword(userId, keywordList, pageable);
    }

    @Test
    public void testDownloadMyAssetService() throws Exception {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 2L, 5L);

        User 유현주 = newMockUser(1L, "유", "현주");

        List<UserResponse.DownloadMyAssetListOutDTO.MyAssetFileUrlOutDTO> myAssetFileUrlOutDTO = new ArrayList<>();
        myAssetFileUrlOutDTO.add(new UserResponse.DownloadMyAssetListOutDTO.MyAssetFileUrlOutDTO(1L, "fileUrl1"));
        myAssetFileUrlOutDTO.add(new UserResponse.DownloadMyAssetListOutDTO.MyAssetFileUrlOutDTO(2L, "fileUrl2"));
        myAssetFileUrlOutDTO.add(new UserResponse.DownloadMyAssetListOutDTO.MyAssetFileUrlOutDTO(5L, "fileUrl5"));

        UserRequest.DownloadMyAssetInDTO downloadMyAssetInDTO = new UserRequest.DownloadMyAssetInDTO();
        downloadMyAssetInDTO.setUserId(userId);
        downloadMyAssetInDTO.setAssets(assets);

        MyUserDetails myUserDetails = new MyUserDetails(유현주);

        // stub 1
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(유현주));

        // stub 2
        when(myAssetQueryRepository.downloadMyAssetByAssetId(any())).thenReturn(myAssetFileUrlOutDTO);

        // when
        UserResponse.DownloadMyAssetListOutDTO downloadMyAssetListOutDTO = userService.downloadMyAssetService(downloadMyAssetInDTO, myUserDetails);

        // then
        assertNotNull(downloadMyAssetListOutDTO);
    }
}
