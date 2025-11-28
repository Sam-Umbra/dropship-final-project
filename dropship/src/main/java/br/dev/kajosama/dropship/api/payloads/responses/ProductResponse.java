package br.dev.kajosama.dropship.api.payloads.responses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.enums.ProductStatus;

/**
 * Represents the detailed data transfer object for a product.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the product.
 * @param name The name of the product.
 * @param description A detailed description of the product.
 * @param price The price of the product.
 * @param stock The available stock quantity.
 * @param status The current status of the product (e.g., AVAILABLE).
 * @param discount The discount percentage applied to the product.
 * @param imgUrl The URL for the product's primary image.
 * @param supplierId The ID of the product's supplier.
 * @param supplierName The name of the product's supplier.
 * @param categories A list of hierarchical category paths for the product.
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        ProductStatus status,
        BigDecimal discount,
        String imgUrl,
        /**
         * The ID of the product's supplier.
         */
        Long supplierId,
        /**
         * The name of the product's supplier.
         */
        String supplierName,
        /**
         * A list of hierarchical category paths for the product.
         */
        List<CategorySummary> categories
        ) {

    /**
     * Creates a {@link ProductResponse} from a {@link Product} entity. It
     * processes the product's categories to create a merged, hierarchical path
     * view.
     *
     * @param product The {@link Product} entity to convert.
     * @return A new {@link ProductResponse} object, or {@code null} if the
     * input is null.
     */
    public static ProductResponse fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        // Monta o caminho de cada categoria
        List<CategorySummary> summaries = product.getCategories().stream()
                .map(CategorySummary::fromEntityWithPath)
                .toList();

        // Mescla duplicados de forma recursiva
        List<CategorySummary> merged = mergeCategories(summaries);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().getAmount(),
                product.getStock(),
                product.getStatus(),
                product.getDiscount(),
                product.getImgUrl(),
                product.getSupplier().getId(),
                product.getSupplier().getName(),
                merged
        );
    }

    /**
     * Merges a list of category paths into a single hierarchical structure.
     * This is used to combine multiple category assignments for a product
     * (e.g., "A > B" and "A > C") into a single tree ("A > [B, C]").
     *
     * @param categories A list of {@link CategorySummary} paths to merge.
     * @return A list of merged {@link CategorySummary} objects representing the
     * top-level categories.
     */
    private static List<CategorySummary> mergeCategories(List<CategorySummary> categories) {
        Map<Long, CategorySummary> mergedMap = new LinkedHashMap<>();

        for (CategorySummary cat : categories) {
            if (mergedMap.containsKey(cat.id())) {
                // Já existe, mescla subcategorias recursivamente
                CategorySummary existing = mergedMap.get(cat.id());
                List<CategorySummary> mergedSubs = mergeCategories(
                        concatLists(existing.subCategories(), cat.subCategories())
                );
                mergedMap.put(cat.id(), new CategorySummary(cat.id(), cat.name(), mergedSubs));
            } else {
                // Insere novo
                mergedMap.put(cat.id(),
                        new CategorySummary(
                                cat.id(),
                                cat.name(),
                                mergeCategories(cat.subCategories()) // recursivo
                        )
                );
            }
        }

        return new ArrayList<>(mergedMap.values());
    }

    /**
     * A utility method to concatenate two lists of {@link CategorySummary}.
     *
     * @param a The first list.
     * @param b The second list.
     * @return A new list containing all elements from both lists.
     */
    private static List<CategorySummary> concatLists(List<CategorySummary> a, List<CategorySummary> b) {
        List<CategorySummary> result = new ArrayList<>(a);
        result.addAll(b);
        return result;
    }
}
