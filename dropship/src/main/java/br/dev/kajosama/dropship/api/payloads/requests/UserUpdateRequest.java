package br.dev.kajosama.dropship.api.payloads.requests;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        String name,
        @Email String email,
        @CPF String cpf,
        @Size(max = 13, min = 13) String phone,
        LocalDate birthDate) {

}
