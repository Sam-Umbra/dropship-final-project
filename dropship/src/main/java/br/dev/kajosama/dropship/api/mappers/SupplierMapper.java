package br.dev.kajosama.dropship.api.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import br.dev.kajosama.dropship.api.payloads.requests.SupplierUpdateRequest;
import br.dev.kajosama.dropship.domain.model.entities.Supplier;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

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