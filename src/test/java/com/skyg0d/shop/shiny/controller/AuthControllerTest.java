package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.payload.UserMachineDetails;
import com.skyg0d.shop.shiny.payload.request.LoginRequest;
import com.skyg0d.shop.shiny.payload.request.SignupRequest;
import com.skyg0d.shop.shiny.payload.request.TokenRefreshRequest;
import com.skyg0d.shop.shiny.payload.response.JwtResponse;
import com.skyg0d.shop.shiny.payload.response.TokenRefreshResponse;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.AuthService;
import com.skyg0d.shop.shiny.util.AuthUtils;
import com.skyg0d.shop.shiny.util.MockUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.skyg0d.shop.shiny.util.auth.AuthCreator.*;
import static com.skyg0d.shop.shiny.util.user.UserCreator.createUserResponse;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for AuthController")
public class AuthControllerTest {

    @InjectMocks
    AuthController authController;

    @Mock
    AuthService authService;

    @Mock
    AuthUtils authUtils;

    @BeforeEach
    void setUp() {
        BDDMockito
                .when(authService.signIn(ArgumentMatchers.any(LoginRequest.class), ArgumentMatchers.any(UserMachineDetails.class)))
                .thenReturn(createJwtResponse());

        BDDMockito
                .when(authService.signUp(ArgumentMatchers.any(SignupRequest.class)))
                .thenReturn(createUserResponse());

        BDDMockito
                .when(authService.refreshToken(ArgumentMatchers.any(TokenRefreshRequest.class)))
                .thenReturn(createTokenRefreshResponse());

        BDDMockito
                .doNothing()
                .when(authService)
                .logout(ArgumentMatchers.anyString());

        BDDMockito
                .when(authUtils.getUserDetails())
                .thenReturn(new UserDetailsImpl(UUID.randomUUID(), USERNAME, EMAIL, PASSWORD, null));
    }

    @Test
    @DisplayName("signIn Returns JwtResponse When Successful")
    void signIn_ReturnsJwtResponse_WhenSuccessful() {
        JwtResponse expectedResponse = createJwtResponse();

        HttpServletRequest httpServletRequest = MockUtils.mockUserMachineInfo();

        ResponseEntity<JwtResponse> entity = authController.signIn(createLoginRequest(), httpServletRequest);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getToken()).isEqualTo(expectedResponse.getToken());

        assertThat(entity.getBody().getRefreshToken()).isEqualTo(expectedResponse.getRefreshToken());
    }

    @Test
    @DisplayName("signUp_PersistsUser_WhenSuccessful")
    void signUp_PersistsUser_WhenSuccessful() {
        UserResponse expectedUser = createUserResponse();

        ResponseEntity<UserResponse> entity = authController.signUp(createSignupRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getEmail()).isEqualTo(expectedUser.getEmail());
    }

    @Test
    @DisplayName("refreshToken Returns Token Refresh When Successful")
    void refreshToken_ReturnsTokenRefresh_WhenSuccessful() {
        TokenRefreshResponse expectedResponse = createTokenRefreshResponse();

        ResponseEntity<TokenRefreshResponse> entity = authController.refreshToken(createTokenRefreshRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getAccessToken()).isEqualTo(expectedResponse.getAccessToken());

        assertThat(entity.getBody().getRefreshToken()).isEqualTo(expectedResponse.getRefreshToken());
    }

    @Test
    @DisplayName("logout Removes Refresh Token When Successful")
    void logout_RemovesRefreshToken_WhenSuccessful() {
        MockUtils.mockSecurityContextHolder();

        ResponseEntity<Void> entity = authController.logout();

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

}
