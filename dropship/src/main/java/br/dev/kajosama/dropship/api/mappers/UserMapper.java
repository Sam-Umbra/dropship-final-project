package br.dev.kajosama.dropship.api.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import br.dev.kajosama.dropship.api.payloads.requests.AccountUpdateRequest;
import br.dev.kajosama.dropship.domain.model.entities.User;

/**
 * @author Sam_Umbra
 * @Description Mapper interface for converting between {@link User} entities
 *              and DTOs.
 *              This interface uses MapStruct to generate the implementation for
 *              mapping operations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Updates an existing {@link User} entity from an {@link AccountUpdateRequest
     * DTO}.
     * Properties with null values in the DTO will be ignored and not applied to the
     * entity.
     * The 'id', 'password', 'createdAt', 'updatedAt', 'deletedAt', 'status',
     * 'emailVerifiedAt', 'lastLogin', 'lastExit', 'userRoles', and 'phone' fields
     * are explicitly ignored
     * as they are managed by the persistence context, security context, or business
     * logic.
     *
     * @param dto    The {@link AccountUpdateRequest DTO} containing the updated
     *               user information.
     * @param entity The target {@link User} entity to be updated.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "lastExit", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    // The 'phone' field is ignored here because its validation and potential
    // normalization
    // might involve business logic that should not be handled directly by the
    // mapper.
    // It's assumed that phone number updates are handled by a dedicated service
    // method
    // that performs necessary validation and formatting before persisting.
    // If direct mapping is desired, ensure that the DTO's phone field is already
    // validated
    // or that a custom mapping method handles the validation.
    @Mapping(target = "phone", ignore = true)
    void updateUserFromDto(AccountUpdateRequest dto, @MappingTarget User entity);
}