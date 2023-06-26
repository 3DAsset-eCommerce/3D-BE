package com.phoenix.assetbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception401;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.core.util.MailUtils;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.mockito.Mock;

import javax.mail.AuthenticationFailedException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("유저 서비스 TEST")
public class UserServiceTest extends DummyEntity {

    @InjectMocks
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
    private MailUtils mailUtils;
    @Mock
    private AssetService assetService;
    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(authenticationManager, bCryptPasswordEncoder, userRepository, myAssetQueryRepository, assetService);
    }
    /**
     * 로그인
     * passwordChangeService
     */

    @Test
    public void testLoginService() throws Exception {
        // given
        Long userId = 1L;
        String email = "yuhyunju@nate.com";
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail(email);
        loginInDTO.setPassword("qwe123!@#");
        loginInDTO.setKeepLogin(true);

        String requestBody = objectMapper.writeValueAsString(loginInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 유현주 = newMockUser(1L, "유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(유현주);
        Authentication authentication = mock(Authentication.class);

        when(userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE)).thenReturn(Optional.ofNullable(유현주));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);

        // when
        userService.loginService(loginInDTO);

        // then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).getPrincipal();
        verify(userRepository, times(1)).findByUserWithEmailAndStatus(anyString(), any());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testLoginService_WithInvalidCredentials_ShouldThrowException401() throws Exception {
        // given
        Long userId = 1L;
        String email = "yuhyunju@nate.com";
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail(email);
        loginInDTO.setPassword("qwe123!@#");
        loginInDTO.setKeepLogin(true);
        User 유현주 = newMockUser(1L, "유", "현주");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());

        //when
        when(userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE)).thenReturn(Optional.ofNullable(유현주));
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .thenThrow(new AuthenticationException("Authentication failed.") {});
        assertThrows( Exception401.class, () -> userService.loginService(loginInDTO));

        // then
        verify(userRepository).findByUserWithEmailAndStatus(email, Status.ACTIVE);
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testSendPasswordChangeCodeService() {
        // given
        Long userId = 1L;
        String email = "yuhyunju@nate.com";

        User 유현주 = newMockUser(1L, "hyunju", "yu");
        유현주.generateEmailCheckToken();

        UserRequest.CheckCodeInDTO CheckCodeInDTO = new UserRequest.CheckCodeInDTO();
        CheckCodeInDTO.setUserId(userId);
        CheckCodeInDTO.setEmail(email);
        CheckCodeInDTO.setCode(유현주.getEmailCheckToken());

        when(userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE)).thenReturn(Optional.ofNullable(유현주));

        // when,
        userService.checkPasswordChangeCodeService(CheckCodeInDTO);

        // then
        verify(userRepository, times(1)).findByUserWithEmailAndStatus(anyString(), any());

    }

    @Test
    public void testCheckPasswordChangeCodeService_WithInvalidCode_ShouldThrowException400() {
        // given
        Long userId = 1L;
        String email = "yuhyunju@nate.com";

        User 유현주 = newMockUser(1L, "hyunju", "yu");
        유현주.generateEmailCheckToken();

        UserRequest.CheckCodeInDTO CheckCodeInDTO = new UserRequest.CheckCodeInDTO();
        CheckCodeInDTO.setUserId(userId);
        CheckCodeInDTO.setEmail(email);
        CheckCodeInDTO.setCode("1234");

        when(userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE)).thenReturn(Optional.ofNullable(유현주));

        // when,
        Exception400 exception = assertThrows(Exception400.class, () -> userService.checkPasswordChangeCodeService(CheckCodeInDTO));

        // then
        assertEquals("잘못된 인증코드 입니다. ", exception.getMessage());
        verify(userRepository, times(1)).findByUserWithEmailAndStatus(anyString(), any());

    }

    @Test
    public void testChangePasswordService() {
        // given
        String email = "yuhyunju@nate.com";
        String newPassword = "newpassword";

        User 유현주 = newMockUser(1L, "hyunju", "yu");
        유현주.generateEmailCheckToken();

        UserRequest.ChangePasswordInDTO ChangePasswordInDTO = new UserRequest.ChangePasswordInDTO();
        ChangePasswordInDTO.setEmail(email);
        ChangePasswordInDTO.setCode(유현주.getEmailCheckToken());
        ChangePasswordInDTO.setPassword(newPassword);

        when(userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE)).thenReturn(Optional.of(유현주));

        // when
        userService.changePasswordService(ChangePasswordInDTO);

        // then
        verify(userRepository, times(1)).findByUserWithEmailAndStatus(anyString(), any());
    }

    @Test
    public void testChangePasswordService_WithInvalidCode_ShouldThrowException400() {
        // given
        String email = "yuhyunju@nate.com";
        String newPassword = "newpassword";

        User 유현주 = newMockUser(1L, "hyunju", "yu");
        유현주.generateEmailCheckToken();

        UserRequest.ChangePasswordInDTO ChangePasswordInDTO = new UserRequest.ChangePasswordInDTO();
        ChangePasswordInDTO.setEmail(email);
        ChangePasswordInDTO.setCode("1234");
        ChangePasswordInDTO.setPassword(newPassword);
        when(userRepository.findByUserWithEmailAndStatus(email, Status.ACTIVE)).thenReturn(Optional.of(유현주));

        // when
        Exception400 exception = assertThrows(Exception400.class, () -> userService.changePasswordService(ChangePasswordInDTO));

        // then
        assertEquals("code", exception.getKey());
        assertEquals("잘못된 인증코드 입니다. ", exception.getMessage());
        verify(userRepository, times(1)).findByUserWithEmailAndStatus(anyString(), any());
    }
    /**
     *
     *
     *
     * signupService
     */

    @Test
    void testCheckEmailDuplicateService() {
        // given
        String notExistingEmail = "notExisting_email@example.com";
        UserRequest.CheckEmailInDTO CheckEmailInDTO = new UserRequest.CheckEmailInDTO();
        CheckEmailInDTO.setEmail(notExistingEmail);

        when(userRepository.existsByEmailAndStatus(notExistingEmail, Status.ACTIVE)).thenReturn(false);

        // when,
        userService.checkEmailDuplicateService(CheckEmailInDTO);

        // then
        verify(userRepository).existsByEmailAndStatus(anyString(), any());
        verify(userRepository, times(1)).existsByEmailAndStatus(anyString(), any());
    }

    @Test
    void testCheckEmailDuplicateService_WithExistingEmail_ShouldThrowException400() {
        // given
        String existingEmail = "existing_email@example.com";
        UserRequest.CheckEmailInDTO CheckEmailInDTO = new UserRequest.CheckEmailInDTO();
        CheckEmailInDTO.setEmail(existingEmail);

        when(userRepository.existsByEmailAndStatus(existingEmail, Status.ACTIVE)).thenReturn(true);

        // when, then
        Exception400 exception = assertThrows(Exception400.class, () -> userService.checkEmailDuplicateService(CheckEmailInDTO));
        assertEquals("email", exception.getKey());
        assertEquals("이미 존재하는 이메일입니다. ", exception.getMessage());
        verify(userRepository).existsByEmailAndStatus(anyString(), any());
        verify(userRepository, times(1)).existsByEmailAndStatus(anyString(), any());
    }

    @Test
    void testCheckSignupCodeService() {
        // given
        Long userId = 1L;
        String email = "yuhyunju@nate.com";

        User 유현주 = User.builder().email(email).id(userId).status(Status.INACTIVE).build();
        유현주.generateEmailCheckToken();

        UserRequest.CheckCodeInDTO CheckCodeInDTO = new UserRequest.CheckCodeInDTO();
        CheckCodeInDTO.setUserId(userId);
        CheckCodeInDTO.setEmail(email);
        CheckCodeInDTO.setCode(유현주.getEmailCheckToken());

        when(userRepository.findById(userId)).thenReturn(Optional.of(유현주));

        // when
        userService.checkSignupCodeService(CheckCodeInDTO);

        // then
        assertEquals(Status.ACTIVE, 유현주.getStatus());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testCheckSignupCodeService_WithInvalidCode_ShouldThrowException400() {
        // given
        Long userId = 1L;
        String email = "yuhyunju@nate.com";

        User 유현주 = User.builder().email(email).id(userId).status(Status.INACTIVE).build();
        유현주.generateEmailCheckToken();

        UserRequest.CheckCodeInDTO CheckCodeInDTO = new UserRequest.CheckCodeInDTO();
        CheckCodeInDTO.setUserId(userId);
        CheckCodeInDTO.setEmail(email);
        CheckCodeInDTO.setCode("invalid");

        when(userRepository.findById(userId)).thenReturn(Optional.of(유현주));

        // when
        Exception400 exception = assertThrows(Exception400.class, () -> userService.checkSignupCodeService(CheckCodeInDTO));

        // then
        assertEquals("code", exception.getKey());
        assertEquals("잘못된 인증코드 입니다. ", exception.getMessage());
        assertEquals(Status.INACTIVE, 유현주.getStatus()); // 상태가 INACTIVE 상태로 유지되었는지 확인
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testSignupService_WithValidRequest_ShouldChangePassword() {
        // given
        String email = "yuhyunju@nate.com";
        String firstName = "hyunju";
        String lastName = "yu";
        String changePassword = "testpassword1!";
        User 유현주 = User.builder().firstName(firstName).lastName(lastName).email(email).id(1L).status(Status.ACTIVE).build();

        UserRequest.SignupInDTO signupInDTO = new UserRequest.SignupInDTO();
        signupInDTO.setEmail(email);
        signupInDTO.setFirstName(firstName);
        signupInDTO.setLastName(lastName);
        signupInDTO.setPassword(changePassword);

        when(userRepository.findByUserWithEmailAndStatus(email,Status.ACTIVE)).thenReturn(Optional.ofNullable(유현주));

        // when
        userService.signupService(signupInDTO);

        // then
        verify(userRepository, times(1)).findByUserWithEmailAndStatus(anyString(), any());
        assertTrue(bCryptPasswordEncoder.matches(changePassword, 유현주.getPassword()));
    }

    @Test
    void testSignupService_WithInvalidName_ShouldThrowException400() {
        // given
        String email = "yuhyunju@nate.com";
        String firstName = "hyunju";
        String lastName = "yu";
        String changePassword = "testpassword1!";
        User 유현주 = User.builder().firstName(firstName).lastName(lastName).email(email).id(1L).status(Status.ACTIVE).build();

        UserRequest.SignupInDTO signupInDTO = new UserRequest.SignupInDTO();
        signupInDTO.setEmail(email);
        signupInDTO.setFirstName(firstName);
        signupInDTO.setLastName("Smith");
        signupInDTO.setPassword(changePassword);

        when(userRepository.findByUserWithEmailAndStatus(email,Status.ACTIVE)).thenReturn(Optional.ofNullable(유현주));

        // when
        Exception400 exception = assertThrows(Exception400.class, () -> userService.signupService(signupInDTO));

        // then
        verify(userRepository, times(1)).findByUserWithEmailAndStatus(anyString(), any());
        assertEquals("name", exception.getKey());
        assertEquals("잘못된 요청입니다. ", exception.getMessage());
        assertNotEquals(bCryptPasswordEncoder.encode(changePassword), 유현주.getPassword());
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
        when(userRepository.findByUserWithIdAndStatus(any(), any(Status.class))).thenReturn(Optional.ofNullable(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails);

        // then
        assertTrue(bCryptPasswordEncoder.matches(checkPasswordInDTO.getPassword(), 송재근.getPassword()));
        verify(userRepository, times(1)).findByUserWithIdAndStatus(anyLong(), any());
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
        when(userRepository.findByUserWithIdAndStatus(any(), any(Status.class))).thenReturn(Optional.ofNullable(송재근));

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
        when(userRepository.findByUserWithIdAndStatus(any(), any(Status.class))).thenReturn(Optional.ofNullable(송재근));

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

        when(userRepository.findByUserWithIdAndStatus(any(), any(Status.class))).thenReturn(Optional.ofNullable(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        UserResponse.GetMyInfoOutDTO getMyInfoOutDTO = userService.getMyInfoService(myUserDetails);

        // then
        verify(userRepository, times(1)).findByUserWithIdAndStatus(anyLong(), any());
    }

    /**
     * 내 에셋
     */
    @Test
    public void testGetMyAssetListService() throws Exception {
        // given
        Long userId = 1L;

        User 송재근 = newMockUser(1L, "송", "재근");

        when(userRepository.existsByIdAndStatus(any(), any(Status.class))).thenReturn(true);

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

        when(userRepository.existsByIdAndStatus(any(), any(Status.class))).thenReturn(true);

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
        when(userRepository.existsByIdAndStatus(any(), any(Status.class))).thenReturn(true);

        // stub 2
        when(myAssetQueryRepository.downloadMyAssetByAssetId(any())).thenReturn(myAssetFileUrlOutDTO);

        // when
        UserResponse.DownloadMyAssetListOutDTO downloadMyAssetListOutDTO = userService.downloadMyAssetService(downloadMyAssetInDTO, myUserDetails);

        // then
        assertNotNull(downloadMyAssetListOutDTO);
    }
}
