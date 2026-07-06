package com.rshinna.taskboardapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rshinna.taskboardapi.auth.security.CustomUserDetailsService;
import com.rshinna.taskboardapi.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rshinna.taskboardapi.auth.dto.LoginRequestDTO;
import com.rshinna.taskboardapi.auth.dto.LoginResponseDTO;
import com.rshinna.taskboardapi.auth.service.AuthService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // Required by SecurityConfig during WebMvcTest context initialization (JwtService e CustomUserDetailsService)
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("rodrigo@email.com", "123456");
        LoginResponseDTO response = new LoginResponseDTO("token-jwt");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("", "");

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}