package br.dev.kajosama.dropship.api.payloads.requests;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRegisterRequest(
    @NotBlank
        @Size(max = 80, min = 5)
        String name,
        @NotBlank
        String description,
        @NotNull
        BigDecimal price,
        @NotNull
        Integer stock,
        @NotBlank
        String imgUrl,
        @DecimalMin(value = "0.0", inclusive = true)
        @DecimalMax(value = "100.0", inclusive = true)
        BigDecimal discount,
        @NotNull
        Long supplierId,
        @NotEmpty
        List<Long> categoryIds
        ) {

}
