package br.dev.kajosama.dropship.api.payloads.requests;

import br.dev.kajosama.dropship.domain.model.entities.User;

/**
 * Represents the composite request for registering a new supplier. It includes
 * both the supplier's details and the primary user's details.
 *
 * @author Sam_Umbra
 * @param supplier The details of the supplier to be registered.
 * @param user The details of the primary user account to be associated with the
 * supplier.
 */
public record SupplierRegisterRequest(
        SupplierRequest supplier,
        User user
        ) {

}
