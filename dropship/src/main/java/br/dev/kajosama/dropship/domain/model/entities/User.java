/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.dev.kajosama.dropship.domain.interfaces.Auditable;
import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.security.entities.Role;
import br.dev.kajosama.dropship.security.entities.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

/**
 * @author Sam_umbra
 * @Description
 * Represents a user in the system.
 * This entity stores user details, authentication information, and associations with roles and orders.
 */
@Entity
@Auditable
@Table(name = "users")
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * The full name of the user.
     * Must be between 1 and 150 characters long.
     */
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * The email address of the user.
     * Must be a valid email format, unique, and up to 255 characters long.
     */
    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    /**
     * The hashed password of the user.
     * Must be between 5 and 64 characters long.
     */
    @NotBlank
    @Size(max = 64, min = 5)
    @Column(nullable = false, length = 64)
    private String password;

    /**
     * The timestamp when the user account was created.
     * Automatically set upon creation and cannot be updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the user account was last updated.
     * Automatically updated on entity modification.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * The timestamp when the user account was logically deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * The CPF (Brazilian individual taxpayer registry identification) of the user.
     * Must be a valid CPF, not null, and unique.
     */
    @CPF
    @NotNull
    @Size(max = 11, min = 11)
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    /**
     * The phone number of the user.
     * Must be a valid phone number and between 10 and 16 characters long.
     */
    @NotBlank
    @Size(max = 16, min = 10)
    @Column(nullable = false, length = 16)
    @ValidPhone
    private String phone;

    /**
     * The current status of the user's account (e.g., ACTIVE, SUSPENDED, DELETED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    /**
     * The birth date of the user.
     * Must not be null and must be a date in the past.
     */
    @NotNull
    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * The timestamp when the user's email was verified.
     */
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    /**
     * The timestamp of the user's last login.
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * The timestamp of the user's last logout or session end.
     */
    @Column(name = "last_exit")
    private LocalDateTime lastExit;

    /**
     * A set of roles assigned to this user.
     * This is a one-to-many relationship with the UserRole entity.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * A list of orders placed by this user.
     * This is a one-to-many relationship with the Order entity.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    // ==================== SPRING SECURITY METHODS ====================
    /**
     * Returns the authorities granted to the user.
     *
     * @return A collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName().toUpperCase()));
        }
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the raw password for the user.
     * Note: In a real application, this should be a hashed password.
     *
     * @param rawPassword The raw password to set.
     */
    public void setPassword(String rawPassword) {
        this.password = rawPassword;
    }

    /**
     * Returns the username used to authenticate the user.
     * In this application, the email is used as the username.
     *
     * @return The user's email address.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return True if the user's account is valid (non-expired), false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return True if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.status != AccountStatus.SUSPENDED;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return True if the user's credentials are valid (non-expired), false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return True if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return this.status == AccountStatus.ACTIVE
                && this.emailVerifiedAt != null;
    }

    // ==================== CONVENIENCE METHODS ====================
    /**
     * Checks if the user has a specific role.
     *
     * @param roleName The name of the role to check (case-insensitive).
     * @return True if the user has the role, false otherwise.
     */
    public boolean hasRole(String roleName) {
        return userRoles.stream()
                .map(userRole -> userRole.getRole().getName().toUpperCase())
                .anyMatch(r -> r.equalsIgnoreCase(roleName) || r.equalsIgnoreCase("ROLE_" + roleName));
    }

    /**
     * Adds a new role to the user.
     *
     * @param role The Role object to add.
     */
    public void addRole(Role role) {
        UserRole userRole = new UserRole(this, role, LocalDateTime.now());
        this.userRoles.add(userRole);
    }

    /**
     * Removes a role from the user.
     *
     * @param roleName The name of the role to remove (case-insensitive).
     */
    public void removeRole(String roleName) {
        userRoles.removeIf(userRole
                -> userRole.getRole().getName().equalsIgnoreCase(roleName));
    }

    /**
     * Checks if the user account is logically deleted.
     *
     * @return True if the account is deleted and has a deletedAt timestamp, false otherwise.
     */
    public boolean isAccountDeleted() {
        return this.status == AccountStatus.DELETED
                && this.deletedAt != null;
    }

    /**
     * Activates the user account and sets the email verification timestamp.
     */
    public void activate() {
        this.status = AccountStatus.ACTIVE;
        this.emailVerifiedAt = LocalDateTime.now();
    }

    /**
     * Suspends the user account.
     */
    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }

    /**
     * Marks the user's email as verified.
     */
    public void verifyEmail() {
        this.emailVerifiedAt = LocalDateTime.now();
    }

    // ==================== CONSTRUCTORS ====================
    /**
     * Default constructor. Initializes createdAt and status.
     */
    public User() {
        this.createdAt = LocalDateTime.now();
        this.status = AccountStatus.ACTIVE;
    }

    /**
     * Constructs a new User with specified details.
     *
     * @param name The full name of the user.
     * @param email The email address of the user.
     * @param password The raw password of the user.
     * @param cpf The CPF of the user.
     * @param phone The phone number of the user.
     * @param birthDate The birth date of the user.
     */
    public User(String name, String email, String password, String cpf, String phone, LocalDate birthDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    // ==================== GETTERS AND SETTERS ====================
    /**
     * Returns the unique identifier for the user.
     *
     * @return The ID of the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the full name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name of the user.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the email address of the user.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the creation timestamp of the user account.
     *
     * @return The LocalDateTime of creation.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the user account.
     *
     * @param createdAt The LocalDateTime to set.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the last update timestamp of the user account.
     *
     * @return The LocalDateTime of last update.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp of the user account.
     *
     * @param updatedAt The LocalDateTime to set.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the deletion timestamp of the user account.
     *
     * @return The LocalDateTime of deletion.
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * Sets the deletion timestamp of the user account.
     *
     * @param deletedAt The LocalDateTime to set.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * Returns the CPF of the user.
     *
     * @return The user's CPF.
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Sets the CPF of the user.
     *
     * @param cpf The CPF to set.
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Returns the phone number of the user.
     *
     * @return The user's phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phone The phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the current status of the user's account.
     *
     * @return The AccountStatus enum.
     */
    public AccountStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the user's account.
     *
     * @param status The AccountStatus enum to set.
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * Returns the birth date of the user.
     *
     * @return The user's birth date.
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date of the user.
     *
     * @param birthDate The birth date to set.
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Returns the timestamp when the user's email was verified.
     *
     * @return The LocalDateTime of email verification.
     */
    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    /**
     * Sets the timestamp when the user's email was verified.
     *
     * @param emailVerifiedAt The LocalDateTime to set.
     */
    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    /**
     * Returns the timestamp of the user's last login.
     *
     * @return The LocalDateTime of last login.
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    /**
     * Sets the timestamp of the user's last login.
     *
     * @param lastLogin The LocalDateTime to set.
     */
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Returns the timestamp of the user's last logout or session end.
     *
     * @return The LocalDateTime of last exit.
     */
    public LocalDateTime getLastExit() {
        return lastExit;
    }

    /**
     * Sets the timestamp of the user's last logout or session end.
     *
     * @param lastExit The LocalDateTime to set.
     */
    public void setLastExit(LocalDateTime lastExit) {
        this.lastExit = lastExit;
    }

    /**
     * Returns the set of roles assigned to this user.
     *
     * @return A Set of UserRole objects.
     */
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    /**
     * Sets the set of roles assigned to this user.
     *
     * @param userRoles The Set of UserRole objects to set.
     */
    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    /**
     * Returns the list of orders placed by this user.
     *
     * @return A List of Order objects.
     */
    public List<Order> getOrders() {
        return this.orders;
    }

    /**
     * Sets the list of orders placed by this user.
     *
     * @param orders The List of Order objects to set.
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
