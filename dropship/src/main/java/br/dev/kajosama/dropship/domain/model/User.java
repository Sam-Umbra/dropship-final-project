/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.domain.model;

import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.security.entities.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    String password;

    @NotBlank
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotBlank
    @Size(max = 11, min = 11)
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @NotBlank
    @Size(max = 13, min = 13)
    @Column(nullable = false, length = 13)
    private String phone;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @NotBlank
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "last_exit")
    private LocalDateTime lastExit;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // Converte os papéis (roles) do usuário para GrantedAuthority (e.g., ROLE_USER, ROLE_ADMIN)
        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;  // Sua senha
    }
    
    public void setPassword(String password) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        this.password = bcrypt.encode(password);
    }

    @Override
    public String getUsername() {
        return this.email;  // Ou o campo que você usa para o nome de usuário, por exemplo, o email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Lógica de expiração da conta, se necessário
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status != AccountStatus.SUSPENDED;  // Depende de como você gerencia a conta do usuário
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Se você controla expiração de credenciais, defina a lógica aqui
    }

    @Override
    public boolean isEnabled() {
        return this.status == AccountStatus.ACTIVE;  // Depende de como você gerencia o status da conta
    }

    public User() {
    }

    public User(Long id, String name, String email, String password, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, String cpf, String phone, AccountStatus status, LocalDate birthDate, LocalDateTime emailVerifiedAt, LocalDateTime lastLogin, LocalDateTime lastExit) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.cpf = cpf;
        this.phone = phone;
        this.status = status;
        this.birthDate = birthDate;
        this.emailVerifiedAt = emailVerifiedAt;
        this.lastLogin = lastLogin;
        this.lastExit = lastExit;
    }

    public LocalDateTime getLastExit() {
        return lastExit;
    }

    public void setLastExit(LocalDateTime lastExit) {
        this.lastExit = lastExit;
    }

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

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
    
    

}
