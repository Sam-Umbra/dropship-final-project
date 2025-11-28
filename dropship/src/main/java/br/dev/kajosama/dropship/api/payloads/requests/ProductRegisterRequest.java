package br.dev.kajosama.dropship.api.payloads.requests;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents the request payload for registering a new product.
 *
 * @author Sam_Umbra
 * @param name The name of the product.
 * @param description A detailed description of the product.
 * @param price The price of the product.
 * @param stock The available stock quantity for the product.
 * @param imgUrl The URL for the product's primary image.
 * @param discount The discount percentage to be applied to the product's price.
 * @param supplierId The ID of the supplier providing the product.
 * @param categoryIds A list of category IDs to which the product belongs.
 */
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
        /**
         * The discount percentage to be applied to the product's price. Must be
         * between 0.0 and 100.0.
         */
        @DecimalMin(value = "0.0", inclusive = true)
        @DecimalMax(value = "100.0", inclusive = true)
        BigDecimal discount,
        /**
         * The ID of the supplier providing the product.
         */
        @NotNull
        Long supplierId,
        /**
         * A list of category IDs to which the product belongs. Must not be
         * empty.
         */
        @NotEmpty
        List<Long> categoryIds
        ) {

}
