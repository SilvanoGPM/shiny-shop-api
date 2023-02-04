package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.exception.details.ExceptionDetails;
import com.skyg0d.shop.shiny.model.RefreshToken;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.LoginRequest;
import com.skyg0d.shop.shiny.payload.request.SignupRequest;
import com.skyg0d.shop.shiny.payload.request.TokenRefreshRequest;
import com.skyg0d.shop.shiny.payload.response.JwtResponse;
import com.skyg0d.shop.shiny.payload.response.TokenRefreshResponse;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.repository.RefreshTokenRepository;
import com.skyg0d.shop.shiny.repository.UserRepository;
import com.skyg0d.shop.shiny.util.JWTCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration tests for AuthController")
public class AuthControllerIT {

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    RefreshTokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTCreator jwtCreator;

    @Test
    @DisplayName("signIn Returns JwtResponse When Successful")
    @SuppressWarnings("null")
    void signIn_ReturnsJwtResponse_WhenSuccessful() {
        LoginRequest login = LoginRequest
                .builder()
                .email("admin@mail.com")
                .password("password")
                .build();

        ResponseEntity<JwtResponse> entity = httpClient
                .postForEntity("/auth/signin", new HttpEntity<>(login), JwtResponse.class);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUsername()).isEqualTo("Admin");
    }

    @Test
    @DisplayName("signUp_SaveUser_WhenSuccessful")
    @SuppressWarnings("null")
    void signUp_SaveUser_WhenSuccessful() {
        SignupRequest signup = SignupRequest
                .builder()
                .email("some@mail.com")
                .fullName("Some name")
                .password("password")
                .username("username")
                .build();

        ResponseEntity<UserResponse> entity = httpClient
                .postForEntity("/auth/signup", new HttpEntity<>(signup), UserResponse.class);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getEmail()).isEqualTo(signup.getEmail());
    }

    @Test
    @DisplayName("refreshToken Returns Token Refresh When Successful")
    @SuppressWarnings("null")
    void refreshToken_ReturnsTokenRefresh_WhenSuccessful() {
        jwtCreator.createAdminAuthEntity(null);

        String token = getAdminToken();

        TokenRefreshRequest request = TokenRefreshRequest
                .builder()
                .refreshToken(token)
                .build();

        ResponseEntity<TokenRefreshResponse> entity = httpClient.postForEntity("/auth/refresh", new HttpEntity<>(request), TokenRefreshResponse.class);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getRefreshToken()).isEqualTo(token);
    }

    @Test
    @DisplayName("logout Removes Refresh Token When Successful")
    void logout_RemovesRefreshToken_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/auth/logout",
                HttpMethod.DELETE,
                jwtCreator.createAdminAuthEntity(null),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("logout Returns 400 BadRequest When User Not Logged")
    @SuppressWarnings("null")
    void logout_Returns400BadRequest_WhenUserNotLogged() {
        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/auth/logout",
                HttpMethod.DELETE,
                null,
                ExceptionDetails.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getDetails()).isEqualTo("You are not logged in.");
    }

    private String getAdminToken() throws RuntimeException {
        User user = userRepository
                .findByEmail("admin@mail.com")
                .orElseThrow(RuntimeException::new);

        Page<RefreshToken> allByUser = tokenRepository.findAllByUser(PageRequest.of(0, 1), user);
        return allByUser.getContent().get(0).getToken();
    }

}
