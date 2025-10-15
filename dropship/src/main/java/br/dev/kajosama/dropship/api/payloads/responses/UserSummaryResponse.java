package br.dev.kajosama.dropship.api.payloads.responses;

public record UserSummaryResponse(
        Long id,
        String name,
        String email
        ) {

}
