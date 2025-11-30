package br.dev.kajosama.dropship.api.payloads.requests;

import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Represents the request payload for updating an existing product review.
 *
 * @author Sam_Umbra
 * @param rating The updated rating for the product, from 0 to 5.
 * @param comment The updated text content of the review.
 * @param imageUrls The updated set of image URLs for the review.
 */
public record ReviewUpdateRequest(
        /**
         * The updated rating for the product, from 0 to 5.
         */
        @Min(0)
        @Max(5)
        Integer rating,
        /**
         * The updated text content of the review.
         */
        String comment,
        /**
         * The updated set of image URLs for the review.
         */
        Set<String> imageUrls
        ) {

}
