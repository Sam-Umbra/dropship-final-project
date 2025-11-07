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



@RestController
@RequestMapping("/admin")
public class AdminController {

    //----------------Tokens----------------//
    @Autowired
    private TokenService tokenService;

    @GetMapping("/tokens")
    public ResponseEntity<Map<String,String>> getAllTokens() {
        Map<String, String> tokens = tokenService.getAllUserTokens();
        return ResponseEntity.ok(tokens);
    }

    //----------------Users----------------//
    @Autowired
    private UserService userService;

    @GetMapping("/user/simple")
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleUserResponse> getAllSimpleUsers() {
        return userService.getAllUsers()
                .stream()
                .map(SimpleUserResponse::fromEntity)
                .toList();
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public List<ComplexUserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(ComplexUserResponse::fromEntity)
                .toList();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ComplexUserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ComplexUserResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //----------------Audit Logs------------//
    @Autowired
    private AuditLogService auditService;

    @GetMapping("/audits")
    public ResponseEntity<List<AuditLog>> findAllAuditLogs() {
        return ResponseEntity.ok(auditService.findAll());
    }

    @GetMapping("/audits/{id}")
    public ResponseEntity<AuditLog> findById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.findById(id));
    }

    @GetMapping("/audits/action-type/{actionType}")
    public ResponseEntity<List<AuditLog>> findByActionType(@PathVariable ActionType actionType) {
        return ResponseEntity.ok(auditService.findByActionType(actionType));
    }

    @GetMapping("/audits/entity/{name}/id/{id}")
    public ResponseEntity<List<AuditLog>> findByEntityNameAndEntityId(@PathVariable String name,
                                                                     @PathVariable Long id) {
        return ResponseEntity.ok(auditService.findByEntityNameAndEntityId(name, id));
    }

    @GetMapping("/audits/savedby/{email}")
    public ResponseEntity<List<AuditLog>> findBySavedByEmail(@PathVariable String email) {
        return ResponseEntity.ok(auditService.findBySavedBy(email));
    }

    @GetMapping("/audits/timestamp/start/{start}/end/{end}")
    public ResponseEntity<List<AuditLog>> findByTimeInterval(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.ok(auditService.findByTimeInterval(start, end));
    }
}
