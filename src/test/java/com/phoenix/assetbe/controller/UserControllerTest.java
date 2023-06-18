package com.phoenix.assetbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.MyRestDoc;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.user.UserRequest;
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
    @MockBean
    JavaMailSender javaMailSender; //이메일 전송 테스트

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
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

        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setId(userId);
        checkPasswordInDTO.setPassword("1234");

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
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void check_password_fail_test() throws Exception {
        // given
        Long userId = 2L;

        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setId(userId);
        checkPasswordInDTO.setPassword("5678");

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
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_test() throws Exception {
        // given
        Long id = 2L;

        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setNewPassword("5678");

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
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_my_info_test() throws Exception {
        // given
        Long id = 2L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}", id));

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.id").value(2));
        resultActions.andExpect(jsonPath("$.data.firstName").value("재근"));
        resultActions.andExpect(jsonPath("$.data.lastName").value("송"));
        resultActions.andExpect(jsonPath("$.data.email").value("송재근@nate.com"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 회원정보 조회 실패 : 권한 체크 실패")
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_my_info_fail_test() throws Exception {
        // given
        Long id = 3L;

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}", id));

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다. "));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    /**
     * 내 에셋
     */
    @DisplayName("내 에셋 조회 성공")
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "유현주@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_my_asset_test() throws Exception {
        // given
        Long id = 2L;
        String page = "0";
        String size = "14";
        List<String> keywordList = Arrays.asList("cute man", "cute boy");

        // when
        ResultActions resultActions = mockMvc.perform(get("/s/user/{id}/assets/search", id)
                .param("keyword", keywordList.toArray(new String[0]))
                .param("page", page)
                .param("size", size));

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 에셋 검색 실패 : 권한 체크 실패") // id 다른 경우
    @WithUserDetails(value = "송재근@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
}
