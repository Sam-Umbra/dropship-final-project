package br.dev.kajosama.dropship.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.requests.AccountUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.requests.StatusUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.responses.SimpleUserResponse;
import br.dev.kajosama.dropship.api.services.UserService;
import br.dev.kajosama.dropship.domain.model.entities.User;
import jakarta.validation.Valid;

/**
 * REST controller for managing user accounts. Provides endpoints for user
 * registration, account updates, confirmation, and deletion.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * Service for handling user-related business logic.
     */
    @Autowired
    UserService userService;

    /**
     * Registers a new user account. The password from the request body is
     * extracted and passed separately for encoding.
     *
     * @param user The {@link User} object to be created, containing details
     * like name, email, and raw password.
     * @return A {@link SimpleUserResponse} object representing the newly
     * created user, excluding sensitive information.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleUserResponse registerAccount(@Valid @RequestBody User user) {

        String rawPassword = user.getPassword();

        userService.registerAccount(user, rawPassword);
        return SimpleUserResponse.fromEntity(user);
    }

    /**
     * Updates an existing user's account information.
     *
     * @param id The ID of the user to update.
     * @param request The request payload containing the fields to be updated.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateRequest request) {

        userService.updateAccount(id, request);

        return ResponseEntity.noContent().build();
    }

    /**
     * Confirms a user's account via an email verification token.
     *
     * @param token The verification token sent to the user's email.
     * @return A {@link ResponseEntity} with no content (204) upon successful
     * confirmation.
     */
    @GetMapping("/email/confirm-account")
    public ResponseEntity<Void> confirmAccount(@RequestParam("token") String token) {
        userService.confirmAccount(token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a user account by its ID.
     *
     * @param id The ID of the user to delete.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        userService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the status of a user account (e.g., activate, deactivate). This
     * is typically an administrative action.
     *
     * @param id The ID of the user whose status is to be updated.
     * @param status The request payload containing the new status.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @PutMapping("/status/{id}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody @Valid StatusUpdateRequest status) {
        userService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

}
