package br.dev.kajosama.dropship.security.jwt;

import br.dev.kajosama.dropship.domain.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {

    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 horas

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    // Gera o token JWT
    public String generateAcessToken(User user) {
        SecretKey secretKey = getSecretKey();

        Claims claims = Jwts.claims().setSubject(user.getEmail()); // Email como subject
        claims.put("userId", user.getId());                         // Adiciona o ID como claim
        claims.put("roles", user.getUserRoles());                       // Adiciona as roles

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("SamUmbra")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Valida o token JWT
    public boolean validateAccessToken(String token) {
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
        } catch (SignatureException ex) {
            LOGGER.error("Invalid signature");
        }
        return false;
    }

    // Recupera o e-mail do subject
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Recupera o ID do usuário a partir da claim
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    // Recupera as roles do token
    public List<String> getRoles(String token) {
        return parseClaims(token).get("roles", List.class);
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
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}