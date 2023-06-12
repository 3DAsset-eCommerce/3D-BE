package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;
    @MockBean
    JavaMailSender javaMailSender; //이메일 전송 테스트

    @BeforeEach
    public void setUp() {

        userRepository.save(dummy.newUser("유", "현주")); // id 순서 주의
        userRepository.save(dummy.newUser("송", "재근"));
        userRepository.save(dummy.newUser("양", "진호"));
        userRepository.save(dummy.newUser("이", "지훈"));

        entityManager.clear();
    }

    /**
     * 마이페이지
     */
    @DisplayName("비밀번호 확인 성공")
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void check_password_test() throws Exception {
        // given
        Long userId = 2L;

        UserInDTO.CheckPasswordInDTO checkPasswordInDTO = new UserInDTO.CheckPasswordInDTO();
        checkPasswordInDTO.setId(userId);
        checkPasswordInDTO.setPassword("1234");

        String requestBody = objectMapper.writeValueAsString(checkPasswordInDTO);
        System.out.println("request 테스트: " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/check").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response 테스트: " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("비밀번호 확인 실패") // 비밀번호 일치 X
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void check_password_fail_test() throws Exception {
        // given
        Long userId = 2L;

        UserInDTO.CheckPasswordInDTO checkPasswordInDTO = new UserInDTO.CheckPasswordInDTO();
        checkPasswordInDTO.setId(userId);
        checkPasswordInDTO.setPassword("5678");

        String requestBody = objectMapper.writeValueAsString(checkPasswordInDTO);
        System.out.println("request 테스트: " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/check").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response 테스트: " + responseBody);


        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("password"));
        resultActions.andExpect(jsonPath("$.data.value").value("비밀번호가 일치하지 않습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원탈퇴 성공")
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_test() throws Exception {
        // given
        Long userId = 2L;

        UserInDTO.WithdrawInDTO withdrawInDTO = new UserInDTO.WithdrawInDTO();
        withdrawInDTO.setMessage("아파서 쉽니다.");

        String requestBody = objectMapper.writeValueAsString(withdrawInDTO);
        System.out.println("request 테스트: " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/" + userId + "/withdraw").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response 테스트: " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원탈퇴 실패") // id 다른 경우
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_fail_test() throws Exception {
        // given
        Long userId = 3L;

        UserInDTO.WithdrawInDTO withdrawInDTO = new UserInDTO.WithdrawInDTO();
        withdrawInDTO.setMessage("아파서 쉽니다.");

        String requestBody = objectMapper.writeValueAsString(withdrawInDTO);
        System.out.println("request 테스트: " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/s/user/" + userId + "/withdraw").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response 테스트: " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
