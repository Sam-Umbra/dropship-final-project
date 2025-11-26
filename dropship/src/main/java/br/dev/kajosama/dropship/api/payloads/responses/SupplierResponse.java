package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.dev.kajosama.dropship.domain.model.entities.Supplier;
import br.dev.kajosama.dropship.domain.model.entities.SupplierUser;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.model.enums.SupplierTier;

public record SupplierResponse(
        Long id,
        String name,
        String cnpj,
        String email,
        String phone,
        String dbUrl,
        SupplierTier tier,
        AccountStatus status,
        boolean aproved,
        UserSummaryResponse primaryUser,
        String image
        ) {

    public static SupplierResponse fromEntity(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        UserSummaryResponse primaryUser = Optional.ofNullable(supplier.getSupplierUsers())
                .flatMap(users -> users.stream()
                .map(SupplierUser::getUser)
                .filter(user -> user.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_SUPPLIER_PRIMARY".equals(ur.getRole().getName())))
                .findFirst()
                )
                .map(user -> new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        ))
                .orElse(null);

        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getCnpj(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getDbUrl(),
                supplier.getTier(),
                supplier.getStatus(),
                supplier.getApproved(),
                primaryUser,
                supplier.getImage()
        );

    }

    public static List<SupplierResponse> fromEntityList(List<Supplier> suppliers) {
        if (suppliers == null) {
            return List.of();
        }
        return suppliers.stream()
                .map(SupplierResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
