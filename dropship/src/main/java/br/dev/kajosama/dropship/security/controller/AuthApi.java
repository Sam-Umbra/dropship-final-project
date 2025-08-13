/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.domain.model.User;
import br.dev.kajosama.dropship.domain.repositories.UserRepository;
import br.dev.kajosama.dropship.security.jwt.JwtTokenUtil;
import br.dev.kajosama.dropship.security.payloads.AuthRequest;
import br.dev.kajosama.dropship.security.payloads.AuthResponse;
import br.dev.kajosama.dropship.security.payloads.RefreshRequest;
import jakarta.validation.Valid;

/**
 *
 * @author Sam_Umbra
 */
@RequestMapping("/auth")
@RestController
public class AuthApi {

    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthApi(AuthenticationManager authManager,
                   JwtTokenUtil jwtUtil,
                   UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password())
            );

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            AuthResponse response = new AuthResponse(user.getEmail(), accessToken, refreshToken);

            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas"));

        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Conta desabilitada"));

        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Conta bloqueada"));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro interno do servidor"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {

            if (!jwtUtil.validateToken(request.refreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token inválido ou expirado"));
            }

            String email = jwtUtil.getEmail(request.refreshToken());
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            String newAccessToken = jwtUtil.generateAccessToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            AuthResponse response = new AuthResponse(user.getEmail(), newAccessToken, newRefreshToken);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao gerar novos tokens"));
        }
    }
}
