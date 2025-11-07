package br.dev.kajosama.dropship.api.payloads.requests;

import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

public record StatusUpdateRequest(AccountStatus status) {

}
