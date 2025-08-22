package br.dev.kajosama.dropship.security.jwt;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.dev.kajosama.dropship.domain.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtTokenUtil jwtUtil;

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
            if (jwtUtil.validateToken(token)) {
                setAuthenticationContext(token, request);
            }
        } catch (ExpiredJwtException ex) {
            handleInvalidToken(response, "Expired JWT token, please request a new token.", HttpStatus.UNAUTHORIZED);
            return;
        } catch (SignatureException ex) {
            handleInvalidToken(response, "Token assignature invalid.", HttpStatus.UNAUTHORIZED);
            return;
        } catch (Exception ex) {
            handleInvalidToken(response, "Invalid Token.", HttpStatus.UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

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

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ");
    }

    private String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization").substring(7);
    }

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

    private List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        return jwtUtil.getRoles(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private UserDetails getUserDetails(String token) {
        User userDetails = new User();
        userDetails.setEmail(jwtUtil.getEmail(token));
        userDetails.setId(jwtUtil.getUserId(token));
        userDetails.setName(jwtUtil.getUserName(token));
        return userDetails;
    }
}
