package br.dev.kajosama.dropship.api.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.api.exceptions.EntityAlreadyExistsException;
import br.dev.kajosama.dropship.api.payloads.requests.SupplierRegisterRequest;
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
        if(supplierRepository.findAll().isEmpty()) {
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
        if(supplierRepository.findByNameIgnoreCaseContaining(name).isEmpty()) {
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

        User u = request.user();
        u.setStatus(AccountStatus.PENDING);
        u.addRole(role);
        userService.registerAccount(u, u.getPassword());

        Supplier s = new Supplier();
        s.setName(request.supplier().name());
        s.setCnpj(request.supplier().cnpj());
        s.setDbUrl(request.supplier().dbUrl());
        s.setEmail(request.supplier().email());
        s.setPhone(request.supplier().phone());
        s.setTier(SupplierTier.NORMAL);
        s.setStatus(AccountStatus.PENDING);
        s.setApproved(false);

        s = supplierRepository.save(s);

        SupplierUser su = new SupplierUser();
        su.setSupplier(s);
        su.setUser(u);
        supplierUserRepository.save(su);

        return SupplierResponse.fromEntity(s);

    }

    public void softDelete(Long id) {
        Supplier s = supplierRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Supplier with ID: {" + id + "} NOT FOUND"));
    
        s.setStatus(AccountStatus.DELETED);
        s.setDeletedAt(LocalDateTime.now());
        supplierRepository.save(s);
    }

}
