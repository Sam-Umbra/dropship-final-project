package br.dev.kajosama.dropship.api.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import br.dev.kajosama.dropship.api.payloads.requests.UserUpdateRequest;
import br.dev.kajosama.dropship.domain.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    void updateUserFromDto(UserUpdateRequest dto, @MappingTarget User entity);
}