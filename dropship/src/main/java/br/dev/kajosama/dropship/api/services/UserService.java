/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package br.dev.kajosama.dropship.api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework..core.userdetails.UsernameNotFoundException;
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
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.repositories.UserRepository;
import br.dev.kajosama.dropship.domain.validators.PhoneValidator;
import br.dev.kajosama.dropship.security.entities.Role;
import br.dev.kajosama.dropship.security.jwt.JwtTokenUtil;
import br.dev.kajosama.dropship.security.repositories.RoleRepository;
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
    JwtTokenUtil jwtUtil;

    @Autowired
    EmailService emailService;

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

    public List<User> getAllUserById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
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

        user.setStatus(AccountStatus.PENDING);

        User savedUser = saveUser(user);

        String token = jwtUtil.generateValidationToken(
                "User",
                savedUser.getId(),
                (3 * 60 * 1000),
                "VALIDATION"
        );

        emailService.sendEmailWithConfirmationButton(
                savedUser.getEmail(),
                "Confirmação de Conta",
                "http://localhost:8080/user/email/confirm-account?token=" + token,
                "Conta da Loja"
        );

        return savedUser;
    }

    public void updateAccount(Long id, AccountUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        checkOwnershipOrAdmin(user);
        checkAccountNotDeleted(user);

        if (request.email() != null) {
            boolean emailExists = userRepository.existsByEmailAndIdNot(request.email(), id);
            if (emailExists) {
                throw new EntityAlreadyExistsException("User", "email", request.email());
            }
        }

        userMapper.updateUserFromDto(request, user);

        Optional.ofNullable(request.phone())
                .filter(p -> !p.isBlank())
                .ifPresent(p -> {
                    String phoneWithDDI = p.startsWith("+55") ? p : "+55" + p;
                    try {
                        String normalizedPhone = PhoneValidator.normalizeToE164(phoneWithDDI, "BR");
                        user.setPhone(normalizedPhone);
                    } catch (NumberParseException e) {
                        throw new IllegalArgumentException("Invalid phone number: " + p, e);
                    }
                });

        userRepository.save(user);
    }

    public void deleteAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        checkOwnershipOrAdmin(user);
        checkAccountNotDeleted(user);

        // A invalidação de token agora é feita pelo AuthService no logout/changePassword
        LocalDateTime now = LocalDateTime.now();

        userRepository.softDelete(id, AccountStatus.DELETED, now, now);

        userRepository.updateLastExit(id);
    }

    public void updateStatus(Long id, StatusUpdateRequest status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        checkOwnershipOrAdmin(user);
        checkAccountNotDeleted(user);

        userRepository.updateStatus(status.status(), id);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("User not found or invalid token");
        }
        return currentUser;
    }

    private void checkOwnershipOrAdmin(User userToModify) {
        User currentUser = getCurrentUser();
        boolean isOwner = currentUser.getId().equals(userToModify.getId());
        boolean isAdmin = currentUser.hasRole("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You can only modify your own account, unless you're an ADMIN");
        }
    }

    private void checkAccountNotDeleted(User user) {
        User currentUser = getCurrentUser();
        if (user.isAccountDeleted() && !currentUser.hasRole("ADMIN")) {
            throw new AccountDeletedException("You can't modify a deleted account");
        }
    }

    public void confirmAccount(String token) {
        jwtUtil.validateValidationToken(token);
        Long userId = jwtUtil.getEntityId(token);

        User user = userRepository.findById(userId).orElseThrow(() -> new AccessDeniedException("Account not found with email: " + userId));
        user.activate();
        userRepository.save(user);
    }
}
