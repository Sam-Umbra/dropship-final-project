package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.List;

import br.dev.kajosama.dropship.domain.model.entities.Category;

public record CategorySummary(
    Long id,
    String name,
    List<CategorySummary> subCategories
) {
    public static CategorySummary fromEntity(Category category) {
        if (category == null) return null;

        return new CategorySummary(
                category.getId(),
                category.getName(),
                category.getSubCategories().stream()
                        .map(CategorySummary::fromEntity)
                        .toList()
        );
    }
}