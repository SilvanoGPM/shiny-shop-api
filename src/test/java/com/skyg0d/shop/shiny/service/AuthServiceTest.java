package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.TokenRefreshException;
import com.skyg0d.shop.shiny.exception.UserAlreadyExistsException;
import com.skyg0d.shop.shiny.model.ERole;
import com.skyg0d.shop.shiny.model.RefreshToken;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.UserMachineDetails;
import com.skyg0d.shop.shiny.payload.response.JwtResponse;
import com.skyg0d.shop.shiny.payload.response.TokenRefreshResponse;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.repository.UserRepository;
import com.skyg0d.shop.shiny.security.jwt.JwtUtils;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.util.auth.AuthCreator;
import com.skyg0d.shop.shiny.util.token.RefreshTokenCreator;
import com.skyg0d.shop.shiny.util.user.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.skyg0d.shop.shiny.util.GenericCreator.createUserMachineDetails;
import static com.skyg0d.shop.shiny.util.auth.AuthCreator.*;
import static com.skyg0d.shop.shiny.util.auth.UserDetailsImplCreator.createUserDetails;
import static com.skyg0d.shop.shiny.util.role.RoleCreator.createRole;
import static com.skyg0d.shop.shiny.util.user.UserCreator.createUser;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for AuthService")
public class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleService roleService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        Authentication authenticationMock = Mockito.mock(Authentication.class);

        BDDMockito
                .when(authenticationMock.getPrincipal())
                .thenReturn(createUserDetails());

        BDDMockito
                .when(authenticationManager.authenticate(ArgumentMatchers.any(Authentication.class)))
                .thenReturn(authenticationMock);

        BDDMockito
                .when(jwtUtils.generateJwtToken(ArgumentMatchers.any(UserDetailsImpl.class)))
                .thenReturn(AuthCreator.TOKEN);

        BDDMockito
                .when(refreshTokenService.create(ArgumentMatchers.anyString(), ArgumentMatchers.any(UserMachineDetails.class)))
                .thenReturn(RefreshTokenCreator.createRefreshToken());

        BDDMockito
                .when(userRepository.existsByEmail(ArgumentMatchers.anyString()))
                .thenReturn(false);

        BDDMockito
                .when(roleService.findByName(ArgumentMatchers.any(ERole.class)))
                .thenReturn(createRole());

        BDDMockito
                .when(passwordEncoder.encode(ArgumentMatchers.any(CharSequence.class)))
                .thenReturn(PASSWORD);

        BDDMockito
                .when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(createUser());

        BDDMockito
                .when(refreshTokenService.findByToken(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(RefreshTokenCreator.createRefreshToken()));

        BDDMockito
                .when(refreshTokenService.verifyExpiration(ArgumentMatchers.any(RefreshToken.class)))
                .thenReturn(RefreshTokenCreator.createRefreshToken());

        BDDMockito
                .when(jwtUtils.generateTokenFromEmail(ArgumentMatchers.anyString()))
                .thenReturn(TOKEN);

        BDDMockito
                .doNothing()
                .when(refreshTokenService)
                .deleteByUserId(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("signIn Authenticate And Returns Jwt Response When Successful")
    void signIn_AuthenticateAndReturnsJwtResponse_WhenSuccessful() {
        JwtResponse expectedResponse = createJwtResponse();

        JwtResponse jwtResponse = authService.signIn(createLoginRequest(), createUserMachineDetails());

        assertThat(jwtResponse.getToken()).isEqualTo(expectedResponse.getToken());
    }

    @Test
    @DisplayName("signUp Persists User When Successful")
    void signUp_PersistsUser_WhenSuccessful() {
        UserResponse userSaved = authService.signUp(createSignupRequest());

        assertThat(userSaved).isNotNull();

        assertThat(userSaved.getEmail()).isEqualTo(UserCreator.EMAIL);
    }

    @Test
    @DisplayName("signUp Throws UserAlreadyExistsException When User Already Exists")
    void signUp_ThrowsUserAlreadyExistsException_WhenUserAlreadyExists() {
        BDDMockito
                .when(userRepository.existsByEmail(ArgumentMatchers.anyString()))
                .thenReturn(true);

        assertThatExceptionOfType(UserAlreadyExistsException.class)
                .isThrownBy(() -> authService.signUp(createSignupRequest()));
    }

    @Test
    @DisplayName("refreshToken Returns TokenRefreshResponse When Successful")
    void refreshToken_ReturnsTokenRefreshResponse_WhenSuccessful() {
        TokenRefreshResponse expectedResponse = createTokenRefreshResponse();

        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(createTokenRefreshRequest());

        assertThat(tokenRefreshResponse).isNotNull();

        assertThat(tokenRefreshResponse.getRefreshToken()).isEqualTo(expectedResponse.getRefreshToken());

        assertThat(tokenRefreshResponse.getAccessToken()).isEqualTo(expectedResponse.getAccessToken());
    }

    @Test
    @DisplayName("refreshToken Throws TokenRefreshException When Refresh Token Not Found")
    void refreshToken_ThrowsTokenRefreshException_WhenRefreshTokenNotFound() {
        BDDMockito
                .when(refreshTokenService.findByToken(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(TokenRefreshException.class)
                .isThrownBy(() -> authService.refreshToken(createTokenRefreshRequest()));
    }

    @Test
    @DisplayName("logout Removes Refresh Token When Successful")
    void logout_RemovesRefreshToken_WhenSuccessful() {
        assertThatCode(() -> authService.logout("some-email"))
                .doesNotThrowAnyException();
    }

}
