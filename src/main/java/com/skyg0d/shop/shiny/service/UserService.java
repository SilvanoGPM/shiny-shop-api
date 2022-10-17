package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.mapper.UserMapper;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.ReplaceUserRequest;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.repository.UserRepository;
import com.skyg0d.shop.shiny.util.RoleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;

    final RoleService roleService;

    final UserMapper mapper = UserMapper.INSTANCE;

    public Page<UserResponse> listAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::toUserResponse);
    }

    public User findByEmail(String email) throws ResourceNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public UserResponse findByEmailMapped(String email) throws ResourceNotFoundException {
        return mapper.toUserResponse(findByEmail(email));
    }

    public void replace(ReplaceUserRequest request) {
        User userFound = findByEmail(request.getEmail());

        User userMapped = mapper.toUser(request);

        userMapped.setId(userFound.getId());
        userMapped.setRoles(userFound.getRoles());
        userMapped.setPassword(userFound.getPassword());

        userRepository.save(userMapped);
    }

    public void promote(String email, Set<String> roles) {
        User user = findByEmail(email);

        user.setRoles(getUserRoles(roles));

        userRepository.save(user);
    }

    private Set<Role> getUserRoles(Set<String> roles) {
        Optional<Set<String>> optionalRoles = Optional.ofNullable(roles);

        Set<String> defaultRoles = Set.of("user");

        return optionalRoles
                .map((innerRoles) -> {
                    if (innerRoles.isEmpty()) {
                        return defaultRoles;
                    }

                    return innerRoles;
                })
                .orElse(defaultRoles)
                .stream()
                .map((role) -> roleService.findByName(RoleUtils.getRoleByString(role)))
                .collect(Collectors.toSet());
    }

}
