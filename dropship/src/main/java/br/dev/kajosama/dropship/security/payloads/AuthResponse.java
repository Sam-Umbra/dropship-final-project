/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.payloads;

/**
 *
 * @author Sam_Umbra
 */
public record AuthResponse(
        String email,
        String accessToken,
        String refreshToken) 
{}
