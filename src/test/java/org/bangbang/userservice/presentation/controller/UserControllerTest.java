package org.bangbang.userservice.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bangbang.userservice.application.dto.TokenInfo;
import org.bangbang.userservice.application.dto.UserRegister;
import org.bangbang.userservice.application.service.TokenGenerateService;
import org.bangbang.userservice.application.service.UserRegisterService;
import org.bangbang.userservice.application.service.UserUpdateService;
import org.bangbang.userservice.presentation.dto.TokenRequest;
import org.bangbang.userservice.presentation.dto.UserRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // [변경] 새로운 import 경로
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// 1. properties 설정 추가 (JWT_ISSUER_URI 에러 해결)
@WebMvcTest(controllers = UserController.class, properties = "JWT_ISSUER_URI=http://test-issuer")
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 2. @MockBean -> @MockitoBean 으로 변경 (Spring Boot 3.4+)
    @MockitoBean
    private TokenGenerateService tokenGenerateService;

    @MockitoBean
    private UserRegisterService userRegisterService;

    @MockitoBean
    private UserUpdateService userUpdateService;

    @Test
    @DisplayName("회원가입 요청 성공 시 201 Created 반환")
    void signUp_success() throws Exception {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "newuser",
            "Password123!",
            "Password123!",
            "test@test.com",
            "GilDong",
            "Hong",
            "01012345678"
        );

        doNothing().when(userRegisterService).register(any(UserRegister.class));

        // When & Then
        mockMvc.perform(post("/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(userRegisterService).register(any(UserRegister.class));
    }

    @Test
    @DisplayName("토큰 발급 요청 성공 시 토큰 정보를 반환한다")
    void generateToken_success() throws Exception {
        // Given
        TokenRequest request = new TokenRequest("testuser", "password");
        TokenInfo tokenInfo = new TokenInfo(
            "access-token",
            300,
            1800,
            "refresh-token",
            "Bearer"
        );

        given(tokenGenerateService.generate(request.username(), request.password()))
            .willReturn(tokenInfo);

        // When & Then
        mockMvc.perform(post("/token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("입력값이 유효하지 않으면 400 Bad Request 반환")
    void signUp_validation_fail() throws Exception {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "user",
            "",
            "",
            "invalid-email",
            "",
            "",
            ""
        );

        // When & Then
        mockMvc.perform(post("/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}