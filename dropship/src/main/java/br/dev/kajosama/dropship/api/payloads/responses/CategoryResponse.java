package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.List;

import br.dev.kajosama.dropship.domain.model.entities.Category;

/**
 * Represents the data transfer object for a category, including its direct subcategories.
 * This is used to build a hierarchical view of categories.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the category.
 * @param name The name of the category.
 * @param subCategories A list of {@link CategoryResponse} objects representing the direct children of this category.
 */
public record CategoryResponse(
        Long id,
        String name,
        List<CategoryResponse> subCategories
        ) {

    /**
     * Creates a {@link CategoryResponse} from a {@link Category} entity.
     * This method recursively converts the entire subtree of subcategories.
     *
     * @param category The {@link Category} entity to convert.
     * @return A new {@link CategoryResponse} object, or {@code null} if the input is null.
     */
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
