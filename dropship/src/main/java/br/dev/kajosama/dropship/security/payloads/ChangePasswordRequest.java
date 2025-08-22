package br.dev.kajosama.dropship.security.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,
        @NotBlank
        @Size(min = 6)
        String newPassword
        ) {

}
