package br.dev.kajosama.dropship.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import br.dev.kajosama.dropship.api.payloads.requests.SupplierUpdateRequest;
import br.dev.kajosama.dropship.domain.model.entities.Supplier;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    // Provide a default implementation to avoid MapStruct annotation processor NPE;
    // perform manual field copying in service code if needed.
    default void updateSupplierFromDto(SupplierUpdateRequest dto, @MappingTarget Supplier entity) {
        // Intentionally left blank: implement field-by-field copying here if required,
        // or keep as no-op so existing entity fields are preserved when DTO has nulls.
    }
}