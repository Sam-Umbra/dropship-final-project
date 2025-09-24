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
 *
 * @author Sam_Umbra
 */
@RequestMapping("/auth")
@RestController
public class AuthApi {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {

            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest request) {
        try {

            AuthResponse response = authService.refreshTokens(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            authService.logout(token);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, 
                                          Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            authService.changePassword(user.getId(), request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully. Please log in again."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
