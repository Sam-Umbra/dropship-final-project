package br.dev.kajosama.dropship.api.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import br.dev.kajosama.dropship.api.payloads.requests.SupplierUpdateRequest;
import br.dev.kajosama.dropship.domain.model.entities.Supplier;

/**
 * @author Sam_Umbra
 * @Description Mapper interface for converting between {@link Supplier}
 *              entities and DTOs.
 *              This interface uses MapStruct to generate the implementation for
 *              mapping operations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    /**
     * Updates an existing {@link Supplier} entity from a
     * {@link SupplierUpdateRequest DTO}.
     * Properties with null values in the DTO will be ignored and not applied to the
     * entity.
     * The 'id', 'approved', 'tier', 'createdAt', 'updatedAt', 'deletedAt',
     * 'status',
     * 'commissionRate', 'supplierUsers', and 'products' fields are explicitly
     * ignored as they are
     * managed by the persistence context or business logic.
     *
     * @param dto    The {@link SupplierUpdateRequest DTO} containing the updated
     *               supplier information.
     * @param entity The target {@link Supplier} entity to be updated.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "tier", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "commissionRate", ignore = true)
    @Mapping(target = "supplierUsers", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateSupplierFromDto(SupplierUpdateRequest dto, @MappingTarget Supplier entity);
}