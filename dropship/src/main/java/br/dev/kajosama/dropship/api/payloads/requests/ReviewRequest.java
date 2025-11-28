package br.dev.kajosama.dropship.api.payloads.requests;

import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the request payload for creating a new product review.
 *
 * @author Sam_Umbra
 * @param rating The rating given to the product, from 0 to 5.
 * @param comment The text content of the review.
 * @param imageUrls A set of URLs for images uploaded with the review.
 */
public record ReviewRequest(
    /**
     * The rating given to the product, from 0 to 5.
     */
    @NotNull @Min(0) @Max(5)
    Integer rating,

    /**
     * The text content of the review.
     */
    @NotBlank
    String comment,

    /**
     * A set of URLs for images uploaded with the review.
     */
    Set<String> imageUrls
) {}