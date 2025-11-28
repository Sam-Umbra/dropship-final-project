package br.dev.kajosama.dropship.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines the available sorting options for product reviews.
 * This enum is used as a request parameter to specify the desired order of review lists.
 */
public enum ReviewSortBy {
    /**
     * Sorts reviews from the most recent to the oldest.
     */
    @JsonProperty("newest")
    NEWEST,
    /**
     * Sorts reviews from the oldest to the most recent.
     */
    @JsonProperty("oldest")
    OLDEST,
    /**
     * Sorts reviews by rating in descending order (highest first).
     */
    @JsonProperty("highest_rating")
    HIGHEST_RATING,
    /**
     * Sorts reviews by rating in ascending order (lowest first).
     */
    @JsonProperty("lowest_rating")
    LOWEST_RATING
}