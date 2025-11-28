/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.payloads;

/**
 * Represents the response payload after a successful authentication.
 *
 * @author Sam_Umbra
 * @param email The email of the authenticated user.
 * @param accessToken The JWT access token for authorizing subsequent requests.
 * @param refreshToken The JWT refresh token used to obtain a new access token.
 */
public record AuthResponse(
        /**
         * The email of the authenticated user.
         */
        String email,
        /**
         * The JWT access token for authorizing subsequent requests.
         */
        String accessToken,
        /**
         * The JWT refresh token used to obtain a new access token.
         */
        String refreshToken) {

}
