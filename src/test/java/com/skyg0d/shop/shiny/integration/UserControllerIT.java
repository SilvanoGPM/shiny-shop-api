package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.mapper.UserMapper;
import com.skyg0d.shop.shiny.model.RefreshToken;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.PromoteRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceUserRequest;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.payload.response.UserTokenResponse;
import com.skyg0d.shop.shiny.repository.RefreshTokenRepository;
import com.skyg0d.shop.shiny.repository.UserRepository;
import com.skyg0d.shop.shiny.util.JWTCreator;
import com.skyg0d.shop.shiny.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

import static com.skyg0d.shop.shiny.util.token.RefreshTokenCreator.createRefreshToken;
import static com.skyg0d.shop.shiny.util.user.UserCreator.createReplaceUserRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration tests for UserController")
public class UserControllerIT {

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTCreator jwtCreator;

    @Test
    @DisplayName("listAll Returns List Of Users Inside Page Object When Successful")
    @SuppressWarnings("null")
    void listAll_ReturnsListOfUsersInsidePageObject_WhenSuccessful() {
        UserResponse expectedUser = UserMapper.INSTANCE.toUserResponse(findUserByEmail(jwtCreator.createUser().getEmail()));

        ResponseEntity<PageableResponse<UserResponse>> entity = httpClient.exchange(
                "/users",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotNull();

        assertThat(entity.getBody().getContent()).contains(expectedUser);
    }

    @Test
    @DisplayName("listAllTokens Returns List Of Refresh Tokens Inside Page Object When Successful")
    void listAllTokens_ReturnsListOfRefreshTokensInsidePageObject_WhenSuccessful() {
        RefreshToken refreshToken = createRefreshToken();

        refreshToken.setUser(findUserByEmail("user@mail.com"));

        RefreshToken expectedRefreshToken = refreshTokenRepository.save(refreshToken);

        ResponseEntity<PageableResponse<RefreshToken>> entity = httpClient.exchange(
                "/users/tokens",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .contains(expectedRefreshToken);
    }

    @Test
    @DisplayName("listMyAllTokens Returns List Of Refresh Tokens Inside Page Object When Successful")
    @SuppressWarnings("null")
    void listMyAllTokens_ReturnsListOfRefreshTokensInsidePageObject_WhenSuccessful() {
        RefreshToken refreshToken = createRefreshToken();

        refreshToken.setUser(findUserByEmail("admin@mail.com"));

        RefreshToken expectedRefreshToken = refreshTokenRepository.save(refreshToken);

        ResponseEntity<PageableResponse<UserTokenResponse>> entity = httpClient.exchange(
                "/users/my/tokens",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody())
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(1);

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0).getId()).isEqualTo(expectedRefreshToken.getId().toString());

        assertThat(entity.getBody().getContent().get(0).getToken()).isEqualTo(expectedRefreshToken.getToken());
    }

    @Test
    @DisplayName("findByEmail Returns User When Successful")
    @SuppressWarnings("null")
    void findByEmail_ReturnsUser_WhenSuccessful() {
        User expectedUser = jwtCreator.createUser();

        ResponseEntity<UserResponse> entity = httpClient.exchange(
                "/users/{email}",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                UserResponse.class,
                expectedUser.getEmail()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getEmail()).isEqualTo(expectedUser.getEmail());
    }

    @Test
    @DisplayName("search Returns List Of Users Inside Page Object When Successful")
    @SuppressWarnings("null")
    void search_ReturnsListOfUsersInsidePageObject_WhenSuccessful() {
        UserResponse expectedUser = UserMapper.INSTANCE.toUserResponse(findUserByEmail(jwtCreator.createUser().getEmail()));

        ResponseEntity<PageableResponse<UserResponse>> entity = httpClient.exchange(
                "/users/search",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotNull();

        assertThat(entity.getBody().getContent()).contains(expectedUser);
    }

    @Test
    @DisplayName("replace Updates User When Successful")
    void replace_UpdatesUser_WhenSuccessful() {
        ReplaceUserRequest request = createReplaceUserRequest();

        request.setEmail(jwtCreator.createUser().getEmail());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/users",
                HttpMethod.PUT,
                jwtCreator.createAdminAuthEntity(request),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("promote Updates User Roles When Successful")
    void promote_UpdatesUserRoles_WhenSuccessful() {
        PromoteRequest promoteRequest = PromoteRequest
                .builder()
                .roles(Set.of("mod"))
                .email(findUserByEmail("user@mail.com").getEmail())
                .build();

        ResponseEntity<Void> entity = httpClient.exchange(
                "/users/promote",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(promoteRequest),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("logout Removes Refresh Token When Successful")
    void logout_RemovesRefreshToken_WhenSuccessful() {
        String email = findUserByEmail("user@mail.com").getEmail();

        ResponseEntity<Void> entity = httpClient.exchange(
                "/users/logout/{email}",
                HttpMethod.DELETE,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                email
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    private User findUserByEmail(String email) throws RuntimeException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(RuntimeException::new);
    }

}
