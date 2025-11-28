/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sam_Umbra
 * @Description Represents a role or authority within the application's security
 *              context.
 *              Each role has a unique name, an optional description, and a
 *              creation timestamp.
 *              Roles are associated with users through the {@link UserRole}
 *              entity.
 */
@Entity
@Table(name = "roles")
public class Role {

    /**
     * The unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    /**
     * The name of the role. Must be unique and not blank.
     */
    @NotBlank
    @Size(max = 45)
    @Column(name = "role_name", nullable = false, unique = true)
    private String name;

    /**
     * A description of what the role entails.
     */
    private String description;

    /**
     * The timestamp when the role was created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * A set of {@link UserRole} entities linking this role to users.
     */
    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Role() {
    }

    /**
     * Constructs a new Role with the specified details.
     *
     * @param id          The unique identifier for the role.
     * @param name        The name of the role.
     * @param description A description of the role.
     * @param createdAt   The timestamp when the role was created.
     */
    public Role(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * Returns the timestamp when the role was created.
     * 
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the role was created.
     * 
     * @param createdAt The creation timestamp to set.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier for the role.
     * 
     * @return The role ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the role.
     * 
     * @param id The role ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the role.
     * 
     * @return The role name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role.
     * 
     * @param name The role name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the role.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the set of {@link UserRole} entities associated with this role.
     * 
     * @return A set of user roles.
     */
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    /**
     * Sets the set of {@link UserRole} entities associated with this role.
     * 
     * @param userRoles The set of user roles to set.
     */
    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
