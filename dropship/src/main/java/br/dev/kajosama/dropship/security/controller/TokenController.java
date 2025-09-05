package br.dev.kajosama.dropship.security.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.security.services.TokenService;

@RestController
@RequestMapping("/admin/tokens")
public class TokenController {

    @Autowired
    TokenService tokenService;

    @GetMapping
    public ResponseEntity<Map<String,String>> gettAllTokens() {
        Map<String, String> tokens = tokenService.getAllUserTokens();
        return ResponseEntity.ok(tokens);
    }

}
