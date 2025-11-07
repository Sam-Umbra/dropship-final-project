package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.List;

import br.dev.kajosama.dropship.domain.model.entities.Category;

public record CategoryResponse(
        Long id,
        String name,
        List<CategoryResponse> subCategories
        ) {

    public static CategoryResponse fromEntity(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSubCategories().stream()
                        .map(CategoryResponse::fromEntity)
                        .toList()
        );
    }
}
