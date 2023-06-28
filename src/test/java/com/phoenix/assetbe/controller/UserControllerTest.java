package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.order.OrderRequest;
import com.phoenix.assetbe.dto.user.UserRequest;
import com.phoenix.assetbe.dto.user.UserResponse;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.*;
import com.phoenix.assetbe.model.wish.WishList;
import com.phoenix.assetbe.model.wish.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest extends MyRestDoc {

    @Autowired
    private MyTestSetUp myTestSetUp;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();
        User user1 = User.builder().email("test1@nate.com")
                .firstName("hyunjoo")
                .lastName("test")
                .password(passwordEncoder.encode("qwe123!@#"))
                .status(Status.ACTIVE)
                .provider(SocialType.COMMON)
                .role(Role.USER.getRole())
                .emailCheckTokenCreatedAt(LocalDateTime.now())
                .emailCheckToken("testtk")
                .build();

        User user2 = User.builder().email("test2@nate.com")
                .firstName("hyunjoo")
                .lastName("test")
                .password(passwordEncoder.encode("qwe123!@#"))
                .status(Status.INACTIVE)
                .provider(SocialType.COMMON)
                .role(Role.USER.getRole())
                .emailCheckTokenCreatedAt(LocalDateTime.now())
                .emailCheckToken("testtk")
                .build();

        User user3 = User.builder().email("test3@nate.com")
                .firstName("hyunjoo")
                .lastName("test")
                .password(passwordEncoder.encode("qwe123!@#"))
                .status(Status.ACTIVE)
                .provider(SocialType.COMMON)
                .role(Role.USER.getRole())
                .emailCheckTokenCreatedAt(LocalDateTime.now().minusMinutes(15))
                .emailCheckToken("testtk")
                .build();

        User user4 = User.builder().email("test4@nate.com")
                .firstName("hyunjoo")
                .lastName("test")
                .password(passwordEncoder.encode("qwe123!@#"))
                .status(Status.INACTIVE)
                .provider(SocialType.COMMON)
                .role(Role.USER.getRole())
                .emailCheckTokenCreatedAt(LocalDateTime.now().minusMinutes(15))
                .emailCheckToken("testtk")
                .build();

        userRepository.saveAll(Arrays.asList(user1, user2, user3, user4));

        myTestSetUp.saveUserScenario(userList, assetList);
    }
    /**
     * 로그인
     * /login p
     * /login/send p
     * /login/check p
     * /login/change p
     */
    @DisplayName("로그인 성공")
    @Test
    public void login_test() throws Exception {
        //given
        String email = "yuhyunju1@nate.com";
        String password = "qwe123!@#";
        Boolean keepLogin = true;
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail(email);
        loginInDTO.setPassword(password);
        loginInDTO.setKeepLogin(keepLogin);

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(loginInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.userId").value(1L));
    }

    @DisplayName("로그인 실패 : 존재하지 않는 이메일")
    @Test
    public void login_invalid_email_fail_test() throws Exception {
        //given
        String email = "yuhyunju11@nate.com";
        String password = "qwe123!@#";
        Boolean keepLogin = true;
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail(email);
        loginInDTO.setPassword(password);
        loginInDTO.setKeepLogin(keepLogin);

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(loginInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("email"))
                .andExpect(jsonPath("$.data.value").value("존재하지 않는 유저입니다. "));
    }

    @DisplayName("로그인 실패 : 비밀번호 입력 오류")
    @Test
    public void login_invalid_password_fail_test() throws Exception {
        //given
        String email = "yuhyunju1@nate.com";
        String password = "qwe1234!@#";
        Boolean keepLogin = true;
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail(email);
        loginInDTO.setPassword(password);
        loginInDTO.setKeepLogin(keepLogin);

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(loginInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("아이디 혹은 비밀번호를 확인해주세요. "));
    }

    @DisplayName("비밀번호 변경 인증코드 전송 성공")
    @Test
    public void send_password_change_code_test() throws Exception {
        //given
        String firstName = "hyunjoo";
        String lastName = "test";
        String email = "test1@nate.com";

        UserRequest.SendCodeInDTO sendCodeInDTO = new UserRequest.SendCodeInDTO();
        sendCodeInDTO.setEmail(email);
        sendCodeInDTO.setFirstName(firstName);
        sendCodeInDTO.setLastName(lastName);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/send")
                .content(objectMapper.writeValueAsString(sendCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.userId").value(9L));
    }

    @DisplayName("비밀번호 변경 인증코드 전송 실패 : 사용자 이름 불일치")
    @Test
    public void send_password_change_code_invalid_name_test() throws Exception {
        //given
        String firstName = "hyunjoo";
        String lastName = "fail";
        String email = "test1@nate.com";

        UserRequest.SendCodeInDTO sendCodeInDTO = new UserRequest.SendCodeInDTO();
        sendCodeInDTO.setEmail(email);
        sendCodeInDTO.setFirstName(firstName);
        sendCodeInDTO.setLastName(lastName);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/send")
                .content(objectMapper.writeValueAsString(sendCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("name"))
                .andExpect(jsonPath("$.data.value").value("잘못된 요청입니다. "));
    }

    @DisplayName("비밀번호 변경 인증코드 전송 실패 : 없는 이메일")
    @Test
    public void send_password_change_code_invalid_email_test() throws Exception {
        //given
        String firstName = "hyunjoo";
        String lastName = "test";
        String email = "test11@nate.com";

        UserRequest.SendCodeInDTO sendCodeInDTO = new UserRequest.SendCodeInDTO();
        sendCodeInDTO.setEmail(email);
        sendCodeInDTO.setFirstName(firstName);
        sendCodeInDTO.setLastName(lastName);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/send")
                .content(objectMapper.writeValueAsString(sendCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("email"))
                .andExpect(jsonPath("$.data.value").value("존재하지 않는 유저입니다. "));
    }

    @DisplayName("비밀번호 변경 인증코드 확인 성공")
    @Test
    public void check_password_change_code_test() throws Exception {
        //given
        Long userId = 9L;
        String email = "test1@nate.com";
        String code = "testtk";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("비밀번호 변경 인증코드 확인 실패 : 없는 이메일")
    @Test
    public void check_password_change_code_invalid_email_test() throws Exception {
        //given
        Long userId = 9L;
        String email = "test11@nate.com";
        String code = "testtk";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("email"))
                .andExpect(jsonPath("$.data.value").value("존재하지 않는 유저입니다. "));
    }

    @DisplayName("비밀번호 변경 인증코드 확인 실패 : 인증코드 불일치")
    @Test
    public void check_password_change_code_invalid_code_test() throws Exception {
        //given
        Long userId = 9L;
        String email = "test1@nate.com";
        String code = "testfa";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("code"))
                .andExpect(jsonPath("$.data.value").value("잘못된 인증코드 입니다. "));
    }

    @DisplayName("비밀번호 변경 인증코드 확인 실패 : 인증코드 만료")
    @Test
    public void check_password_change_code_expired_code_test() throws Exception {
        //given
        Long userId = 11L;
        String email = "test3@nate.com";
        String code = "testtk";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("code"))
                .andExpect(jsonPath("$.data.value").value("유효하지 않은 인증코드 입니다. "));
    }

    @DisplayName("비밀번호 변경 성공")
    @Test
    public void change_password_test() throws Exception {
        //given
        String email = "test1@nate.com";
        String password = "change123!@#";
        String  code = "testtk";
        UserRequest.ChangePasswordInDTO changePasswordInDTO = new UserRequest.ChangePasswordInDTO();
        changePasswordInDTO.setEmail(email);
        changePasswordInDTO.setPassword(password);
        changePasswordInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/change")
                .content(objectMapper.writeValueAsString(changePasswordInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("비밀번호 변경 실패 : 인증코드 입력 안함")
    @Test
    public void change_password_null_code_test() throws Exception {
        //given
        String email = "test1@nate.com";
        String password = "change123!@#";
        String  code = null;
        UserRequest.ChangePasswordInDTO changePasswordInDTO = new UserRequest.ChangePasswordInDTO();
        changePasswordInDTO.setEmail(email);
        changePasswordInDTO.setPassword(password);
        changePasswordInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/change")
                .content(objectMapper.writeValueAsString(changePasswordInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("code"))
                .andExpect(jsonPath("$.data.value").value("인증코드를 입력해주세요. "));
    }

    @DisplayName("비밀번호 변경 실패 : 인증코드 불일치")
    @Test
    public void change_password_invalid_code_test() throws Exception {
        //given
        String email = "test1@nate.com";
        String password = "change123!@#";
        String  code = "testfa";
        UserRequest.ChangePasswordInDTO changePasswordInDTO = new UserRequest.ChangePasswordInDTO();
        changePasswordInDTO.setEmail(email);
        changePasswordInDTO.setPassword(password);
        changePasswordInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/login/change")
                .content(objectMapper.writeValueAsString(changePasswordInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("code"))
                .andExpect(jsonPath("$.data.value").value("잘못된 인증코드 입니다. "));
    }

    /**
     * 회원가입
     * /signup/duplicate
     * /signup/send
     * /signup/check
     * /signup
     */

    @DisplayName("이메일 중복 체크 성공")
    @Test
    public void check_email_duplicate_test() throws Exception {
        //given
        String email = "test5@nate.com";
        UserRequest.CheckEmailInDTO checkEmailInDTO = new UserRequest.CheckEmailInDTO();
        checkEmailInDTO.setEmail(email);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup/duplicate")
                .content(objectMapper.writeValueAsString(checkEmailInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("이메일 중복 체크 : 중복")
    @Test
    public void check_email_duplicate_fail_test() throws Exception {
        //given
        String email = "test1@nate.com";
        UserRequest.CheckEmailInDTO checkEmailInDTO = new UserRequest.CheckEmailInDTO();
        checkEmailInDTO.setEmail(email);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup/duplicate")
                .content(objectMapper.writeValueAsString(checkEmailInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("email"))
                .andExpect(jsonPath("$.data.value").value("이미 존재하는 이메일입니다. "));
    }

    @DisplayName("회원가입 인증코드 전송 성공")
    @Test
    public void send_signup_code_test() throws Exception {
        //given
        String firstName = "hyunju";
        String lastName = "yu";
        String email = "test5@nate.com";

        UserRequest.SendCodeInDTO sendCodeInDTO = new UserRequest.SendCodeInDTO();
        sendCodeInDTO.setEmail(email);
        sendCodeInDTO.setFirstName(firstName);
        sendCodeInDTO.setLastName(lastName);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup/send")
                .content(objectMapper.writeValueAsString(sendCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.userId").value(13L));
    }


    @DisplayName("회원가입 인증코드 확인 성공")
    @Test
    public void check_signup_code_test() throws Exception {
        //given
        Long userId = 10L;
        String email = "test2@nate.com";
        String code = "testtk";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원가입 인증코드 확인 실패 : 없는 이메일")
    @Test
    public void check_signup_code_invalid_email_fail_test() throws Exception {
        //given
        Long userId = 10L;
        String email = "test11@nate.com";
        String code = "testtk";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원가입 인증코드 확인 실패 : 인증번호 불일치")
    @Test
    public void check_signup_code_invalid_code_fail_test() throws Exception {
        //given
        Long userId = 10L;
        String email = "test2@nate.com";
        String code = "testfa";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("code"))
                .andExpect(jsonPath("$.data.value").value("잘못된 인증코드 입니다. "));
    }

    @DisplayName("회원가입 인증코드 확인 실패 : 인증번호 만료")
    @Test
    public void check_signup_code_expired_code_fail_test() throws Exception {
        //given
        Long userId = 10L;
        String email = "test4@nate.com";
        String code = "testtk";
        UserRequest.CheckCodeInDTO checkCodeInDTO = new UserRequest.CheckCodeInDTO();
        checkCodeInDTO.setUserId(userId);
        checkCodeInDTO.setEmail(email);
        checkCodeInDTO.setCode(code);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup/check")
                .content(objectMapper.writeValueAsString(checkCodeInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원가입 성공")
    @Test
    public void signup_test() throws Exception {
        //given
        String firstName = "hyunjoo";
        String lastName = "test";
        String email = "test1@nate.com";
        String password = "qwe123!@#";
        UserRequest.SignupInDTO signupInDTO = new UserRequest.SignupInDTO();
        signupInDTO.setFirstName(firstName);
        signupInDTO.setLastName(lastName);
        signupInDTO.setEmail(email);
        signupInDTO.setPassword(password);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(signupInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원가입 실패 : 이메일 인증 미완료")
    @Test
    public void signup_before_check_fail_test() throws Exception {
        //given
        String firstName = "hyunjoo";
        String lastName = "test";
        String email = "test2@nate.com";
        String password = "qwe123!@#";
        UserRequest.SignupInDTO signupInDTO = new UserRequest.SignupInDTO();
        signupInDTO.setFirstName(firstName);
        signupInDTO.setLastName(lastName);
        signupInDTO.setEmail(email);
        signupInDTO.setPassword(password);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(signupInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("email"))
                .andExpect(jsonPath("$.data.value").value("존재하지 않는 유저입니다. "));
    }

    @DisplayName("회원가입 실패 : 이름 불일치")
    @Test
    public void signup_invalid_name_fail_test() throws Exception {
        //given
        String firstName = "hyunjoo";
        String lastName = "fail";
        String email = "test1@nate.com";
        String password = "qwe123!@#";
        UserRequest.SignupInDTO signupInDTO = new UserRequest.SignupInDTO();
        signupInDTO.setFirstName(firstName);
        signupInDTO.setLastName(lastName);
        signupInDTO.setEmail(email);
        signupInDTO.setPassword(password);
        // when
        ResultActions resultActions = mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(signupInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("name"))
                .andExpect(jsonPath("$.data.value").value("잘못된 요청입니다. "));
    }

    /**
     * 마이페이지
     */
    @DisplayName("비밀번호 확인 성공")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void check_password_test() throws Exception {
        // given
        Long userId = 2L;

        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setUserId(userId);
        checkPasswordInDTO.setPassword("qwe123!@#");

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/check")
                .content(objectMapper.writeValueAsString(checkPasswordInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("비밀번호 확인 실패 : 비밀번호 불일치")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void check_password_fail_test() throws Exception {
        // given
        Long userId = 2L;

        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setUserId(userId);
        checkPasswordInDTO.setPassword("qwe123!@#$");

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/check")
                .content(objectMapper.writeValueAsString(checkPasswordInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("badRequest"))
                .andExpect(jsonPath("$.data.key").value("password"))
                .andExpect(jsonPath("$.data.value").value("비밀번호가 일치하지 않습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원탈퇴 성공")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_test() throws Exception {
        // given
        Long id = 2L;

        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setMessage("아파서 쉽니다.");

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/{id}/withdraw", id)
                .content(objectMapper.writeValueAsString(withdrawInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").isEmpty());
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원탈퇴 실패 : 권한 체크 실패")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_fail_test() throws Exception {
        // given
        Long id = 3L;

        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setMessage("아파서 쉽니다.");

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/{id}/withdraw", id)
                .content(objectMapper.writeValueAsString(withdrawInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.msg").value("forbidden"))
                .andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원정보 수정 성공")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_test() throws Exception {
        // given
        Long id = 2L;

        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setNewPassword("qwe123!@#$");

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/{id}", id)
                .content(objectMapper.writeValueAsString(updateInDTO))
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원정보 수정 실패 : 권한 체크 실패")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_fail_test() throws Exception {
        // given
        Long id = 3L;

        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setNewPassword("5678");

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/{id}", id)
                .content(objectMapper.writeValueAsString(updateInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 회원정보 조회 성공")
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_my_info_test() throws Exception {
        // given
        Long id = 2L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user"));

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.id").value(2));
        resultActions.andExpect(jsonPath("$.data.firstName").value("jaegeun2"));
        resultActions.andExpect(jsonPath("$.data.lastName").value("song"));
        resultActions.andExpect(jsonPath("$.data.email").value("songjaegeun2@nate.com"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 회원정보 조회 실패 : 인증 실패")
    @Test
    public void get_my_info_fail_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    /**
     * 내 에셋
     */
    @DisplayName("내 에셋 조회 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_my_asset_list_test() throws Exception {
        // given
        Long id = 1L;
        String page = "1";
        String size = "4";

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/assets", id)
                .param("page", page)
                .param("size", size));

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 에셋 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_my_asset_list_fail_test() throws Exception {
        // given
        Long id = 2L;
        String page = "0";
        String size = "4";

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/assets", id)
                .param("page", page)
                .param("size", size));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 에셋 검색 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_my_asset_test() throws Exception {
        // given
        Long id = 1L;
        String page = "0";
        String size = "14";

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/assets/search", id)
                .param("keyword", "woman", "cute")
                .param("page", page)
                .param("size", size));

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 에셋 검색 실패 : 권한 체크 실패") // id 다른 경우
    @WithUserDetails(value = "songjaegeun2@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_my_asset_fail_test() throws Exception {
        // given
        Long id = 1L;
        String page = "1";
        String size = "2";
        List<String> keywordList = Arrays.asList("cute man", "cute boy");

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/assets/search", id)
                .param("keyword", keywordList.toArray(new String[0]))
                .param("page", page)
                .param("size", size));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 에셋 다운 성공")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void download_my_asset_test() throws Exception {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(1L, 3L);


        UserRequest.DownloadMyAssetInDTO downloadMyAssetInDTO = new UserRequest.DownloadMyAssetInDTO();
        downloadMyAssetInDTO.setUserId(userId);
        downloadMyAssetInDTO.setAssets(assets);

        String requestBody = objectMapper.writeValueAsString(downloadMyAssetInDTO);
        System.out.println("테스트 request : " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/download")
                .content(objectMapper.writeValueAsString(downloadMyAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 에셋 다운 실패 : 권한 체크 실패")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void download_my_asset_fail1_test() throws Exception {
        // given
        Long userId = 2L;
        List<Long> assets = Arrays.asList(1L, 3L);


        UserRequest.DownloadMyAssetInDTO downloadMyAssetInDTO = new UserRequest.DownloadMyAssetInDTO();
        downloadMyAssetInDTO.setUserId(userId);
        downloadMyAssetInDTO.setAssets(assets);

        String requestBody = objectMapper.writeValueAsString(downloadMyAssetInDTO);
        System.out.println("테스트 request : " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/download")
                .content(objectMapper.writeValueAsString(downloadMyAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 에셋 다운 실패 : 해당 에셋 보유 안함")
    @WithUserDetails(value = "yuhyunju1@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void download_my_asset_fail2_test() throws Exception {
        // given
        Long userId = 1L;
        List<Long> assets = Arrays.asList(9L, 11L);


        UserRequest.DownloadMyAssetInDTO downloadMyAssetInDTO = new UserRequest.DownloadMyAssetInDTO();
        downloadMyAssetInDTO.setUserId(userId);
        downloadMyAssetInDTO.setAssets(assets);

        String requestBody = objectMapper.writeValueAsString(downloadMyAssetInDTO);
        System.out.println("테스트 request : " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/download")
                .content(objectMapper.writeValueAsString(downloadMyAssetInDTO))
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("No match"));
        resultActions.andExpect(jsonPath("$.data.value").value("잘못된 요청입니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
