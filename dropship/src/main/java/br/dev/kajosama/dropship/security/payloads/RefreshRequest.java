package br.dev.kajosama.dropship.security.payloads;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the request payload for refreshing an access token.
 *
 * @author Sam_Umbra
 * @param refreshToken The valid JWT refresh token.
 */
public record RefreshRequest(@NotBlank
        /**
         * The valid JWT refresh token.
         */
        String refreshToken) {

}
