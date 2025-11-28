package br.dev.kajosama.dropship.security.jwt;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.security.entities.Role;
import br.dev.kajosama.dropship.security.entities.UserRole;
import br.dev.kajosama.dropship.security.services.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A filter that intercepts incoming HTTP requests to validate JWT tokens. This
 * filter is executed once per request and is responsible for parsing the
 * 'Authorization' header, validating the JWT, and setting the authentication
 * context for Spring Security if the token is valid.
 *
 * @author Sam_Umbra
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    /**
     * Utility class for handling JWT operations like validation and parsing
     * claims.
     */
    @Autowired
    private JwtTokenUtil jwtUtil;

    /**
     * Service for managing token versions and validity.
     */
    @Autowired
    private TokenService tokenService;

    /**
     * The main filter method that processes the request. It checks for a bearer
     * token, validates it, and sets the security context.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);

        try {
            if (jwtUtil.validateToken(token) && isTokenVersionValid(token)) {
                setAuthenticationContext(token, request);
            } else {
                if (!isTokenVersionValid(token)) {
                    handleInvalidToken(response, "Token has been invalidated.", HttpStatus.UNAUTHORIZED);
                    return;
                }
            }
        } catch (ExpiredJwtException ex) {
            handleInvalidToken(response, "Expired JWT token, please request a new token.", HttpStatus.UNAUTHORIZED);
            return;
        } catch (SignatureException ex) {
            handleInvalidToken(response, "Token assignature invalid.", HttpStatus.UNAUTHORIZED);
            return;
        } catch (InvalidCsrfTokenException ex) {
            handleInvalidToken(response, "Invalid Token.", HttpStatus.UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Handles invalid token scenarios by sending a JSON error response.
     *
     * @param response The HttpServletResponse to write the error to.
     * @param message The error message to include in the response.
     * @param status The HTTP status to set for the response.
     * @throws IOException If an input or output exception occurs.
     */
    private void handleInvalidToken(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\": \"%s\", \"timestamp\": \"%s\"}",
                message,
                Instant.now().toString()
        );
        response.getWriter().write(jsonResponse);
    }

    /**
     * Checks if the token version from the JWT is still valid in the system.
     *
     * @param token The JWT string.
     * @return {@code true} if the token version is valid, {@code false}
     * otherwise.
     */
    private boolean isTokenVersionValid(String token) {
        Long userId = jwtUtil.getUserId(token);
        return tokenService.isTokenVersionValid(userId, jwtUtil.getTokenVersion(token));
    }

    /**
     * Checks if the request has an 'Authorization' header with the 'Bearer '
     * prefix.
     *
     * @param request The incoming HttpServletRequest.
     * @return {@code true} if the header is present and correctly formatted,
     * {@code false} otherwise.
     */
    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ");
    }

    /**
     * Extracts the JWT string from the 'Authorization' header.
     *
     * @param request The incoming HttpServletRequest.
     * @return The JWT string without the 'Bearer ' prefix.
     */
    private String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization").substring(7);
    }

    /**
     * Sets the authentication context in Spring Security.
     *
     * @param token The valid JWT string.
     * @param request The current HttpServletRequest.
     */
    private void setAuthenticationContext(String token, HttpServletRequest request) {
        try {
            UserDetails userDetails = getUserDetails(token);
            List<SimpleGrantedAuthority> authorities = getAuthoritiesFromToken(token);

            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            LOGGER.error("Error setting authentication context: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Extracts the roles from the JWT and converts them to a list of
     * {@link SimpleGrantedAuthority}.
     *
     * @param token The JWT string.
     * @return A list of authorities for the user.
     */
    private List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        return jwtUtil.getRoles(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Creates a {@link UserDetails} object from the claims in the JWT.
     *
     * @param token The JWT string.
     * @return A {@link User} object populated with details from the token.
     */
    private UserDetails getUserDetails(String token) {
        User userDetails = new User();
        userDetails.setEmail(jwtUtil.getEmail(token));
        userDetails.setId(jwtUtil.getUserId(token));
        userDetails.setName(jwtUtil.getUserName(token));

        // popula roles
        Set<UserRole> roles = jwtUtil.getRoles(token).stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return new UserRole(userDetails, role, LocalDateTime.now());
                })
                .collect(Collectors.toSet());

        userDetails.setUserRoles(roles);

        return userDetails;
    }

}
