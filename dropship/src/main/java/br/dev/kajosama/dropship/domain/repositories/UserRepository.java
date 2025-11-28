/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package br.dev.kajosama.dropship.domain.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

/**
 * Repository interface for managing {@link User} entities.
 * Provides methods for performing CRUD operations and custom queries related to users.
 * @author Sam_Umbra
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
 * Finds a user by their email address.
 *
 * @param email The email address of the user to find.
 * @return An {@link Optional} containing the found {@link User} if it exists,
 * or an empty {@link Optional} if no user with the given email is found.
 */
    Optional<User> findByEmail(String email);

    /**
 * Checks if a user exists with the given email address.
 *
 * @param email The email address to check.
 * @return True if a user with the specified email exists, false otherwise.
 */
    boolean existsByEmail(String email);

    /**
 * Checks if a user exists with the given CPF.
 *
 * @param cpf The CPF to check.
 * @return True if a user with the specified CPF exists, false otherwise.
 */
    boolean existsByCpf(String cpf);

    /**
 * Checks if a user exists with the given email address, excluding a specific user ID.
 * This is useful for validating unique emails during updates.
 *
 * @param email The email address to check.
 * @param id The ID of the user to exclude from the check.
 * @return True if another user with the specified email exists, false otherwise.
 */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
 * Checks if a user exists with the given CPF, excluding a specific user ID.
 * This is useful for validating unique CPFs during updates.
 *
 * @param cpf The CPF to check.
 * @param id The ID of the user to exclude from the check.
 * @return True if another user with the specified CPF exists, false otherwise.
 */
    boolean existsByCpfAndIdNot(String cpf, Long id);

    /**
 * Finds a user by their email address, eagerly fetching their associated roles.
 *
 * @param email The email address of the user to find.
 * @return An {@link Optional} containing the found {@link User} with roles,
 * or an empty {@link Optional} if no such user is found.
 */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    /**
 * Finds a page of all users, including those marked as deleted.
 *
 * @param pageable The pagination information.
 * @return A {@link Page} of all {@link User} entities.
 */
    @Query("SELECT u FROM User u")
    Page<User> findAllIncludingDeleted(Pageable pageable);

    /**
 * Finds a page of users who have been logically deleted.
 *
 * @param pageable The pagination information.
 * @return A {@link Page} of deleted {@link User} entities.
 */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL")
    Page<User> findDeletedUsers(Pageable pageable);

    /**
 * Updates the password for a specific user.
 *
 * @param userId The ID of the user whose password is to be updated.
 * @param newEncodedPassword The new encoded password.
 */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?2, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = ?1")
    void updatePassword(Long userId, String newEncodedPassword);

    /**
 * Updates the last login timestamp for a specific user to the current time.
 *
 * @param userId The ID of the user whose last login is to be updated.
 */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId);

    /**
 * Updates the last exit timestamp for a specific user to the current time.
 *
 * @param userId The ID of the user whose last exit is to be updated.
 */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastExit = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastExit(@Param("userId") Long userId);

    @Modifying
    @Query("""
    UPDATE User u 
       SET u.status = :status, 
           u.deletedAt = :deletedAt,
           u.updatedAt = :updatedAt
     WHERE u.id = :id
    """)
    void softDelete(
            @Param("id") Long id,
            @Param("status") AccountStatus status,
            @Param("deletedAt") LocalDateTime deletedAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    /**
 * Updates the status of a specific user.
 * The {@code updatedAt} timestamp is automatically set to the current time.
 *
 * @param status The new {@link AccountStatus} for the user.
 * @param id The ID of the user whose status is to be updated.
 */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void updateStatus(@Param("status") AccountStatus status, @Param("id") Long id);
}
