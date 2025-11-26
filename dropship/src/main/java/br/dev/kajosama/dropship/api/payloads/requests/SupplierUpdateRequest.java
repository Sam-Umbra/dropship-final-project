package br.dev.kajosama.dropship.api.payloads.requests;

import org.hibernate.validator.constraints.br.CNPJ;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.validation.constraints.Size;

public record SupplierUpdateRequest(
    @Size(max = 150)
    String name,
    @CNPJ
    String cnpj,
    String dbUrl,
    String email,
    @ValidPhone
    String phone,
    String image
) {

}
