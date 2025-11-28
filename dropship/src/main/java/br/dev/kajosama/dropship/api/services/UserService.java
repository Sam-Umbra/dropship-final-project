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
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.repositories.UserRepository;
import br.dev.kajosama.dropship.domain.validators.PhoneValidator;
import br.dev.kajosama.dropship.security.entities.Role;
import br.dev.kajosama.dropship.security.jwt.JwtTokenUtil;
import br.dev.kajosama.dropship.security.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link User} entities.
 *              Provides business logic for user-related operations such as
 *              registration,
 *              account updates, deletion, status changes, and authentication.
 *              It interacts with {@link UserRepository},
 *              {@link RoleRepository},
 *              {@link PasswordEncoder}, {@link UserMapper},
 *              {@link JwtTokenUtil}, and {@link EmailService}.
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

    /**
     * Loads a user by their username (email) for authentication purposes.
     *
     * @param username The email address of the user.
     * @return A {@link UserDetails} object representing the authenticated user.
     * @throws UsernameNotFoundException If no user with the given username is
     *                                   found.
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.getUserRoles().size();

        return user;
    }

    /**
     * Checks if a user exists by their ID.
     *
     * @param id The ID of the user to check.
     * @return True if a user with the specified ID exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return An {@link Optional} containing the found {@link User} if it exists,
     *         or an empty {@link Optional} if no user with the given ID is found.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return A {@link List} of all {@link User} entities.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a list of users by their IDs.
     *
     * @param ids A {@link List} of user IDs to retrieve.
     * @return A {@link List} of {@link User} entities matching the provided IDs.
     */
    public List<User> getAllUserById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Registers a new user account.
     *
     * @param user        The {@link User} object containing the user's details.
     * @param rawPassword The raw password for the new user.
     * @return The newly registered {@link User} entity.
     * @throws EntityAlreadyExistsException If a user with the same email or CPF
     *                                      already exists.
     * @throws EntityNotFoundException      If the default "ROLE_USER" is not found.
     */
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
                "VALIDATION");

        emailService.sendEmailWithConfirmationButton(
                savedUser.getEmail(),
                "Confirmação de Conta",
                "http://localhost:8080/user/email/confirm-account?token=" + token,
                "Conta da Loja");

        return savedUser;
    }

    /**
     * Updates an existing user account.
     *
     * @param id      The ID of the user to update.
     * @param request The {@link AccountUpdateRequest} containing the updated user
     *                information.
     * @throws EntityNotFoundException      If the user with the specified ID is not
     *                                      found.
     * @throws AccessDeniedException        If the current user does not have
     *                                      permission to modify the account.
     * @throws EntityAlreadyExistsException If the updated email already exists for
     *                                      another user.
     * @throws IllegalArgumentException     If the provided phone number is invalid.
     */
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

    /**
     * Soft deletes a user account by setting its status to DELETED and recording
     * the deletion timestamp.
     *
     * @param id The ID of the user to delete.
     * @throws EntityNotFoundException If the user with the specified ID is not
     *                                 found.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to delete the account.
     */
    public void deleteAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        checkOwnershipOrAdmin(user);
        checkAccountNotDeleted(user);

        // A invalidação de token agora é feita pelo AuthService no
        // logout/changePassword
        LocalDateTime now = LocalDateTime.now();

        userRepository.softDelete(id, AccountStatus.DELETED, now, now);

        userRepository.updateLastExit(id);
    }

    /**
     * Updates the status of a user account.
     *
     * @param id     The ID of the user whose status is to be updated.
     * @param status The {@link StatusUpdateRequest} containing the new status.
     * @throws EntityNotFoundException If the user with the specified ID is not
     *                                 found.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to modify the account.
     */
    public void updateStatus(Long id, StatusUpdateRequest status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));

        checkOwnershipOrAdmin(user);
        checkAccountNotDeleted(user);

        userRepository.updateStatus(status.status(), id);
    }

    /**
     * Retrieves the currently authenticated user from the Spring Security context.
     *
     * @return The {@link User} object of the current authenticated user.
     * @throws AccessDeniedException If no user is authenticated or the token is
     *                               invalid.
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("User not found or invalid token");
        }
        return currentUser;
    }

    /**
     * Checks if the current authenticated user has permission to modify a target
     * user's account.
     * Permission is granted if the current user is the owner of the account or has
     * an "ADMIN" role.
     *
     * @param userToModify The {@link User} account that is intended to be modified.
     * @throws AccessDeniedException If the current user does not have the necessary
     *                               permissions.
     */
    private void checkOwnershipOrAdmin(User userToModify) {
        User currentUser = getCurrentUser();
        boolean isOwner = currentUser.getId().equals(userToModify.getId());
        boolean isAdmin = currentUser.hasRole("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You can only modify your own account, unless you're an ADMIN");
        }
    }

    /**
     * Checks if a user account is marked as deleted.
     * If the account is deleted and the current user is not an "ADMIN", an
     * {@link AccountDeletedException} is thrown.
     *
     * @param user The {@link User} account to check.
     * @throws AccountDeletedException If the account is deleted and the current
     *                                 user is not an admin.
     */
    private void checkAccountNotDeleted(User user) {
        User currentUser = getCurrentUser();
        if (user.isAccountDeleted() && !currentUser.hasRole("ADMIN")) {
            throw new AccountDeletedException("You can't modify a deleted account");
        }
    }

    /**
     * Confirms a user account using a validation token.
     *
     * @param token The validation token received via email.
     * @throws AccessDeniedException If the token is invalid or the user associated
     *                               with the token is not found.
     */
    public void confirmAccount(String token) {
        jwtUtil.validateValidationToken(token);
        Long userId = jwtUtil.getEntityId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("Account not found with email: " + userId));
        user.activate();
        userRepository.save(user);
    }
}
