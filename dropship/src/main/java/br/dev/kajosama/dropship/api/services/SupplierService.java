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
import br.dev.kajosama.dropship.security.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;

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

    public boolean existsById(Long id) {
        return supplierRepository.existsById(id);
    }

    public void existsByEmail(String email) {
        if (supplierRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Supplier", "contact email", email);
        }
    }

    public void existsByCnpj(String cnpj) {
        if (supplierRepository.existsByCnpj(cnpj)) {
            throw new EntityAlreadyExistsException("Supplier", "cnpj", cnpj);
        }
    }

    public List<SupplierResponse> findAllSupplier() {
        if (supplierRepository.findAll().isEmpty()) {
            throw new EntityNotFoundException("No suppliers found");
        }
        List<Supplier> suppliers = supplierRepository.findAll();
        return SupplierResponse.fromEntityList(suppliers);
    }

    public SupplierResponse findSupplierbyId(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with ID: {" + id + "} NOT FOUND"));
        return SupplierResponse.fromEntity(supplier);
    }

    public List<SupplierResponse> findSuppliersByName(String name) {
        if (supplierRepository.findByNameIgnoreCaseContaining(name).isEmpty()) {
            throw new EntityNotFoundException("No suppliers found");
        }
        List<Supplier> suppliers = supplierRepository.findByNameIgnoreCaseContaining(name);
        return SupplierResponse.fromEntityList(suppliers);
    }

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

        return SupplierResponse.fromEntity(s);

    }

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
