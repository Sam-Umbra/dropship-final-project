package br.dev.kajosama.dropship.security.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents the request payload for changing a user's password.
 *
 * @author Sam_Umbra
 * @param currentPassword The user's current password for verification.
 * @param newPassword The desired new password. Must be at least 6 characters
 * long.
 */
public record ChangePasswordRequest(
        /**
         * The user's current password for verification.
         */
        @NotBlank
        String currentPassword,
        /**
         * The desired new password. Must be at least 6 characters long.
         */
        @NotBlank
        @Size(min = 6)
        String newPassword
        ) {

}
