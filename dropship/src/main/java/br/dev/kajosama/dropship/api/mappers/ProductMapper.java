package br.dev.kajosama.dropship.api.mappers;

import java.math.BigDecimal;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import br.dev.kajosama.dropship.api.payloads.requests.ProductUpdateRequest;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.objects.Price;

/**
 * @author Sam_Umbra
 * @Description Mapper interface for converting between {@link Product} entities
 *              and DTOs.
 *              This interface uses MapStruct to generate the implementation for
 *              mapping operations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    /**
     * Updates an existing {@link Product} entity from a {@link ProductUpdateRequest
     * DTO}.
     * Properties with null values in the DTO will be ignored and not applied to the
     * entity.
     * The 'id' and 'updatedAt' fields are explicitly ignored as they are managed by
     * the persistence context.
     * The 'price' field is mapped using custom default methods to handle conversion
     * between BigDecimal and Price.
     *
     * @param dto    The {@link ProductUpdateRequest DTO} containing the updated
     *               product information.
     * @param entity The target {@link Product} entity to be updated.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "price", target = "price")
    void updateProductFromDto(ProductUpdateRequest dto, @MappingTarget Product entity);

    /**
     * Maps a {@link BigDecimal} value to a {@link Price} object.
     *
     * @param value The BigDecimal amount.
     * @return A new {@link Price} object, or null if the input value is null.
     */
    default Price map(BigDecimal value) {
        if (value == null) {
            return null;
        }
        Price price = Price.of(value);
        return price;
    }

    /**
     * Maps a {@link Price} object to its {@link BigDecimal} amount.
     *
     * @param price The Price object.
     * @return The BigDecimal amount of the price, or null if the input price is
     *         null.
     */
    default BigDecimal map(Price price) {
        if (price == null) {
            return null;
        }
        return price.getAmount();
    }

}
