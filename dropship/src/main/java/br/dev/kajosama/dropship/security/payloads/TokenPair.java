package br.dev.kajosama.dropship.security.payloads;

/**
 * Represents a pair of JWT tokens, typically an access token and a refresh
 * token. This is used internally or as a response for token generation/refresh
 * operations.
 *
 * @author Sam_Umbra
 * @param accessToken The JWT access token.
 * @param refreshToken The JWT refresh token.
 */
public record TokenPair(
        /**
         * The JWT access token.
         */
        String accessToken,
        /**
         * The JWT refresh token.
         */
        String refreshToken
        ) {

}
