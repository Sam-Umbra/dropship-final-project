package br.dev.kajosama.dropship.api.payloads.responses;

/**
 * Represents a minimal summary of a user, typically for embedding in other
 * responses.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the user.
 * @param name The user's full name.
 * @param email The user's email address.
 */
public record UserSummaryResponse(
        Long id,
        String name,
        String email
        ) {

}
