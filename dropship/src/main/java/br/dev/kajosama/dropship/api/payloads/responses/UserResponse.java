package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.Set;

import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

public record UserResponse(
    Long id,
    String name,
    String email,
    AccountStatus status,
    Set<String> roles
) {
}