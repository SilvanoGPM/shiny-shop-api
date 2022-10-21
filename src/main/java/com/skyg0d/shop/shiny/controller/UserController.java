package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsAdmin;
import com.skyg0d.shop.shiny.annotations.IsStaff;
import com.skyg0d.shop.shiny.annotations.IsUser;
import com.skyg0d.shop.shiny.model.RefreshToken;
import com.skyg0d.shop.shiny.payload.request.PromoteRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceUserRequest;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.payload.response.UserTokenResponse;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.AuthService;
import com.skyg0d.shop.shiny.service.RefreshTokenService;
import com.skyg0d.shop.shiny.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    final AuthService authService;
    final RefreshTokenService refreshTokenService;

    @GetMapping
    @IsStaff
    @Operation(summary = "Returns all users with pagination", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<UserResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(userService.listAll(pageable));
    }

    @GetMapping("/{email}")
    @IsStaff
    @Operation(summary = "Find user by email", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmailMapped(email));
    }

    @GetMapping("/tokens")
    @IsStaff
    @Operation(summary = "Returns all users tokens with pagination", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<RefreshToken>> listAllTokens(Pageable pageable) {
        return ResponseEntity.ok(refreshTokenService.listAll(pageable));
    }

    @GetMapping("/my/tokens")
    @IsUser
    @Operation(summary = "Returns all user tokens with pagination", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<UserTokenResponse>> listMyAllTokens(Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(refreshTokenService.listAllByUser(pageable, userDetails.getEmail()));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or #request.email == authentication.principal.email")
    @Operation(summary = "Promote user to others roles", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When user not found"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> replace(@Valid @RequestBody ReplaceUserRequest request) {
        userService.replace(request);

        return ResponseEntity.ok(new MessageResponse("User replaced!"));
    }

    @PatchMapping("/promote")
    @IsAdmin
    @Operation(summary = "Promote user to others roles", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When user not found"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> promote(@Valid @RequestBody PromoteRequest request) {
        userService.promote(request.getEmail(), request.getRoles());

        return ResponseEntity.ok(new MessageResponse("User promoted!"));
    }

    @DeleteMapping("/logout/{email}")
    @IsAdmin
    @Operation(summary = "User logout", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When user not found"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> logout(@PathVariable String email) {
        authService.logout(email);

        return ResponseEntity.ok(new MessageResponse("Log out successful"));
    }

}
