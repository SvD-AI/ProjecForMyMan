package com.glovodelivery.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.glovodelivery.project.auth.CustomUserDetailsService;
import com.glovodelivery.project.auth.DelegatedAuthenticationEntryPoint;
import com.glovodelivery.project.config.SecurityConfig;
import com.glovodelivery.project.config.properties.RsaKeyProperties;
import com.glovodelivery.project.dto.request.AuthenticationRequest;
import com.glovodelivery.project.dto.response.AuthenticationResponse;
import com.glovodelivery.project.enums.ResponseStatus;
import com.glovodelivery.project.service.AuthenticationService;
import com.glovodelivery.project.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@EnableConfigurationProperties(value = {RsaKeyProperties.class})
@WebMvcTest(value = AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationController authenticationController;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private DelegatedAuthenticationEntryPoint delegatedAuthenticationEntryPoint;

    private final TestUtil testUtil = new TestUtil();

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    void testUserLogin() throws Exception {
        AuthenticationResponse response = testUtil.getAuthenticationResponse();
        AuthenticationRequest request = testUtil.getAuthenticationRequest();

        when(authenticationService.login(request)).thenReturn(response);

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(response.accessToken()))
                .andExpect(jsonPath("$.tokenType").value(response.tokenType()))
                .andExpect(jsonPath("$.expiresIn").value(response.expiresIn()));
    }

    @Test
    void testUserLogin_whenPasswordIsNotProvided() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
                "john.doe@library.com",
                null
        );;

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ResponseStatus.FAILED.getValue()))
                .andExpect(jsonPath("$.message").value("Password is required"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
