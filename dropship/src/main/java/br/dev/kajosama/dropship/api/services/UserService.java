/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package br.dev.kajosama.dropship.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.domain.model.User;
import br.dev.kajosama.dropship.domain.repositories.UserRepository;
import br.dev.kajosama.dropship.security.entities.Role;
import br.dev.kajosama.dropship.security.repositories.RoleRepository;

/**
 *
 * @author Sam_Umbra
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Força carregamento das roles se estiver usando LAZY
        user.getUserRoles().size();

        return user;
    }

    // ==================== CRUD OPERATIONS ====================

    public User registerAccount(User user, String rawPassword) {
        // Criptografa a senha
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Role Padrão
        Role role = roleRepository.findByName("ROLE_USER")
        .orElseThrow(() -> new RuntimeException("Role USER Not Found"));
        user.addRole(role);

        // Salva o usuário
        User savedUser = userRepository.save(user);

        return userRepository.save(savedUser);
    }

}
