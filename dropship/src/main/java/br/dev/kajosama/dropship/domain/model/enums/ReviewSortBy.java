package br.dev.kajosama.dropship.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReviewSortBy {
    @JsonProperty("newest")
    NEWEST,
    @JsonProperty("oldest")
    OLDEST,
    @JsonProperty("highest_rating")
    HIGHEST_RATING,
    @JsonProperty("lowest_rating")
    LOWEST_RATING
}