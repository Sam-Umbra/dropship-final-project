/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents the authentication request payload, containing user credentials for login.
 *
 * @author Sam_Umbra
 * @param email The user's email address. Must be a valid email format and not blank.
 * @param password The user's password. Must be at least 5 characters long and not blank.
 */
public record AuthRequest(
    /** The user's email address. */
    @NotBlank
    @Email
    String email, 
    
    /** The user's raw password. */
    @NotBlank
    @Size(min = 5)
    String password) 
{}