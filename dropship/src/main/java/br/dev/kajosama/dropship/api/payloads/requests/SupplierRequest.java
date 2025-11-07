package br.dev.kajosama.dropship.api.payloads.requests;

import org.hibernate.validator.constraints.br.CNPJ;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
    @Size(max = 150)
    @NotBlank
    String name,
    @CNPJ
    @NotBlank
    String cnpj,
    String dbUrl,
    @NotBlank
    String email,
    @ValidPhone
    @NotBlank
    String phone
    
) {

}
