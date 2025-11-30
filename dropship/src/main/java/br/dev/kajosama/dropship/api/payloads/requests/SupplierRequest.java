package br.dev.kajosama.dropship.api.payloads.requests;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.br.CNPJ;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents the data transfer object for creating a new supplier.
 *
 * @author Sam_Umbra
 * @param name The supplier's name.
 * @param cnpj The supplier's CNPJ (Brazilian company taxpayer registry).
 * @param dbUrl The database connection URL for the supplier, if applicable.
 * @param email The supplier's contact email address.
 * @param phone The supplier's contact phone number.
 * @param commissionRate The commission rate for the supplier.
 */
public record SupplierRequest(
        /**
         * The supplier's name.
         */
        @Size(max = 150)
        @NotBlank
        String name,
        /**
         * The supplier's CNPJ (Brazilian company taxpayer registry).
         */
        @CNPJ
        @NotBlank
        String cnpj,
        /**
         * The database connection URL for the supplier, if applicable.
         */
        String dbUrl,
        /**
         * The supplier's contact email address.
         */
        @NotBlank
        String email,
        /**
         * The supplier's contact phone number.
         */
        @ValidPhone
        @NotBlank
        String phone,
        /**
         * The commission rate for the supplier. Must be a value between 0.0 and
         * 100.0.
         */
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @DecimalMax(value = "100.0", inclusive = true)
        BigDecimal commissionRate
        ) {

}
