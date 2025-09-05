package br.dev.kajosama.dropship.api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.responses.ComplexUserResponse;
import br.dev.kajosama.dropship.api.payloads.responses.SimpleUserResponse;
import br.dev.kajosama.dropship.api.services.UserService;
import br.dev.kajosama.dropship.security.services.TokenService;


@RestController
@RequestMapping("/admin")
public class AdminController {

    //----------------Tokens----------------//
    @Autowired
    TokenService tokenService;

    @GetMapping("/tokens")
    public ResponseEntity<Map<String,String>> gettAllTokens() {
        Map<String, String> tokens = tokenService.getAllUserTokens();
        return ResponseEntity.ok(tokens);
    }

    //----------------Users----------------//
    @Autowired
    UserService userService;

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

}
