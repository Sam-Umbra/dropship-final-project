package br.dev.kajosama.dropship.security.services;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    private final RedisTemplate<String, String> redisTemplate;

    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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
