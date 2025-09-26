package br.dev.kajosama.dropship.api.payloads.requests;

import java.math.BigDecimal;

import br.dev.kajosama.dropship.domain.model.enums.ProductStatus;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @Size(max = 80, min = 5)
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        ProductStatus status,
        String imgUrl) {

}
