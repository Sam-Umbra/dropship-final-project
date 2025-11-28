package br.dev.kajosama.dropship.api.payloads.requests;

import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
    @NotNull @Min(0) @Max(5)
    Integer rating,

    @NotBlank
    String comment,

    Set<String> imageUrls
) {}