package br.dev.kajosama.dropship.api.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.api.exceptions.EntityAlreadyExistsException;
import br.dev.kajosama.dropship.api.mappers.SupplierMapper;
import br.dev.kajosama.dropship.api.payloads.requests.SupplierRegisterRequest;
import br.dev.kajosama.dropship.api.payloads.requests.SupplierUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.responses.SupplierResponse;
import br.dev.kajosama.dropship.domain.model.entities.Supplier;
import br.dev.kajosama.dropship.domain.model.entities.SupplierUser;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.model.enums.SupplierTier;
import br.dev.kajosama.dropship.domain.repositories.SupplierRepository;
import br.dev.kajosama.dropship.domain.repositories.SupplierUserRepository;
import br.dev.kajosama.dropship.security.entities.Role;
import br.dev.kajosama.dropship.security.jwt.JwtTokenUtil;
import br.dev.kajosama.dropship.security.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link Supplier} entities.
 *              Provides business logic for supplier-related operations such as
 *              registration,
 *              updates, deletion, and user association. It interacts with
 *              {@link SupplierRepository},
 *              {@link SupplierMapper}, {@link UserService},
 *              {@link RoleRepository}, {@link SupplierUserRepository},
 *              {@link EmailService}, and {@link JwtTokenUtil}.
 */
@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SupplierUserRepository supplierUserRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenUtil jwtUtil;

    /**
     * Checks if a supplier exists by their ID.
     *
     * @param id The ID of the supplier to check.
     * @return True if a supplier with the specified ID exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return supplierRepository.existsById(id);
    }

    /**
     * Checks if a supplier with the given email already exists.
     *
     * @param email The email address to check for existence.
     * @throws EntityAlreadyExistsException If a supplier with the same email
     *                                      already exists.
     */
    public void existsByEmail(String email) {
        if (supplierRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Supplier", "contact email", email);
        }
    }

    /**
     * Checks if a supplier with the given CNPJ already exists.
     *
     * @param cnpj The CNPJ to check for existence.
     * @throws EntityAlreadyExistsException If a supplier with the same CNPJ already
     *                                      exists.
     */
    public void existsByCnpj(String cnpj) {
        if (supplierRepository.existsByCnpj(cnpj)) {
            throw new EntityAlreadyExistsException("Supplier", "cnpj", cnpj);
        }
    }

    /**
     * Retrieves a list of all registered suppliers.
     *
     * @return A {@link List} of {@link SupplierResponse} objects representing all
     *         suppliers.
     * @throws EntityNotFoundException If no suppliers are found in the system.
     */
    public List<SupplierResponse> findAllSupplier() {
        if (supplierRepository.findAll().isEmpty()) {
            throw new EntityNotFoundException("No suppliers found");
        }
        List<Supplier> suppliers = supplierRepository.findAll();
        return SupplierResponse.fromEntityList(suppliers);
    }

    /**
     * Retrieves a supplier by their ID.
     *
     * @param id The ID of the supplier to retrieve.
     * @return A {@link SupplierResponse} object representing the found supplier.
     * @throws EntityNotFoundException If no supplier with the given ID is found.
     */
    public SupplierResponse findSupplierbyId(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with ID: {" + id + "} NOT FOUND"));
        return SupplierResponse.fromEntity(supplier);
    }

    public List<SupplierResponse> findSuppliersByName(String name) {
        /**
         * Retrieves a list of suppliers whose names contain the given string
         * (case-insensitive).
         *
         * @param name The string to search for in supplier names.
         * @return A {@link List} of {@link SupplierResponse} objects matching the
         *         search criteria.
         * @throws EntityNotFoundException If no suppliers are found matching the given
         *                                 name.
         */
        if (supplierRepository.findByNameIgnoreCaseContaining(name).isEmpty()) {
            throw new EntityNotFoundException("No suppliers found");
        }
        List<Supplier> suppliers = supplierRepository.findByNameIgnoreCaseContaining(name);
        return SupplierResponse.fromEntityList(suppliers);
    }

    /**
     * Registers a new primary supplier account.
     * This involves creating a new {@link Supplier} entity and associating it with
     * a new {@link User}
     * who will have the "ROLE_SUPPLIER_PRIMARY" role. An email confirmation is sent
     * to the supplier.
     *
     * @param request The {@link SupplierRegisterRequest} containing details for the
     *                supplier and the primary user.
     * @return A {@link SupplierResponse} object representing the newly registered
     *         supplier.
     * @throws EntityAlreadyExistsException If a supplier with the same email or
     *                                      CNPJ already exists.
     * @throws EntityNotFoundException      If the "ROLE_SUPPLIER_PRIMARY" role is
     *                                      not found.
     * @throws AccessDeniedException        If there's an issue with user
     *                                      authentication during the process.
     */
    public SupplierResponse registerPrimarySupplier(SupplierRegisterRequest request) {

        existsByEmail(request.supplier().email());
        existsByCnpj(request.supplier().cnpj());

        Role role = roleRepository.findByName("ROLE_SUPPLIER_PRIMARY")
                .orElseThrow(() -> new EntityNotFoundException("Role SUPPLIER_PRIMARY Not Found"));

        Supplier s = new Supplier();
        s.setName(request.supplier().name());
        s.setCnpj(request.supplier().cnpj());
        s.setDbUrl(request.supplier().dbUrl());
        s.setEmail(request.supplier().email());
        s.setPhone(request.supplier().phone());
        s.setTier(SupplierTier.NORMAL);
        s.setStatus(AccountStatus.PENDING);
        s.setApproved(false);
        s.setcommissionRate(request.supplier().commissionRate());
        s = supplierRepository.save(s);

        User u = request.user();
        u.setStatus(AccountStatus.PENDING);
        u.addRole(role);
        userService.registerAccount(u, u.getPassword());

        SupplierUser su = new SupplierUser();
        su.setSupplier(s);
        su.setUser(u);
        supplierUserRepository.save(su);

        String token = jwtUtil.generateValidationToken(
                "Supplier",
                s.getId(),
                (3 * 60 * 1000),
                "VALIDATION");

        emailService.sendSupplierEmail(
                "ikommercy.dropshipping@gmail.com",
                "Confirmação de Fornecedor",
                "http://localhost:8080/suppliers/email/confirm-account?token=" + token,
                "Conta de Fornecedor",
                s.getName(),
                s.getEmail(),
                s.getPhone(),
                u.getName(),
                u.getEmail(),
                String.valueOf(s.getId()));

        return SupplierResponse.fromEntity(s);
    }

    /**
     * Confirms a supplier account using a validation token.
     * This activates the supplier's account and the associated primary user's
     * account.
     *
     * @param token The validation token received via email.
     * @throws AccessDeniedException If the token is invalid, the supplier is not
     *                               found,
     *                               or the primary supplier user cannot be
     *                               identified.
     */
    @Transactional
    public void confirmSupplier(String token) {
        jwtUtil.validateValidationToken(token);
        Long supplierId = jwtUtil.getEntityId(token);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new AccessDeniedException("Supplier not found with id: " + supplierId));

        supplier.setStatus(AccountStatus.ACTIVE);
        supplier.setApproved(true);
        supplierRepository.save(supplier);

        SupplierUser primary = supplier.getSupplierUsers().stream()
                .filter(su -> su.getUser().hasRole("ROLE_SUPPLIER_PRIMARY"))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Primary supplier user not found"));

        User user = primary.getUser();
        user.activate();
        userService.saveUser(user);
    }

    /**
     * Updates an existing supplier's information.
     * Only an ADMIN or the PRIMARY user associated with an ACTIVE supplier can
     * perform this action.
     *
     * @param id      The ID of the supplier to update.
     * @param request The {@link SupplierUpdateRequest} containing the updated
     *                supplier information.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to modify the supplier,
     *                                 or if the supplier/user account is not
     *                                 active.
     * @throws EntityNotFoundException If the supplier with the specified ID is not
     *                                 found.
     */
    public void updateSupplier(Long id, SupplierUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current User not found or invalid token");
        }

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with ID " + id + " not found"));

        boolean isPrimarySupplierUserActive = supplier.getStatus().equals(AccountStatus.ACTIVE)
                && supplier.getSupplierUsers().stream()
                        .anyMatch(su -> su.getUser().getId().equals(currentUser.getId())
                                && currentUser.hasRole("ROLE_SUPPLIER_PRIMARY"));

        if (!isPrimarySupplierUserActive && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException(String.format(
                    "User '%s' cannot update supplier '%s' unless they are an ADMIN or the PRIMARY Supplier, also the user or the supplier must have an Active account.",
                    currentUser.getName(),
                    supplier.getName()));
        }

        existsByEmail(request.email());
        existsByCnpj(request.cnpj());

        supplierMapper.updateSupplierFromDto(request, supplier);

        supplierRepository.save(supplier);
    }

    /**
     * Associates one or more existing users with a supplier.
     * The associated users will be granted the "ROLE_SUPPLIER" role.
     * Only an ADMIN or the PRIMARY user associated with an ACTIVE supplier can
     * perform this action.
     *
     * @param supplierId The ID of the supplier to associate users with.
     * @param userIds    A {@link List} of user IDs to associate with the supplier.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to add users to the supplier,
     *                                 or if the supplier/user account is not
     *                                 active.
     * @throws EntityNotFoundException If the supplier, any of the users, or the
     *                                 "ROLE_SUPPLIER" role is not found.
     */
    @Transactional
    public void registerUserToSupplier(Long supplierId, List<Long> userIds) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current User not found or invalid token");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with ID " + supplierId + " not found"));

        boolean isPrimarySupplierUserActive = supplier.getStatus().equals(AccountStatus.ACTIVE)
                && supplier.getSupplierUsers().stream()
                        .anyMatch(su -> su.getUser().getId().equals(currentUser.getId())
                                && currentUser.hasRole("ROLE_SUPPLIER_PRIMARY"));

        if (!isPrimarySupplierUserActive && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException(String.format(
                    "User '%s' cannot add users to supplier '%s' unless they are an ADMIN or the PRIMARY Supplier, also the user or the supplier must have an Active account.",
                    currentUser.getName(),
                    supplier.getName()));
        }

        List<User> users = userService.getAllUserById(userIds);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No valid users found for the provided IDs");
        }

        Role supplierRole = roleRepository.findByName("ROLE_SUPPLIER")
                .orElseThrow(() -> new EntityNotFoundException("Role SUPPLIER not found"));

        for (User user : users) {
            if (!user.hasRole("ROLE_SUPPLIER")) {
                user.addRole(supplierRole);
                userService.saveUser(user);
            }

            boolean alreadyAssociated = supplier.getSupplierUsers().stream()
                    .anyMatch(su -> su.getUser().getId().equals(user.getId()));

            if (!alreadyAssociated) {
                SupplierUser su = new SupplierUser();
                su.setSupplier(supplier);
                su.setUser(user);
                supplier.getSupplierUsers().add(su);
                supplierUserRepository.save(su);
            }
        }

        supplierRepository.save(supplier);
    }

    /**
     * Soft deletes a supplier account by setting its status to DELETED and
     * recording the deletion timestamp.
     * Only an ADMIN or the PRIMARY user associated with an ACTIVE supplier can
     * perform this action.
     *
     * @param id The ID of the supplier to delete.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to delete the supplier,
     *                                 or if the supplier/user account is not
     *                                 active.
     * @throws EntityNotFoundException If the supplier with the specified ID is not
     *                                 found.
     */
    public void softDelete(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current User not found or invalid token");
        }

        Supplier s = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with ID: {" + id + "} NOT FOUND"));

        boolean isPrimarySupplierUserActive = s.getStatus().equals(AccountStatus.ACTIVE)
                && s.getSupplierUsers().stream()
                        .anyMatch(su -> su.getUser().getId().equals(currentUser.getId())
                                && currentUser.hasRole("ROLE_SUPPLIER_PRIMARY"));

        if (!isPrimarySupplierUserActive && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException(String.format(
                    "User '%s' cannot delete supplier '%s' unless they are an ADMIN or the PRIMARY Supplier, also the user or the supplier must have an Active account.",
                    currentUser.getName(),
                    s.getName()));
        }

        s.setStatus(AccountStatus.DELETED);
        s.setDeletedAt(LocalDateTime.now());
        supplierRepository.save(s);
    }

}
