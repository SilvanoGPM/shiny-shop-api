package com.skyg0d.shop.shiny.util;

import com.skyg0d.shop.shiny.model.ERole;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.LoginRequest;
import com.skyg0d.shop.shiny.payload.response.JwtResponse;
import com.skyg0d.shop.shiny.repository.RoleRepository;
import com.skyg0d.shop.shiny.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class JWTCreator {
    @Autowired
    final TestRestTemplate testRestTemplate;

    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    final UserRepository userRepository;

    public JWTCreator(TestRestTemplate testRestTemplate, UserRepository userRepository, RoleRepository roleRepository) {
        this.testRestTemplate = testRestTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();

        persistUsers(roleRepository, userRepository);
    }

    public User createUser() {
        return User
                .builder()
                .username("User")
                .fullName("User")
                .email("user@mail.com")
                .password(passwordEncoder.encode("password"))
                .build();
    }

    public User createModerator() {
        return User
                .builder()
                .username("Moderator")
                .fullName("Moderator")
                .email("mod@mail.com")
                .password(passwordEncoder.encode("password"))
                .build();
    }

    public User createAdmin() {
        return User
                .builder()
                .username("Admin")
                .fullName("Admin")
                .email("admin@mail.com")
                .password(passwordEncoder.encode("password"))
                .build();
    }

    public void persistUsers(RoleRepository roleRepository, UserRepository userRepository) {
        Role adminRole = roleRepository.save(new Role(ERole.ROLE_ADMIN));
        Role modRole = roleRepository.save(new Role(ERole.ROLE_MODERATOR));
        Role userRole = roleRepository.save(new Role(ERole.ROLE_USER));

        User user = createUser();
        User moderator = createModerator();
        User admin = createAdmin();

        user.setRoles(Set.of(userRole));
        moderator.setRoles(Set.of(modRole));
        admin.setRoles(Set.of(adminRole));

        userRepository.saveAll(List.of(admin, user, moderator));
    }

    public <T> HttpEntity<T> createAdminAuthEntity(T t) {
        return createAuthEntity(t, new LoginRequest("admin@mail.com", "password"));
    }

    public <T> HttpEntity<T> createModeratorAuthEntity(T t) {
        return createAuthEntity(t, new LoginRequest("mod@mail.com", "password"));
    }

    public <T> HttpEntity<T> createUserAuthEntity(T t) {
        return createAuthEntity(t, new LoginRequest("user@mail.com", "password"));
    }

    public <T> HttpEntity<T> createAuthEntity(T t, LoginRequest login) {
        ResponseEntity<JwtResponse> entity = testRestTemplate
                .postForEntity("/auth/signin", new HttpEntity<>(login), JwtResponse.class);

        if (entity.getBody() != null && entity.getBody().getToken().isEmpty()) {
            throw new RuntimeException("Empty access token.");
        }

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(entity.getBody().getToken());

        return new HttpEntity<>(t, headers);
    }

}
