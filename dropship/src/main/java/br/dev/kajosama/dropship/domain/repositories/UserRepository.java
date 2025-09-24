/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package br.dev.kajosama.dropship.domain.repositories;

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
 *
 * @author Sam_Umbra
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("SELECT u FROM User u")
    Page<User> findAllIncludingDeleted(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL")
    Page<User> findDeletedUsers(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?2, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = ?1")
    void updatePassword(Long userId, String newEncodedPassword);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastExit = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastExit(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.deletedAt = CURRENT_TIMESTAMP, u.status = 'DELETED' WHERE u.id = :id")
    void softDeleteById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void updateStatus(@Param("status") AccountStatus status, @Param("id") Long id);
}
