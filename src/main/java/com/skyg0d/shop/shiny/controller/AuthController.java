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
import com.skyg0d.shop.shiny.util.HttpUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthUtils authUtils;

    @PostMapping("/signin")
    @Operation(summary = "User sign in", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When user not found or incorrect password"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<JwtResponse> signIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String ipAddress = HttpUtils.getClientIp();

        UserMachineDetails userMachineDetails = UserMachineDetails
                .builder()
                .ipAddress(ipAddress)
                .browser(userAgent.getBrowser().getName())
                .operatingSystem(userAgent.getOperatingSystem().getName())
                .build();

        return ResponseEntity.ok(authService.signIn(loginRequest, userMachineDetails));
    }

    @PostMapping("/signup")
    @Operation(summary = "User sign up", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User Created"),
            @ApiResponse(responseCode = "400", description = "When email already exists"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignupRequest signUpRequest) {
        return new ResponseEntity<>(authService.signUp(signUpRequest), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @Operation(summary = "User refresh token", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "403", description = "When refresh token don't exists"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @DeleteMapping("/logout")
    @Operation(summary = "User logout", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When user not logged in"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> logout() {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        authService.logout(userDetails.getEmail());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
