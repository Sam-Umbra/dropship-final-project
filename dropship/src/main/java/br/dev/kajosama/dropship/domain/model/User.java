/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
 *
 * @author Sam_Umbra
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @NotBlank
    @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String password;

    // Adicionar @CreationTimestamp e @UpdateTimestamp para automação
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CPF
    @NotNull
    @Size(max = 11, min = 11)
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @NotBlank
    @Size(max = 13, min = 13)
    @Column(nullable = false, length = 13)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE; // Valor padrão

    @NotNull
    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "last_exit")
    private LocalDateTime lastExit;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    // ==================== SPRING SECURITY METHODS ====================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName().toUpperCase()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // MÉTODO MELHORADO para setPassword - sem instanciar encoder
    public void setRawPassword(String rawPassword) {
        // Este método será usado pelo service com PasswordEncoder injetado
        // Não codifica aqui - deixa para o service fazer isso
        this.password = rawPassword;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Implementar lógica se tiver data de expiração de conta
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status != AccountStatus.SUSPENDED
                && this.status != AccountStatus.DELETED
                && this.deletedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Implementar se tiver expiração de senha
        // Exemplo: verificar se senha foi alterada há mais de X dias
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == AccountStatus.ACTIVE
                && this.emailVerifiedAt != null; // Só ativo se email verificado
    }

    // ==================== MÉTODOS DE CONVENIÊNCIA ====================
    /**
     * Verifica se o usuário tem uma role específica
     */
    public boolean hasRole(String roleName) {
        return userRoles.stream()
                .anyMatch(userRole -> userRole.getRole().getName().equalsIgnoreCase("ROLE_" + roleName));
    }

    /**
     * Adiciona uma nova role ao usuário
     */
    public void addRole(Role role) {
        UserRole userRole = new UserRole(this, role, LocalDateTime.now());
        this.userRoles.add(userRole);
    }

    /**
     * Remove uma role do usuário
     */
    public void removeRole(String roleName) {
        userRoles.removeIf(userRole
                -> userRole.getRole().getName().equalsIgnoreCase(roleName));
    }

    /**
     * Marca a conta como deletada (soft delete)
     */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
        this.status = AccountStatus.DELETED;
    }

    /**
     * Ativa a conta
     */
    public void activate() {
        this.status = AccountStatus.ACTIVE;
        this.deletedAt = null;
    }

    /**
     * Suspende a conta
     */
    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }

    /**
     * Marca email como verificado
     */
    public void verifyEmail() {
        this.emailVerifiedAt = LocalDateTime.now();
    }

    /**
     * Atualiza último login
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Atualiza último logout
     */
    public void updateLastExit() {
        this.lastExit = LocalDateTime.now();
    }

    // ==================== CONSTRUTORES ====================
    public User() {
        this.createdAt = LocalDateTime.now();
        this.status = AccountStatus.ACTIVE;
    }

    public User(String name, String email, String cpf, String phone, LocalDate birthDate) {
        this();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    // ==================== GETTERS E SETTERS ====================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getLastExit() {
        return lastExit;
    }

    public void setLastExit(LocalDateTime lastExit) {
        this.lastExit = lastExit;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
