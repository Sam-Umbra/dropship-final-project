package br.dev.kajosama.dropship.api.payloads.requests;

import br.dev.kajosama.dropship.domain.model.entities.User;

public record SupplierRegisterRequest(
        SupplierRequest supplier,
        User user
) {}
