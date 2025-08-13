/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.api.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/")
public class TestController {
    
    @GetMapping("manager")
    public Map<String, Object> privateManageEndpoint() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Manager Endpoint: Area Apenas para ADMINS!!!");
        return model;
    }
    
    // Apenas Logados podem ter acesso
    @GetMapping("private")
    public Map<String, Object> privateEndpoint() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Private Endpoint: Area Restrita!");
        return model;
    }

    // Todos podem ter acesso
    @RequestMapping("public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Public Endpoint: Area Publica!");
        return model;
    }
    
}
