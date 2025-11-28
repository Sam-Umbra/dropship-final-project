/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.entities;

import java.time.LocalDateTime;

import br.dev.kajosama.dropship.domain.model.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Sam_Umbra
 * @Description Represents the association between a {@link User} and a
 *              {@link Role}.
 *              This entity links a specific user to a specific role, indicating
 *              when the role was assigned.
 */
@Entity
@Table(name = "users_roles")
public class UserRole {

    /**
     * The unique identifier for the user-role association.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long id;

    /**
     * The user associated with this entry.
     * This is a many-to-one relationship with the {@link User} entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The role associated with this entry.
     * This is a many-to-one relationship with the {@link Role} entity.
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * The timestamp when the role was assigned to the user.
     */
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    /**
     * Default constructor required by JPA.
     */
    public UserRole() {
    }

    /**
     * Constructs a new UserRole association with the specified user, role, and
     * assignment timestamp.
     *
     * @param user       The {@link User} to associate.
     * @param role       The {@link Role} to associate.
     * @param assignedAt The timestamp when the role was assigned.
     */
    public UserRole(User user, Role role, LocalDateTime assignedAt) {
        this.user = user;
        this.role = role;
        this.assignedAt = assignedAt;
    }

    /**
     * Returns the user associated with this entry.
     * 
     * @return The {@link User} object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user for this association.
     * 
     * @param user The {@link User} object to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the role associated with this entry.
     * 
     * @return The {@link Role} object.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role for this association.
     * 
     * @param role The {@link Role} object to set.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Returns the timestamp when the role was assigned to the user.
     * 
     * @return The {@link LocalDateTime} of assignment.
     */
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    /**
     * Sets the timestamp when the role was assigned to the user.
     * 
     * @param assignedAt The {@link LocalDateTime} to set.
     */
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    /**
     * Compares this UserRole object with another object for equality.
     * Two UserRole objects are considered equal if their IDs are equal.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRole userRole = (UserRole) o;
        return id != null && id.equals(userRole.id);
    }

    /**
     * Returns the hash code for this UserRole object.
     * The hash code is based on the ID of the user-role association.
     * 
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
