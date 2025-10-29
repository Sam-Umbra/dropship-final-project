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
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.requests.SupplierRegisterRequest;
import br.dev.kajosama.dropship.api.payloads.requests.SupplierUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.responses.SupplierResponse;
import br.dev.kajosama.dropship.api.services.SupplierService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponse> registerSupplier(@RequestBody @Valid SupplierRegisterRequest request) {
        SupplierResponse response = supplierService.registerPrimarySupplier(request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/others/{supplierId}")
    public ResponseEntity<Void> addSupplier(@PathVariable Long supplierId, @RequestBody List<Long> ids) {
        supplierService.registerUserToSupplier(supplierId, ids);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{supplierId}")
    public ResponseEntity<Void> updateSupplier(@PathVariable Long supplierId, @RequestBody @Valid SupplierUpdateRequest request) {
        supplierService.updateSupplier(supplierId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> findAllSuppliers() {
        List<SupplierResponse> responses = supplierService.findAllSupplier();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<SupplierResponse> findSupplierById(@PathVariable Long id) {
        SupplierResponse response = supplierService.findSupplierbyId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<SupplierResponse>> findSuppliersByName(@PathVariable String name) {
        List<SupplierResponse> responses = supplierService.findSuppliersByName(name);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

}
