package br.dev.kajosama.dropship.security.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Sam_Umbra
 * @Description Configuration properties for JSON Web Tokens (JWT).
 *              This class binds properties prefixed with "app.jwt" from the
 *              application's
 *              configuration files (e.g., application.properties or
 *              application.yml)
 *              to Java fields, providing a structured way to access JWT-related
 *              settings.
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * The secret key for JWT signing and verification.
     * This key is crucial for the security of the JWTs.
     */
    private String secret;

    /**
     * Returns the secret key used for JWT signing and verification.
     * 
     * @return The JWT secret key.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Sets the secret key for JWT signing and verification.
     * 
     * @param secret The secret key to set.
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }
}