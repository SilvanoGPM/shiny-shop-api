package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.TokenRefreshException;
import com.skyg0d.shop.shiny.model.RefreshToken;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.UserMachineDetails;
import com.skyg0d.shop.shiny.payload.response.UserTokenResponse;
import com.skyg0d.shop.shiny.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${app.jwt.refreshExpirationMs: #{0L}}")
    private Long refreshTokenDurationMs = 0L;

    public Page<RefreshToken> listAll(Pageable pageable) {
        return refreshTokenRepository.findAll(pageable);
    }

    public Page<UserTokenResponse> listAllByUser(Pageable pageable, String email) {
        User user = userService.findByEmail(email);

        return refreshTokenRepository.findAllByUser(pageable, user).map((UserTokenResponse::new));
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken create(String email, UserMachineDetails userMachineDetails) {
        User user = userService.findByEmail(email);

        RefreshToken refreshToken = RefreshToken.builder().token(UUID.randomUUID().toString()).user(user).expiryDate(Instant.now().plusMillis(refreshTokenDurationMs)).ipAddress(userMachineDetails.getIpAddress()).browser(userMachineDetails.getBrowser()).operatingSystem(userMachineDetails.getOperatingSystem()).build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        boolean isTokenExpired = token.getExpiryDate().compareTo(Instant.now()) < 0;

        if (isTokenExpired) {
            refreshTokenRepository.delete(token);

            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public void deleteByUserId(String email) {
        refreshTokenRepository.deleteByUser(userService.findByEmail(email));
    }

}
