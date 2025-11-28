package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.dev.kajosama.dropship.domain.model.entities.Supplier;
import br.dev.kajosama.dropship.domain.model.entities.SupplierUser;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.model.enums.SupplierTier;

/**
 * Represents the data transfer object for a supplier.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the supplier.
 * @param name The supplier's name.
 * @param cnpj The supplier's CNPJ (Brazilian company taxpayer registry).
 * @param email The supplier's contact email.
 * @param phone The supplier's contact phone number.
 * @param dbUrl The supplier's external database URL, if any.
 * @param tier The supplier's tier level (e.g., BRONZE, SILVER, GOLD).
 * @param status The current status of the supplier's account.
 * @param aproved Whether the supplier's account has been approved.
 * @param primaryUser A summary of the primary user associated with the
 * supplier.
 * @param image The URL for the supplier's logo or image.
 */
public record SupplierResponse(
        Long id,
        String name,
        String cnpj,
        String email,
        String phone,
        String dbUrl,
        SupplierTier tier,
        AccountStatus status,
        /**
         * Whether the supplier's account has been approved.
         */
        boolean aproved,
        /**
         * A summary of the primary user associated with the supplier.
         */
        UserSummaryResponse primaryUser,
        String image
        ) {

    /**
     * Creates a {@link SupplierResponse} from a {@link Supplier} entity. It
     * finds and includes the primary user associated with the supplier.
     *
     * @param supplier The {@link Supplier} entity to convert.
     * @return A new {@link SupplierResponse} object, or {@code null} if the
     * input is null.
     */
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

    /**
     * Converts a list of {@link Supplier} entities to a list of
     * {@link SupplierResponse} objects.
     *
     * @param suppliers The list of {@link Supplier} entities to convert.
     * @return A list of {@link SupplierResponse} objects.
     */
    public static List<SupplierResponse> fromEntityList(List<Supplier> suppliers) {
        if (suppliers == null) {
            return List.of();
        }
        return suppliers.stream()
                .map(SupplierResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
