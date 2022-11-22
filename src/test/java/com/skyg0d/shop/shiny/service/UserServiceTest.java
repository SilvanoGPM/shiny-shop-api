package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.model.ERole;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.skyg0d.shop.shiny.util.role.RoleCreator.createRole;
import static com.skyg0d.shop.shiny.util.user.UserCreator.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for UserService")
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleService roleService;

    @BeforeEach
    void setUp() {
        PageImpl<User> usersPage = new PageImpl<>(List.of(
                createUser()
        ));

        BDDMockito
                .when(userRepository.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(usersPage);

        BDDMockito
                .when(userRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(createUser()));

        BDDMockito
                .when(roleService.findByName(ArgumentMatchers.any(ERole.class)))
                .thenReturn(createRole());

        BDDMockito
                .when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(usersPage);
    }

    @Test
    @DisplayName("listAll Returns List Of Users Inside Page Object When Successful")
    void listAll_ReturnsListOfUsersInsidePageObject_WhenSuccessful() {
        User expectedUser = createUser();

        Page<UserResponse> usersPage = userService.listAll(PageRequest.of(0, 1));

        assertThat(usersPage).isNotEmpty();

        assertThat(usersPage.getContent()).isNotEmpty();

        assertThat(usersPage.getContent().get(0)).isNotNull();

        assertThat(usersPage.getContent().get(0).getEmail()).isEqualTo(expectedUser.getEmail());
    }

    @Test
    @DisplayName("findByEmail Returns User When Successful")
    void findByEmail_ReturnsUser_WhenSuccessful() {
        User expectedUser = createUser();

        User foundUser = userService.findByEmail(expectedUser.getEmail());

        assertThat(foundUser).isNotNull();

        assertThat(foundUser.getEmail()).isEqualTo(expectedUser.getEmail());
    }

    @Test
    @DisplayName("findByEmail Throws ResourceNotFoundException When User Not Found")
    void findByEmail_ThrowsResourceNotFoundException_WhenUserNotFound() {
        BDDMockito
                .when(userRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.findByEmail("some-email"));
    }

    @Test
    @DisplayName("findByEmailMapped Returns User When Successful")
    void findByEmailMapped_ReturnsUser_WhenSuccessful() {
        User expectedUser = createUser();

        UserResponse foundUser = userService.findByEmailMapped(expectedUser.getEmail());

        assertThat(foundUser).isNotNull();

        assertThat(foundUser.getEmail()).isEqualTo(expectedUser.getEmail());
    }

    @Test
    @DisplayName("findByEmailMapped Throws ResourceNotFoundException When User Not Found")
    void findByEmailMapped_ThrowsResourceNotFoundException_WhenUserNotFound() {
        BDDMockito
                .when(userRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.findByEmailMapped("some-email"));
    }

    @Test
    @DisplayName("search Returns List Of Users Inside Page Object When Successful")
    void search_ReturnsListOfUsersInsidePageObject_WhenSuccessful() {
        User expectedUser = createUser();

        Page<UserResponse> usersPage = userService.search(createUserParameterSearch(), PageRequest.of(0, 1));

        assertThat(usersPage).isNotEmpty();

        assertThat(usersPage.getContent()).isNotEmpty();

        assertThat(usersPage.getContent().get(0)).isNotNull();

        assertThat(usersPage.getContent().get(0).getEmail()).isEqualTo(expectedUser.getEmail());
    }

    @Test
    @DisplayName("replace Updates User When Successful")
    void replace_UpdatesUser_WhenSuccessful() {
        assertThatCode(() -> userService.replace(createReplaceUserRequest()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("promote Updates User Roles When Successful")
    void promote_UpdatesUserRoles_WhenSuccessful() {
        User expectedUser = createUser();

        assertThatCode(() -> userService.promote(expectedUser.getEmail(), Set.of("admin")))
                .doesNotThrowAnyException();

        assertThatCode(() -> userService.promote(expectedUser.getEmail(), Set.of()))
                .doesNotThrowAnyException();
    }

}
