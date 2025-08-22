package br.dev.kajosama.dropship.security.services;

import java.time.Duration;
import java.util.Optional;

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
        String key = "user:token:version:" + userId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofDays(30));
        LOGGER.info("Incremented token version for user: {}", userId);
    }

    public Long getUserTokenVersion(Long userId) {
        String key = "user:token:version:" + userId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .map(Long::parseLong)
                .orElse(0L);
    }

    public boolean isTokenVersionValid(Long userId, Long tokenVersion) {
        Long currentVersion = getUserTokenVersion(userId);
        return tokenVersion.equals(currentVersion);
    }
}