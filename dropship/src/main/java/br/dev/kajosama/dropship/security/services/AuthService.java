package br.dev.kajosama.dropship.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.dev.kajosama.dropship.api.exceptions.AccountDeletedException;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.repositories.UserRepository;
import br.dev.kajosama.dropship.security.jwt.JwtTokenUtil;
import br.dev.kajosama.dropship.security.payloads.AuthRequest;
import br.dev.kajosama.dropship.security.payloads.AuthResponse;
import br.dev.kajosama.dropship.security.payloads.ChangePasswordRequest;
import br.dev.kajosama.dropship.security.payloads.RefreshRequest;
import br.dev.kajosama.dropship.security.payloads.TokenPair;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtUtil;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtUtil, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        if(user.isAccountDeleted()) {
            throw new AccountDeletedException("Account is deleted");
        }

        tokenService.invalidateAllUserTokens(user.getId());
        userRepository.updateLastLogin(user.getId());

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        LOGGER.info("User {} logged in successfully", request.email());
        return new AuthResponse(request.email(), accessToken, refreshToken);
    }

    public AuthResponse refreshTokens(RefreshRequest request) {
        if (!jwtUtil.validateToken(request.refreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        Long userId = jwtUtil.getUserId(request.refreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        if(user.isAccountDeleted()) {
            throw new AccountDeletedException("Account is deleted");
        }

        TokenPair tokens = jwtUtil.refreshTokens(user);

        LOGGER.info("Tokens refreshed for user {}", userId);
        return new AuthResponse(user.getEmail(), tokens.accessToken(), tokens.refreshToken());
    }

    public void logout(String accessToken) {
        Long userId = jwtUtil.getUserId(accessToken);
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " wasn't found"));

        userRepository.updateLastExit(userId);
        jwtUtil.logout(userId);
        LOGGER.info("User {} logged out successfully", userId);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        String newEncodedPassword = passwordEncoder.encode(request.newPassword());

        userRepository.updatePassword(userId, newEncodedPassword);

        jwtUtil.logout(userId);

        LOGGER.info("Password changed and all tokens invalidated for user {}", userId);
    }

}
