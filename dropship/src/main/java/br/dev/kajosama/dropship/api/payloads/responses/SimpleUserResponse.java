package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.Set;
import java.util.stream.Collectors;

import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

/**
 * Represents a simplified data transfer object for a user, containing basic,
 * non-sensitive information.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the user.
 * @param name The user's full name.
 * @param email The user's email address.
 * @param status The current status of the user's account.
 * @param roles A set of role names assigned to the user.
 */
public record SimpleUserResponse(
        Long id,
        String name,
        String email,
        AccountStatus status,
        /**
         * A set of role names assigned to the user (e.g., "ROLE_USER",
         * "ROLE_ADMIN").
         */
        Set<String> roles
        ) {

    /**
     * Creates a {@link SimpleUserResponse} from a {@link User} entity.
     *
     * @param user The {@link User} entity to convert.
     * @return A new {@link SimpleUserResponse} object.
     */
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
