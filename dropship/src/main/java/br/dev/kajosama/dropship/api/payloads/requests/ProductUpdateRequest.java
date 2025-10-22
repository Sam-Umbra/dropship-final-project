package br.dev.kajosama.dropship.api.payloads.requests;

import java.math.BigDecimal;

import br.dev.kajosama.dropship.domain.model.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(
        @Size(max = 80, min = 5)
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        ProductStatus status,
        String imgUrl,
        @DecimalMin(value = "0.0", inclusive = true)
        @DecimalMax(value = "100.0", inclusive = true)
        BigDecimal discount) {

}
