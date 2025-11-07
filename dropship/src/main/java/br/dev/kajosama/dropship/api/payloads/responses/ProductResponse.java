package br.dev.kajosama.dropship.api.payloads.responses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.enums.ProductStatus;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    ProductStatus status,
    BigDecimal discount,
    String imgUrl,
    Long supplierId,
    String supplierName,
    List<CategorySummary> categories
) {
    public static ProductResponse fromEntity(Product product) {
        if (product == null) return null;

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
     * Junta categorias duplicadas (mesclando subcategorias de forma recursiva).
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

    private static List<CategorySummary> concatLists(List<CategorySummary> a, List<CategorySummary> b) {
        List<CategorySummary> result = new ArrayList<>(a);
        result.addAll(b);
        return result;
    }
}