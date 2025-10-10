/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package br.dev.kajosama.dropship.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.i18n.phonenumbers.NumberParseException;

import br.dev.kajosama.dropship.api.exceptions.AccountDeletedException;
import br.dev.kajosama.dropship.api.exceptions.EntityAlreadyExistsException;
import br.dev.kajosama.dropship.api.mappers.UserMapper;
import br.dev.kajosama.dropship.api.payloads.requests.AccountUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.requests.StatusUpdateRequest;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.repositories.UserRepository;
import br.dev.kajosama.dropship.domain.validators.PhoneValidator;
import br.dev.kajosama.dropship.security.entities.Role;
import br.dev.kajosama.dropship.security.repositories.RoleRepository;
import br.dev.kajosama.dropship.security.services.TokenService;
import jakarta.persistence.EntityNotFoundException;

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

    @Autowired
    UserMapper userMapper;

    @Autowired
    TokenService tokenService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.getUserRoles().size();

        return user;
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerAccount(User user, String rawPassword) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityAlreadyExistsException("User", "email", user.getEmail());
        }
        if (userRepository.existsByCpf(user.getCpf())) {
            throw new EntityAlreadyExistsException("User", "cpf", user.getCpf());
        }

        user.setPassword(passwordEncoder.encode(rawPassword));

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Role USER Not Found"));
        user.addRole(role);

        return userRepository.save(user);
    }

    public void updateAccount(Long id, AccountUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        if (user.isAccountDeleted() && !currentUser.hasRole("ADMIN")) {
            throw new AccountDeletedException("You can't modify a deleted account");
        }
        if (!currentUser.getEmail().equals(user.getEmail()) && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException("You can only modify your account, unless you're an ADMIN");
        }

        if (request.email() != null && userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EntityAlreadyExistsException("User", "email", request.email());
        }
        if (request.cpf() != null && userRepository.existsByCpfAndIdNot(request.cpf(), id)) {
            throw new EntityAlreadyExistsException("User", "cpf", request.cpf());
        }

        userMapper.updateUserFromDto(request, user);

        Optional.ofNullable(request)
                .map(AccountUpdateRequest::phone)
                .filter(p -> !p.isBlank())
                .ifPresent(p -> {
                    try {
                        String normalizedPhone = PhoneValidator.normalizeToE164(p, "BR");
                        user.setPhone(normalizedPhone);
                    } catch (NumberParseException e) {
                        throw new IllegalArgumentException("Invalid phone number:" + p);
                    }
                });

        userRepository.save(user);
    }

    public void deleteAccount(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        if (user.isAccountDeleted()) {
            throw new AccountDeletedException("You can't modify a deleted account");
        }
        if (!currentUser.getEmail().equals(user.getEmail()) && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException("You can only delete your account, unless you're an ADMIN");
        }

        tokenService.invalidateAllUserTokens(id);

        userRepository.softDeleteById(id);
        userRepository.updateLastExit(id);
    }

    public void updateStatus(Long id, StatusUpdateRequest status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        if (!currentUser.getEmail().equals(user.getEmail()) && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException("You can only modify your account, unless you're an ADMIN");
        }
        if (user.isAccountDeleted() && !currentUser.hasRole("ADMIN")) {
            throw new AccountDeletedException("You can't modify a deleted account");
        }

        userRepository.updateStatus(status.status(), id);
    }

}
