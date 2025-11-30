package br.dev.kajosama.dropship.api.payloads.requests;

import org.hibernate.validator.constraints.br.CNPJ;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.validation.constraints.Size;

/**
 * Represents the request payload for updating an existing supplier. All fields
 * are optional to allow for partial updates.
 *
 * @author Sam_Umbra
 * @param name The new name for the supplier.
 * @param cnpj The new CNPJ for the supplier.
 * @param dbUrl The new database URL for the supplier.
 * @param email The new contact email for the supplier.
 * @param phone The new contact phone number for the supplier.
 * @param image The new URL for the supplier's image/logo.
 */
public record SupplierUpdateRequest(
        /**
         * The new name for the supplier.
         */
        @Size(max = 150)
        String name,
        /**
         * The new CNPJ for the supplier.
         */
        @CNPJ
        String cnpj,
        /**
         * The new database URL for the supplier.
         */
        String dbUrl,
        /**
         * The new contact email for the supplier.
         */
        String email,
        /**
         * The new contact phone number for the supplier.
         */
        @ValidPhone
        String phone,
        /**
         * The new URL for the supplier's image/logo.
         */
        String image
        ) {

}
