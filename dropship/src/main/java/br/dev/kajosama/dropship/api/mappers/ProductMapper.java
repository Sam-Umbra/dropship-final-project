package br.dev.kajosama.dropship.api.mappers;

import java.math.BigDecimal;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import br.dev.kajosama.dropship.api.payloads.requests.ProductRequest;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.objects.Price;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "price", target = "price")
    void updateProductFromDto(ProductRequest dto, @MappingTarget Product entity);

    default Price map(BigDecimal value) {
        if (value == null) return null;
        Price price = Price.of(value);
        return price;
    }
}
