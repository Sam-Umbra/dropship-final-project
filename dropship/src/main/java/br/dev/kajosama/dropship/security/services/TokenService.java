package br.dev.kajosama.dropship.security.services;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Sam_Umbra
 * @Description Service class for managing token versions and invalidation using
 *              Redis.
 *              This service is crucial for implementing a robust token
 *              invalidation mechanism,
 *              allowing for immediate logout or password change effects across
 *              all active tokens
 *              for a given user. It interacts with {@link RedisTemplate} to
 *              store and retrieve
 *              token version numbers.
 */
@Service
public class TokenService {

    /**
     * Logger for the {@link TokenService} class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    /**
     * Redis template for performing Redis operations.
     */
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Constructs a {@link TokenService} with the necessary {@link RedisTemplate}
     * dependency.
     *
     * @param redisTemplate The {@link RedisTemplate} for Redis data access.
     */
    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Invalidates all active tokens for a specific user by incrementing their token
     * version in Redis.
     * This effectively makes all previously issued tokens with an older version
     * invalid.
     * The new token version is set to expire after 30 days.
     *
     * @param userId The ID of the user whose tokens are to be invalidated.
     * @throws RuntimeException If there is a failure in communicating with Redis.
     */
    public void invalidateAllUserTokens(Long userId) {
        try {
            String key = "user:token:version:" + userId;
            Long newVersion = redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, Duration.ofDays(30));
            LOGGER.info("Incremented token version for user: {} to version: {}", userId, newVersion);
        } catch (Exception e) {
            LOGGER.error("Failed to invalidate tokens for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Token invalidation failed", e);
        }
    }

    /**
     * Retrieves the current token version for a specific user from Redis.
     * If no version is found, it defaults to 0L.
     *
     * @param userId The ID of the user whose token version is to be retrieved.
     * @return The current token version for the user.
     */
    public Long getUserTokenVersion(Long userId) {
        try {
            String key = "user:token:version:" + userId;
            String version = redisTemplate.opsForValue().get(key);
            Long result = version != null ? Long.valueOf(version) : 0L;
            LOGGER.debug("Retrieved token version {} for user {}", result, userId);
            return result;
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to get token version for user {}: {}", userId, e.getMessage());
            return 0L;

        }
    }

    /**
     * Checks if a given token version is valid for a specific user.
     * A token version is considered valid if it matches the current token version
     * stored in Redis for that user.
     *
     * @param userId       The ID of the user.
     * @param tokenVersion The token version to validate against the current
     *                     version.
     * @return True if the provided token version is valid, false otherwise.
     */
    public boolean isTokenVersionValid(Long userId, Long tokenVersion) {
        try {
            Long currentVersion = getUserTokenVersion(userId);
            boolean isValid = tokenVersion.equals(currentVersion);
            LOGGER.debug("Token version validation for user {}: token={}, current={}, valid={}",
                    userId, tokenVersion, currentVersion, isValid);
            return isValid;
        } catch (Exception e) {
            LOGGER.error("Failed to validate token version for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all user token versions currently stored in Redis.
     * This method scans for keys matching "user:token:version:*" and returns a map
     * of these keys and their values.
     *
     * @return A {@link Map} where keys are Redis keys (e.g.,
     *         "user:token:version:123") and values are the token versions.
     */
    public Map<String, String> getAllUserTokens() {
        Map<String, String> tokens = new HashMap<>();
        try {
            Set<String> keys = redisTemplate.keys("user:token:version:*");
            if (keys != null) {
                for (String key : keys) {
                    String value = redisTemplate.opsForValue().get(key);
                    tokens.put(key, value);
                    LOGGER.debug("Redis Key={} Value={}", key, value);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to fetch all tokens: {}", e.getMessage(), e);
        }
        return tokens;
    }
}
