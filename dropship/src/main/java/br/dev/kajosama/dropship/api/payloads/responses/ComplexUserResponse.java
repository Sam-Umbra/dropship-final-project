package br.dev.kajosama.dropship.api.payloads.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.model.User;

public record ComplexUserResponse(
    Long id,
    String name,
    String email,
    String password,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    String cpf,
    String phone,
    AccountStatus status,
    LocalDate birthDate,
    LocalDateTime emailVerifiedAt,
    LocalDateTime lastLogin,
    LocalDateTime lastExit,
    Set<String> roles
) {
    public static ComplexUserResponse fromEntity(User user) {
        return new ComplexUserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getDeletedAt(),
            user.getCpf(),
            user.getPhone(),
            user.getStatus(),
            user.getBirthDate(),
            user.getEmailVerifiedAt(),
            user.getLastLogin(),
            user.getLastExit(),
            user.getUserRoles()
                    .stream().map(
                            userRole -> userRole.getRole().getName())
                    .collect(Collectors.toSet()));
    }
}
