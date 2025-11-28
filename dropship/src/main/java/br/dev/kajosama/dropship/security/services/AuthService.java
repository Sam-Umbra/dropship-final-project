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
import br.dev.kajosama.dropship.security.payloads.TokenPair;
import jakarta.transaction.Transactional;

/**
 * @author Sam_Umbra
 * @Description Service class for handling user authentication and authorization
 *              operations.
 *              This includes user login, token refreshing, logout, and password
 *              changes.
 *              It interacts with {@link UserRepository},
 *              {@link PasswordEncoder}, {@link JwtTokenUtil},
 *              and {@link TokenService} to manage user sessions and security.
 */
@Service
@Transactional
public class AuthService {

    /**
     * Logger for the {@link AuthService} class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    /**
     * Repository for {@link User} entities.
     */
    private final UserRepository userRepository;
    /**
     * Encoder for user passwords.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Utility for JWT token operations.
     */
    private final JwtTokenUtil jwtUtil;
    /**
     * Service for managing token versions and invalidation.
     */
    private final TokenService tokenService;

    /**
     * Constructs an {@link AuthService} with the necessary dependencies.
     *
     * @param userRepository  The {@link UserRepository} for user data access.
     * @param passwordEncoder The {@link PasswordEncoder} for password hashing.
     * @param jwtUtil         The {@link JwtTokenUtil} for JWT operations.
     * @param tokenService    The {@link TokenService} for token version management.
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtUtil,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    /**
     * Authenticates a user based on their email and password.
     * Upon successful authentication, it generates new access and refresh tokens,
     * invalidates previous tokens for the user, and updates the user's last login
     * timestamp.
     *
     * @param request The {@link AuthRequest} containing the user's email and
     *                password.
     * @return An {@link AuthResponse} containing the generated access and refresh
     *         tokens.
     * @throws BadCredentialsException If the provided credentials are invalid.
     * @throws DisabledException       If the user's account is disabled.
     * @throws AccountDeletedException If the user's account has been deleted.
     */
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
        if (user.isAccountDeleted()) {
            throw new AccountDeletedException("Account is deleted");
        }

        tokenService.invalidateAllUserTokens(user.getId());
        userRepository.updateLastLogin(user.getId());

        Long tokenVersion = tokenService.getUserTokenVersion(user.getId());
        String accessToken = jwtUtil.generateAccessToken(user, tokenVersion);
        String refreshToken = jwtUtil.generateRefreshToken(user, tokenVersion);

        LOGGER.info("User {} logged in successfully", request.email());
        return new AuthResponse(request.email(), accessToken, refreshToken);
    }

    /**
     * Refreshes a user's access and refresh tokens using a valid refresh token.
     * It validates the provided refresh token, checks the user's account status,
     * and ensures the token version is valid. Upon successful refresh, it
     * invalidates
     * all previous tokens for the user and generates a new pair of tokens.
     *
     * @param refreshToken The refresh token provided by the client.
     * @return An {@link AuthResponse} containing the new access and refresh tokens.
     * @throws RuntimeException        If the refresh token is invalid, the user is
     *                                 not found,
     *                                 or the refresh token has been invalidated.
     * @throws DisabledException       If the user's account is disabled.
     * @throws AccountDeletedException If the user's account has been deleted.
     */
    public AuthResponse refreshTokens(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        if (user.isAccountDeleted()) {
            throw new AccountDeletedException("Account is deleted");
        }

        if (!tokenService.isTokenVersionValid(userId, jwtUtil.getTokenVersion(refreshToken))) {
            throw new RuntimeException("Refresh token has been invalidated");
        }

        tokenService.invalidateAllUserTokens(userId);
        Long newTokenVersion = tokenService.getUserTokenVersion(userId);
        TokenPair tokens = new TokenPair(jwtUtil.generateAccessToken(user, newTokenVersion),
                jwtUtil.generateRefreshToken(user, newTokenVersion));

        LOGGER.info("Tokens refreshed for user {}", userId);
        return new AuthResponse(user.getEmail(), tokens.accessToken(), tokens.refreshToken());
    }

    /**
     * Logs out a user by invalidating all their active tokens.
     * It updates the user's last exit timestamp and increments their token version,
     * effectively invalidating all previously issued tokens.
     *
     * @param accessToken The access token of the user to be logged out.
     * @throws RuntimeException If the user associated with the access token is not
     *                          found.
     */
    public void logout(String accessToken) {
        Long userId = jwtUtil.getUserId(accessToken);
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " wasn't found"));

        userRepository.updateLastExit(userId);
        tokenService.invalidateAllUserTokens(userId);
        LOGGER.info("User {} logged out successfully", userId);
    }

    /**
     * Changes a user's password.
     * It verifies the current password, encodes the new password, updates it in the
     * database,
     * and invalidates all active tokens for the user, forcing a re-login.
     *
     * @param userId  The ID of the user whose password is to be changed.
     * @param request The {@link ChangePasswordRequest} containing the current and
     *                new passwords.
     * @throws RuntimeException        If the user is not found.
     * @throws BadCredentialsException If the current password provided is
     *                                 incorrect.
     */
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        String newEncodedPassword = passwordEncoder.encode(request.newPassword());

        userRepository.updatePassword(userId, newEncodedPassword);

        tokenService.invalidateAllUserTokens(userId);

        LOGGER.info("Password changed and all tokens invalidated for user {}", userId);
    }

}
