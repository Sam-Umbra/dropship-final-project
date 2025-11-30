package br.dev.kajosama.dropship.api.payloads.requests;

import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

/**
 * Represents a request to update the status of a user account.
 *
 * @author Sam_Umbra
 * @param status The new status for the account (e.g., ACTIVE, INACTIVE,
 * BANNED).
 */
public record StatusUpdateRequest(AccountStatus status) {

}
