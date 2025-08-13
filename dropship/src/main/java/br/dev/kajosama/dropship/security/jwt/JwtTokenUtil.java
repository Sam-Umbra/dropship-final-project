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

    // Corrigido: Injetar JwtProperties via construtor
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public JwtTokenUtil(JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }

    // Gera o token JWT
    public String generateAccessToken(User user) {
        try {
            SecretKey secretKey = getSecretKey();

            // Extrai apenas os nomes das roles (não os objetos completos)
            List<String> roleNames = user.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .collect(Collectors.toList());

            Claims claims = Jwts.claims().setSubject(user.getEmail());
            claims.put("userId", user.getId());
            claims.put("name", user.getName());
            claims.put("roles", roleNames); // Lista simples de strings
            claims.put("status", user.getStatus().toString());

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuer("DropShip-API")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

        } catch (InvalidKeyException e) {
            LOGGER.error("Erro ao gerar token JWT para usuário {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Erro ao gerar token de acesso", e);
        }
    }

    public String generateRefreshToken(User user) {
    try {
        SecretKey secretKey = getSecretKey();

        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("userId", user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("DropShip-API")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE_DURATION))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

    } catch (InvalidKeyException e) {
        LOGGER.error("Erro ao gerar refresh token para usuário {}: {}", user.getEmail(), e.getMessage());
        throw new RuntimeException("Erro ao gerar refresh token", e);
    }
}

    // Valida o token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
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

    // Recupera o e-mail do subject
    public String getEmail(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (Exception e) {
            LOGGER.error("Erro ao extrair email do token: {}", e.getMessage());
            throw new RuntimeException("Token inválido", e);
        }
    }

    // Recupera o ID do usuário a partir da claim
    public Long getUserId(String token) {
        try {
            Object userIdClaim = parseClaims(token).get("userId");
            if (userIdClaim instanceof Integer integer) {
                return integer.longValue();
            }
            return parseClaims(token).get("userId", Long.class);
        } catch (Exception e) {
            LOGGER.error("Erro ao extrair userId do token: {}", e.getMessage());
            throw new RuntimeException("Token inválido", e);
        }
    }

    public String getUserName(String token) {
        try {
            return parseClaims(token).get("name", String.class);
        } catch (Exception e) {
            LOGGER.error("Erro ao extrair nome do token: {}", e.getMessage());
            return null;
        }
    }

    // Recupera as roles do token
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        try {
            Object rolesObj = parseClaims(token).get("roles");

            if (rolesObj instanceof List) {
                return (List<String>) rolesObj;
            }

            // Fallback para casos onde pode estar como Set ou outro tipo
            return objectMapper.convertValue(rolesObj, new TypeReference<List<String>>() {
            });

        } catch (IllegalArgumentException e) {
            LOGGER.error("Erro ao extrair roles do token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Verifica se o token está expirado
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
     * Recupera o tempo restante do token em milissegundos
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    // Método auxiliar para parsear claims
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Gera a chave secreta decodificada
    private SecretKey getSecretKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (WeakKeyException e) {
            LOGGER.error("Erro ao gerar chave secreta: {}", e.getMessage());
            throw new RuntimeException("Configuração JWT inválida", e);
        }
    }
}
