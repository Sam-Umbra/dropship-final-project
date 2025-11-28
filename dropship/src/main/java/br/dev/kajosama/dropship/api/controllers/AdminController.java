package br.dev.kajosama.dropship.api.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.responses.ComplexUserResponse;
import br.dev.kajosama.dropship.api.payloads.responses.SimpleUserResponse;
import br.dev.kajosama.dropship.api.services.AuditLogService;
import br.dev.kajosama.dropship.api.services.UserService;
import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import br.dev.kajosama.dropship.security.services.TokenService;

/**
 * Controller for administrative operations. Provides endpoints for managing
 * users, viewing tokens, and auditing system logs. Access to these endpoints is
 * restricted to users with administrative privileges.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    //----------------Tokens----------------//
    /**
     * Service for handling business logic related to authentication tokens.
     */
    @Autowired
    private TokenService tokenService;

    /**
     * Retrieves all active user tokens from the system.
     *
     * @return A ResponseEntity containing a map of all user tokens.
     */
    @GetMapping("/tokens")
    public ResponseEntity<Map<String, String>> getAllTokens() {
        Map<String, String> tokens = tokenService.getAllUserTokens();
        return ResponseEntity.ok(tokens);
    }

    //----------------Users----------------//
    /**
     * Service for handling user-related business logic.
     */
    @Autowired
    private UserService userService;

    /**
     * Retrieves a simplified list of all users.
     *
     * @return A list of {@link SimpleUserResponse} objects.
     */
    @GetMapping("/user/simple")
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleUserResponse> getAllSimpleUsers() {
        return userService.getAllUsers()
                .stream()
                .map(SimpleUserResponse::fromEntity)
                .toList();
    }

    /**
     * Retrieves a detailed list of all users.
     *
     * @return A list of {@link ComplexUserResponse} objects.
     */
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public List<ComplexUserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(ComplexUserResponse::fromEntity)
                .toList();
    }

    /**
     * Finds a specific user by their ID and returns detailed information.
     *
     * @param id The ID of the user to retrieve.
     * @return A {@link ResponseEntity} with the {@link ComplexUserResponse} if
     * found, or a 404 Not Found response.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<ComplexUserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ComplexUserResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //----------------Audit Logs------------//
    /**
     * Service for handling audit log business logic.
     */
    @Autowired
    private AuditLogService auditService;

    /**
     * Retrieves all audit logs from the system.
     *
     * @return A ResponseEntity containing a list of all {@link AuditLog}
     * objects.
     */
    @GetMapping("/audits")
    public ResponseEntity<List<AuditLog>> findAllAuditLogs() {
        return ResponseEntity.ok(auditService.findAll());
    }

    /**
     * Finds a specific audit log by its ID.
     *
     * @param id The ID of the audit log to retrieve.
     * @return A ResponseEntity containing the found {@link AuditLog}.
     */
    @GetMapping("/audits/{id}")
    public ResponseEntity<AuditLog> findById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.findById(id));
    }

    /**
     * Finds all audit logs related to a specific action type.
     *
     * @param actionType The type of action (e.g., CREATED, UPDATED).
     * @return A ResponseEntity containing a list of matching {@link AuditLog}
     * objects.
     */
    @GetMapping("/audits/action-type/{actionType}")
    public ResponseEntity<List<AuditLog>> findByActionType(@PathVariable ActionType actionType) {
        return ResponseEntity.ok(auditService.findByActionType(actionType));
    }

    /**
     * Finds all audit logs for a specific entity and entity ID.
     *
     * @param name The name of the entity (e.g., "User", "Product").
     * @param id The ID of the entity instance.
     * @return A ResponseEntity containing a list of matching {@link AuditLog}
     * objects.
     */
    @GetMapping("/audits/entity/{name}/id/{id}")
    public ResponseEntity<List<AuditLog>> findByEntityNameAndEntityId(@PathVariable String name,
            @PathVariable Long id) {
        return ResponseEntity.ok(auditService.findByEntityNameAndEntityId(name, id));
    }

    /**
     * Finds all audit logs created by a specific user, identified by their
     * email.
     *
     * @param email The email of the user who performed the actions.
     * @return A ResponseEntity containing a list of matching {@link AuditLog}
     * objects.
     */
    @GetMapping("/audits/savedby/{email}")
    public ResponseEntity<List<AuditLog>> findBySavedByEmail(@PathVariable String email) {
        return ResponseEntity.ok(auditService.findBySavedBy(email));
    }

    /**
     * Finds all audit logs created within a specific time interval.
     *
     * @param start The start of the time interval (ISO DATE_TIME format).
     * @param end The end of the time interval (ISO DATE_TIME format).
     * @return A ResponseEntity containing a list of matching {@link AuditLog}
     * objects.
     */
    @GetMapping("/audits/timestamp/start/{start}/end/{end}")
    public ResponseEntity<List<AuditLog>> findByTimeInterval(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.ok(auditService.findByTimeInterval(start, end));
    }
}
