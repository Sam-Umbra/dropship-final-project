package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.ArrayList;
import java.util.List;

import br.dev.kajosama.dropship.domain.model.entities.Category;

/**
 * Represents a summary of a category, used in different contexts like product
 * listings. It can represent a full category tree or just the hierarchical path
 * to a specific category.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the category.
 * @param name The name of the category.
 * @param subCategories A list of child categories.
 */
public record CategorySummary(
        Long id,
        String name,
        List<CategorySummary> subCategories
        ) {

    /**
     * Converts a {@link Category} entity into a {@link CategorySummary},
     * including its ENTIRE subtree of subcategories.
     * <p>
     * This should be used for displaying a full category tree, not for
     * individual product details.
     *
     * @param category The category entity to convert.
     * @return A {@link CategorySummary} representing the full category tree.
     */
    public static CategorySummary fromEntity(Category category) {
        if (category == null) {
            return null;
        }

        return new CategorySummary(
                category.getId(),
                category.getName(),
                category.getSubCategories().stream()
                        .map(CategorySummary::fromEntity)
                        .toList()
        );
    }

    /**
     * Converts a {@link Category} entity into a nested {@link CategorySummary}
     * that represents ONLY the hierarchical path from the root to the given
     * category.
     * <p>
     * This is ideal for product listings to show the breadcrumb path (e.g.,
     * "Electronics > Accessories") without including other sibling or child
     * categories.
     *
     * @param category The leaf category entity from which to build the path.
     * @return A nested {@link CategorySummary} representing the hierarchical
     * path.
     */
    public static CategorySummary fromEntityWithPath(Category category) {
        if (category == null) {
            return null;
        }

        // Constrói o caminho da raiz até a categoria atual
        List<Category> path = new ArrayList<>();
        Category current = category;

        // Sobe até a raiz
        while (current != null) {
            path.add(0, current); // Adiciona no início para manter ordem: raiz -> folha
            current = current.getParentCategory();
        }

        // Constrói a estrutura aninhada a partir do caminho
        return buildFromPath(path, 0);
    }

    /**
     * Recursively builds the nested {@link CategorySummary} structure from a
     * list of categories representing the path.
     *
     * @param path The list of {@link Category} entities, ordered from root to
     * leaf.
     * @param index The current position in the path list to process.
     * @return The constructed {@link CategorySummary}.
     */
    private static CategorySummary buildFromPath(List<Category> path, int index) {
        if (index >= path.size()) {
            return null;
        }

        Category current = path.get(index);
        List<CategorySummary> subCategories = new ArrayList<>();

        // Se não é a última categoria do caminho, adiciona a próxima como subcategoria
        if (index < path.size() - 1) {
            CategorySummary next = buildFromPath(path, index + 1);
            if (next != null) {
                subCategories.add(next);
            }
        }

        return new CategorySummary(
                current.getId(),
                current.getName(),
                subCategories
        );
    }
}
