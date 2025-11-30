package br.dev.kajosama.dropship.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.requests.SupplierRegisterRequest;
import br.dev.kajosama.dropship.api.payloads.requests.SupplierUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.responses.SupplierResponse;
import br.dev.kajosama.dropship.api.services.SupplierService;
import jakarta.validation.Valid;

/**
 * REST controller for managing suppliers. Provides endpoints for registering,
 * updating, finding, and deleting suppliers, as well as managing user
 * associations with suppliers.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    /**
     * Service for handling supplier-related business logic.
     */
    @Autowired
    private SupplierService supplierService;

    /**
     * Registers a new primary supplier account. This creates a new supplier and
     * associates the currently authenticated user as its primary contact.
     *
     * @param request The request payload containing the supplier's registration
     * details.
     * @return A {@link ResponseEntity} containing the {@link SupplierResponse}
     * of the newly created supplier.
     */
    @PostMapping
    public ResponseEntity<SupplierResponse> registerSupplier(@RequestBody @Valid SupplierRegisterRequest request) {
        SupplierResponse response = supplierService.registerPrimarySupplier(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Adds one or more existing users to a supplier's team. This is used to
     * grant other users access to manage a supplier's account.
     *
     * @param supplierId The ID of the supplier to which users will be added.
     * @param ids A list of user IDs to associate with the supplier.
     * @return A {@link ResponseEntity} with an Accepted (202) status.
     */
    @PatchMapping("/others/{supplierId}")
    public ResponseEntity<Void> addSupplier(@PathVariable Long supplierId, @RequestBody List<Long> ids) {
        supplierService.registerUserToSupplier(supplierId, ids);
        return ResponseEntity.accepted().build();
    }

    /**
     * Updates the details of an existing supplier.
     *
     * @param supplierId The ID of the supplier to update.
     * @param request The request payload with the updated information.
     * @return A {@link ResponseEntity} with an OK (200) status.
     */
    @PatchMapping("/{supplierId}")
    public ResponseEntity<Void> updateSupplier(@PathVariable Long supplierId, @RequestBody @Valid SupplierUpdateRequest request) {
        supplierService.updateSupplier(supplierId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a list of all suppliers.
     *
     * @return A {@link ResponseEntity} containing a list of
     * {@link SupplierResponse} objects.
     */
    @GetMapping
    public ResponseEntity<List<SupplierResponse>> findAllSuppliers() {
        List<SupplierResponse> responses = supplierService.findAllSupplier();
        return ResponseEntity.ok(responses);
    }

    /**
     * Finds a single supplier by its ID.
     *
     * @param id The ID of the supplier to retrieve.
     * @return A {@link ResponseEntity} containing the {@link SupplierResponse}
     * for the found supplier.
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<SupplierResponse> findSupplierById(@PathVariable Long id) {
        SupplierResponse response = supplierService.findSupplierbyId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Finds suppliers by their name.
     *
     * @param name The name of the supplier(s) to search for.
     * @return A {@link ResponseEntity} containing a list of matching
     * {@link SupplierResponse} objects.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<SupplierResponse>> findSuppliersByName(@PathVariable String name) {
        List<SupplierResponse> responses = supplierService.findSuppliersByName(name);
        return ResponseEntity.ok(responses);
    }

    /**
     * Confirms a supplier's account using a verification token sent via email.
     *
     * @param token The verification token.
     * @return A {@link ResponseEntity} with no content (204) upon successful
     * confirmation.
     */
    @GetMapping("/email/confirm-account")
    public ResponseEntity<Void> confirmSupplier(@RequestParam("token") String token) {
        supplierService.confirmSupplier(token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Soft deletes a supplier by their ID. This marks the supplier as inactive
     * rather than physically removing them from the database.
     *
     * @param id The ID of the supplier to delete.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * TODO: Implementar Endpoint de alteração de commissionRate via EMAIL
     */
}
