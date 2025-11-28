package br.dev.kajosama.dropship.api.payloads.requests;

import java.math.BigDecimal;

import br.dev.kajosama.dropship.domain.model.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * Represents the request payload for updating an existing product. All fields
 * are optional to allow for partial updates.
 *
 * @author Sam_Umbra
 * @param name The new name of the product.
 * @param description The new description for the product.
 * @param price The new price for the product.
 * @param stock The new stock quantity for the product.
 * @param status The new status of the product (e.g., AVAILABLE, UNAVAILABLE).
 * @param imgUrl The new URL for the product's image.
 * @param discount The new discount percentage for the product.
 */
public record ProductUpdateRequest(
        @Size(max = 80, min = 5)
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        /**
         * The new status of the product (e.g., AVAILABLE, UNAVAILABLE).
         */
        ProductStatus status,
        String imgUrl,
        /**
         * The new discount percentage for the product. Must be between 0.0 and
         * 100.0.
         */
        @DecimalMin(value = "0.0", inclusive = true)
        @DecimalMax(value = "100.0", inclusive = true)
        BigDecimal discount) {

}
