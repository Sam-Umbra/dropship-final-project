package br.dev.kajosama.dropship.api.payloads.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

/**
 * Represents a detailed data transfer object for a user, including sensitive
 * and administrative information. This response is typically used in admin-only
 * contexts.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the user.
 * @param name The user's full name.
 * @param email The user's email address.
 * @param password The user's encrypted password.
 * @param createdAt The timestamp when the user account was created.
 * @param updatedAt The timestamp when the user account was last updated.
 * @param deletedAt The timestamp when the user account was soft-deleted.
 * @param cpf The user's CPF (Brazilian individual taxpayer registry).
 * @param phone The user's phone number.
 * @param status The current status of the user's account.
 * @param birthDate The user's date of birth.
 * @param emailVerifiedAt The timestamp when the user's email was verified.
 * @param lastLogin The timestamp of the user's last login.
 * @param lastExit The timestamp of the user's last exit/logout.
 * @param roles A set of role names assigned to the user.
 */
public record ComplexUserResponse(
        Long id,
        String name,
        String email,
        /**
         * The user's encrypted password.
         */
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        String cpf,
        String phone,
        AccountStatus status,
        LocalDate birthDate,
        LocalDateTime emailVerifiedAt,
        /**
         * The timestamp of the user's last login.
         */
        LocalDateTime lastLogin,
        /**
         * The timestamp of the user's last exit/logout.
         */
        LocalDateTime lastExit,
        /**
         * A set of role names assigned to the user (e.g., "ROLE_USER",
         * "ROLE_ADMIN").
         */
        Set<String> roles
        ) {

    /**
     * Creates a {@link ComplexUserResponse} from a {@link User} entity.
     *
     * @param user The {@link User} entity to convert.
     * @return A new {@link ComplexUserResponse} object.
     */
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
