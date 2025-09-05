package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.Set;
import java.util.stream.Collectors;

import br.dev.kajosama.dropship.domain.model.User;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

public record SimpleUserResponse(
    Long id,
    String name,
    String email,
    AccountStatus status,
    Set<String> roles
) {
    public static SimpleUserResponse fromEntity(User user) {
        return new SimpleUserResponse(
            user.getId(), 
            user.getName(), 
            user.getEmail(), 
            user.getStatus(), 
            user.getUserRoles()
                .stream().map(
                    userRole -> userRole.getRole().getName())
                .collect(Collectors.toSet()));
    }
}