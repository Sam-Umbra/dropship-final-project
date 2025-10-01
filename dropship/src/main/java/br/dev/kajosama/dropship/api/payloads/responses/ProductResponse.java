package br.dev.kajosama.dropship.api.payloads.responses;

import java.math.BigDecimal;

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
        CategorySummary categorySummary
) {
    public static ProductResponse fromEntity(Product product) {
        if (product == null) return null;

        CategorySummary summary = product.getCategories().stream()
                .filter(c -> c.getParentCategory() == null) // raiz
                .map(CategorySummary::fromEntity)
                .findFirst()
                .orElse(null);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().getAmount(),
                product.getStock(),
                product.getStatus(),
                product.getDiscount(),
                product.getImgUrl(),
                summary
        );
    }
}
