package br.dev.kajosama.dropship.api.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for testing security configurations and endpoint access levels.
 * Provides public, private (authenticated), and manager (admin-only) endpoints.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/")
public class TestController {

    /**
     * An endpoint accessible only by users with 'MANAGER' or 'ADMIN' roles.
     *
     * @return A map with a sample message indicating access to a manager-level
     * area.
     */
    @GetMapping("manager")
    public Map<String, Object> privateManageEndpoint() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Manager Endpoint: Area Apenas para ADMINS!!!");
        return model;
    }

    /**
     * An endpoint accessible only by authenticated (logged-in) users.
     *
     * @return A map with a sample message indicating access to a restricted
     * area.
     */
    @GetMapping("private")
    public Map<String, Object> privateEndpoint() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Private Endpoint: Area Restrita!");
        return model;
    }

    /**
     * An endpoint accessible by anyone, including unauthenticated users.
     *
     * @return A map with a sample message indicating access to a public area.
     */
    @GetMapping("public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Public Endpoint: Area Publica!");
        return model;
    }

}
