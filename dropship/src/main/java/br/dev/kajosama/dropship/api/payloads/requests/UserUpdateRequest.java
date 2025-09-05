package br.dev.kajosama.dropship.api.payloads.requests;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Nullable
        String name,
        @Nullable
        @Email
        String email,
        @Nullable
        @CPF
        String cpf,
        @Nullable
        @Size(max = 13, min = 13)
        String phone,
        @Nullable
        @Past
        LocalDate birthDate) {

}
