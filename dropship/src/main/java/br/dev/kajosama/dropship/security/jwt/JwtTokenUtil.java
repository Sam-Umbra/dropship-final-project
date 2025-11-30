package br.dev.kajosama.dropship.security.jwt;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.security.configurations.JwtProperties;
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
import br.dev.kajosama.dropship.domain.model.entities.User;

/**
 * Utility class for handling JSON Web Tokens (JWT). Provides methods for
 * generating, validating, and parsing different types of tokens, including
 * access, refresh, and validation tokens.
 *
 * @author Sam_Umbra
 */
@Component
public class JwtTokenUtil {

    /**
     * Expiration time for access tokens: 15 minutes.
     */
    private static final long EXPIRE_DURATION = 15 * 60 * 1000;

    /**
     * Expiration time for refresh tokens: 3 hours.
     */
    private static final long REFRESH_EXPIRE_DURATION = 3 * 60 * 60 * 1000;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    /**
     * Configuration properties for JWT, such as the secret key.
     */
    private final JwtProperties jwtProperties;
    /**
     * Jackson object mapper for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructs the JwtTokenUtil with necessary dependencies.
     *
     * @param jwtProperties The JWT configuration properties.
     * @param objectMapper The Jackson object mapper.
     */
    public JwtTokenUtil(JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a new JWT access token for a user.
     *
     * @param user The user for whom the token is generated.
     * @param tokenVersion The current token version for the user.
     * @return A JWT access token string.
     */
    public String generateAccessToken(User user, Long tokenVersion) {
        return generateToken(user, tokenVersion, EXPIRE_DURATION, "ACCESS");
    }

    /**
     * Generates a new JWT refresh token for a user.
     *
     * @param user The user for whom the token is generated.
     * @param tokenVersion The current token version for the user.
     * @return A JWT refresh token string.
     */
    public String generateRefreshToken(User user, Long tokenVersion) {
        return generateToken(user, tokenVersion, REFRESH_EXPIRE_DURATION, "REFRESH");
    }

    /**
     * Generates a generic validation token for a specific entity and purpose.
     * This is used for actions like email confirmation where a user context
     * isn't needed.
     *
     * @param entityName The name of the entity (e.g., "User", "Supplier").
     * @param entityId The ID of the entity.
     * @param expiration The expiration duration in milliseconds.
     * @param tokenType A custom token type identifier.
     * @return A JWT validation token string.
     */
    public String generateValidationToken(String entityName, Long entityId, long expiration, String tokenType) {
        try {

            SecretKey secretKey = getSecretKey();
            Claims claims = Jwts.claims();
            claims.put("entityId", entityId);
            claims.put("entityName", entityName);
            claims.put("tokenType", tokenType);

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuer("DropShip-API")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

        } catch (InvalidKeyException e) {
            LOGGER.error("Error creating {} validation token for entity type {}: {}", tokenType, entityName, e.getMessage());
            throw new RuntimeException("Error creating token", e);
        }
    }

    /**
     * Validates a generic validation token. It checks the signature,
     * expiration, and ensures the token type is 'VALIDATION'.
     *
     * @param token The validation token string to validate.
     * @return {@code true} if the token is valid.
     */
    public boolean validateValidationToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String tokenType = claims.get("tokenType", String.class);

            if (!"VALIDATION".equals(tokenType)) {
                throw new JwtException("Token type is invalid. Expected 'VALIDATION', but got '" + tokenType + "'.");
            }

            return true;

        } catch (ExpiredJwtException ex) {
            LOGGER.error("Validation JWT has expired: {}", ex.getMessage());
            throw ex;
        } catch (JwtException ex) {
            LOGGER.error("Invalid validation JWT: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Private helper method to generate a token with specified parameters.
     *
     * @param user The user entity.
     * @param tokenVersion The token version.
     * @param expiration The expiration duration in milliseconds.
     * @param tokenType The type of token ("ACCESS" or "REFRESH").
     * @return The generated JWT string.
     * @throws AccessDeniedException if the user account is not active.
     */
    private String generateToken(User user, Long tokenVersion, long expiration, String tokenType) {
        if (!user.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new AccessDeniedException("User: " + user.getName() + "is not authorized in the system");
        }

        try {
            SecretKey secretKey = getSecretKey();

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

    /**
     * Validates the signature and expiration of a JWT. It does not check the
     * token version or other claims.
     *
     * @param token The JWT string to validate.
     * @return {@code true} if the token signature and expiration are valid,
     * {@code false} otherwise.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true; // Se não lançar exceção, é válido

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

    /**
     * Extracts the email (subject) from the JWT claims.
     *
     * @param token The JWT string.
     * @return The user's email.
     */
    public String getEmail(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (Exception e) {
            LOGGER.error("Error upon extracting email from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Extracts the user ID from the JWT claims.
     *
     * @param token The JWT string.
     * @return The user's ID.
     */
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

    /**
     * Extracts the entity ID from a generic validation token's claims.
     *
     * @param token The JWT string.
     * @return The entity's ID.
     */
    public Long getEntityId(String token) {
        try {
            Object entityIdClaim = parseClaims(token).get("entityId");
            if (entityIdClaim instanceof Integer integer) {
                return integer.longValue();
            }
            return parseClaims(token).get("entityId", Long.class);
        } catch (Exception e) {
            LOGGER.error("Error upon extracting entityId form token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Extracts the user's name from the JWT claims.
     *
     * @param token The JWT string.
     * @return The user's name, or {@code null} if not present.
     */
    public String getUserName(String token) {
        try {
            return parseClaims(token).get("name", String.class);
        } catch (Exception e) {
            LOGGER.error("Error upon extracting name from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the user's roles from the JWT claims.
     *
     * @param token The JWT string.
     * @return A list of role names.
     */
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

    /**
     * Checks if the token is expired.
     *
     * @param token The JWT string.
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Calculates the remaining time in milliseconds before the token expires.
     *
     * @param token The JWT string.
     * @return The remaining time in milliseconds, or 0 if expired or invalid.
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Parses the JWT string and returns its claims.
     *
     * @param token The JWT string.
     * @return The {@link Claims} object.
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decodes the Base64 secret key from properties and returns it as a
     * {@link SecretKey}.
     *
     * @return The HMAC SHA secret key.
     */
    private SecretKey getSecretKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException e) {
            LOGGER.error("Error upon creating secret key: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT configuration", e);
        }
    }

    /**
     * Extracts the token version from the JWT claims.
     *
     * @param token The JWT string.
     * @return The token version number.
     */
    public Long getTokenVersion(String token) {
        Object versionClaim = parseClaims(token).get("tokenVersion");
        if (versionClaim instanceof Integer integer) {
            return integer.longValue();
        }
        return parseClaims(token).get("tokenVersion", Long.class);
    }

    /**
     * Extracts the token version from a pre-parsed {@link Claims} object.
     *
     * @param claims The pre-parsed claims.
     * @return The token version number.
     */
    public Long getTokenVersionFromClaims(Claims claims) {
        Object versionClaim = claims.get("tokenVersion");
        if (versionClaim instanceof Integer integer) {
            return integer.longValue();
        }
        return claims.get("tokenVersion", Long.class);
    }
}
