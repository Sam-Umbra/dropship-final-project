package br.dev.kajosama.dropship.security.payloads;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank
        String refreshToken) {

}
