package br.dev.kajosama.dropship.api.payloads.requests;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

/**
 * Represents the data transfer object for updating a user account. All fields
 * are optional, allowing for partial updates.
 *
 * @author Sam_Umbra
 * @param name The user's full name.
 * @param email The user's email address. Must be a valid email format.
 * @param cpf The user's CPF (Brazilian individual taxpayer registry). Must be a
 * valid CPF format.
 * @param phone The user's phone number. Must be a valid phone format.
 * @param birthDate The user's date of birth. Must be a date in the past.
 */
public record AccountUpdateRequest(
        /**
         * The user's full name.
         */
        @Nullable
        String name,
        /**
         * The user's email address. Must be a valid email format.
         */
        @Nullable
        @Email
        String email,
        /**
         * The user's CPF (Brazilian individual taxpayer registry). Must be a
         * valid CPF format.
         */
        @Nullable
        @CPF
        String cpf,
        /**
         * The user's phone number. Must be a valid phone format.
         */
        @Nullable
        @Size(max = 15, min = 10)
        @ValidPhone
        String phone,
        /**
         * The user's date of birth. Must be a date in the past.
         */
        @Nullable
        @Past
        LocalDate birthDate) {

}
