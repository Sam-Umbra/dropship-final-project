/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.security.payloads.AuthRequest;
import br.dev.kajosama.dropship.security.payloads.AuthResponse;
import br.dev.kajosama.dropship.security.payloads.ChangePasswordRequest;
import br.dev.kajosama.dropship.security.payloads.RefreshRequest;
import br.dev.kajosama.dropship.security.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * @author Sam_Umbra
 * @Description REST controller for authentication-related operations.
 *              This class handles user login, token refreshing, logout, and
 *              password change requests.
 *              It interacts with {@link AuthService} to perform the business
 *              logic.
 */
@RequestMapping("/auth")
@RestController
public class AuthApi {

    /**
     * Injected {@link AuthService} for handling authentication logic.
     */
    @Autowired
    AuthService authService;

    /**
     * Handles user login requests.
     * Authenticates the user with the provided credentials and returns JWT tokens
     * upon successful login.
     *
     * @param request The {@link AuthRequest} containing user credentials (email and
     *                password).
     * @return A {@link ResponseEntity} containing an {@link AuthResponse} with JWT
     *         tokens if successful,
     *         or an error map with HTTP status 401 (Unauthorized) if authentication
     *         fails.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {

            /**
             * Calls the authentication service to perform login.
             */
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handles token refresh requests.
     * Uses a refresh token to obtain a new access token and refresh token pair.
     *
     * @param request The {@link RefreshRequest} containing the refresh token.
     * @return A {@link ResponseEntity} containing an {@link AuthResponse} with new
     *         JWT tokens if successful,
     *         or an error map with HTTP status 401 (Unauthorized) if the refresh
     *         token is invalid or expired.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest request) {
        try {

            /**
             * Calls the authentication service to refresh tokens.
             */
            AuthResponse response = authService.refreshTokens(request.refreshToken());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handles user logout requests.
     * Invalidates the current JWT token, effectively logging the user out.
     *
     * @param request The {@link HttpServletRequest} from which the Authorization
     *                header (containing the token) is extracted.
     * @return A {@link ResponseEntity} with a success message if logout is
     *         successful,
     *         or an error message with HTTP status 400 (Bad Request) if no token is
     *         provided.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        /**
         * Extracts the JWT token from the Authorization header.
         */
        String token = getTokenFromRequest(request);
        if (token != null) {
            authService.logout(token);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
    }

    /**
     * Handles requests to change the authenticated user's password.
     *
     * @param request        The {@link ChangePasswordRequest} containing the old
     *                       and new passwords.
     * @param authentication The current {@link Authentication} object, from which
     *                       the authenticated user is extracted.
     * @return A {@link ResponseEntity} with a success message if the password is
     *         changed successfully,
     *         or an error message with HTTP status 400 (Bad Request) if the old
     *         password is incorrect.
     * @throws BadCredentialsException If the old password provided does not match
     *                                 the user's current password.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            /**
             * Retrieves the authenticated {@link User} from the {@link Authentication}
             * object.
             */
            User user = (User) authentication.getPrincipal();
            authService.changePassword(user.getId(), request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully. Please log in again."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Extracts the JWT token from the "Authorization" header of an
     * {@link HttpServletRequest}.
     * The token is expected to be in the format "Bearer <token>".
     *
     * @param request The {@link HttpServletRequest} to extract the token from.
     * @return The JWT token string, or {@code null} if the header is missing or not
     *         in the expected format.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
