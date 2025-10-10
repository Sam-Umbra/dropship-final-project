package br.dev.kajosama.dropship.api.payloads.requests;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(
        @Nullable
        String name,
        @Nullable
        @Email
        String email,
        @Nullable
        @CPF
        String cpf,
        @Nullable
        @Size(max = 15, min = 10)
        @ValidPhone
        String phone,
        @Nullable
        @Past
        LocalDate birthDate) {

}
