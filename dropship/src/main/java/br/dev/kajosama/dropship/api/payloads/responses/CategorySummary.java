package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.ArrayList;
import java.util.List;

import br.dev.kajosama.dropship.domain.model.entities.Category;

public record CategorySummary(
    Long id,
    String name,
    List<CategorySummary> subCategories
) {
    /**
     * Converte a categoria com TODAS suas subcategorias (árvore completa)
     * USE APENAS para listar categorias, NÃO para produtos!
     */
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

    /**
     * Converte APENAS o caminho hierárquico da categoria atual até a raiz
     * USE para produtos: mostra apenas Eletrônicos > Acessórios (sem subcategorias extras)
     */
    public static CategorySummary fromEntityWithPath(Category category) {
        if (category == null) return null;

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
     * Constrói recursivamente a estrutura aninhada a partir do caminho
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