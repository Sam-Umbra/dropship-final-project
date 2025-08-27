package br.dev.kajosama.dropship.security.jwt;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.dev.kajosama.dropship.domain.model.User;
import br.dev.kajosama.dropship.security.configurations.JwtProperties;
import br.dev.kajosama.dropship.security.payloads.TokenPair;
import br.dev.kajosama.dropship.security.services.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;

@Component
public class JwtTokenUtil {

    // Hora Minuto Segundo
    private static final long EXPIRE_DURATION = 15 * 60 * 1000; // 15 minutos

    private static final long REFRESH_EXPIRE_DURATION = 12 * 60 * 60 * 1000; // 12 Horas

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    public JwtTokenUtil(JwtProperties jwtProperties, ObjectMapper objectMapper,
    TokenService tokenService) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, EXPIRE_DURATION, "ACCESS");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_EXPIRE_DURATION, "REFRESH");
    }

    private String generateToken(User user, long expiration, String tokenType) {
        try {
            
            SecretKey secretKey = getSecretKey();
            Long tokenVersion = tokenService.getUserTokenVersion(user.getId());

            Claims claims = Jwts.claims().setSubject(user.getEmail());
            claims.put("userId", user.getId());
            claims.put("tokenVersion", tokenVersion);
            claims.put("tokenType", tokenType);
            
            if ("ACCESS".equals(tokenType)) {
                claims.put("name", user.getName());
                claims.put("roles", user.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .collect(Collectors.toList()));
                claims.put("status", user.getStatus().toString());
            }

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuer("DropShip-API")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

        } catch (InvalidKeyException e) {
            LOGGER.error("Error creating {} token for user {}: {}", tokenType, user.getEmail(), e.getMessage());
            throw new RuntimeException("Error creating token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            @SuppressWarnings("unused")
            Claims claims = parseClaims(token);
            Long userId = getUserId(token);
            Long tokenVersion = getTokenVersion(token);
            
            if (!tokenService.isTokenVersionValid(userId, tokenVersion)) {
                LOGGER.debug("Token version is invalid for user: {}", userId);
                return false;
            }

            return true;

        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null or empty: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT: {}", ex.getMessage());
        } catch (JwtException ex) {
            LOGGER.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    public String getEmail(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (Exception e) {
            LOGGER.error("Error upon extracting email from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    public Long getUserId(String token) {
        try {
            Object userIdClaim = parseClaims(token).get("userId");
            if (userIdClaim instanceof Integer integer) {
                return integer.longValue();
            }
            return parseClaims(token).get("userId", Long.class);
        } catch (Exception e) {
            LOGGER.error("Error upon extracting userId form token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String getUserName(String token) {
        try {
            return parseClaims(token).get("name", String.class);
        } catch (Exception e) {
            LOGGER.error("Error upon extracting name from token: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        try {
            Object rolesObj = parseClaims(token).get("roles");

            if (rolesObj instanceof List) {
                return (List<String>) rolesObj;
            }

            return objectMapper.convertValue(rolesObj, new TypeReference<List<String>>() {
            });

        } catch (IllegalArgumentException e) {
            LOGGER.error("Error upon extracting roles from token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSecretKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException e) {
            LOGGER.error("Error upon creating secret key: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT configuration", e);
        }
    }

    //------ TOKEN SERVICE --------------//

    public TokenPair refreshTokens(User user) {
        tokenService.invalidateAllUserTokens(user.getId());
        
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        
        return new TokenPair(accessToken, refreshToken);
    }

    public void logout(Long userId) {
        tokenService.invalidateAllUserTokens(userId);
    }

    public Long getTokenVersion(String token) {
        Object versionClaim = parseClaims(token).get("tokenVersion");
        if (versionClaim instanceof Integer integer) {
            return integer.longValue();
        }
        return parseClaims(token).get("tokenVersion", Long.class);
    }
}
