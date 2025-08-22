package br.dev.kajosama.dropship.security.payloads;

public record TokenPair(
        String accessToken,
        String refreshToken
        ) {

}
