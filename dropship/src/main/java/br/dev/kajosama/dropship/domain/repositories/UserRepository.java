/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package br.dev.kajosama.dropship.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.dev.kajosama.dropship.domain.model.User;
import java.util.Optional;

/**
 *
 * @author Sam_Umbra
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ==================== ALL QUERIES =========================
    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);

    // ==================== EXISTENCE CHECKS ====================
    /**
     * Verifica se email existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se CPF existe
     */
    boolean existsByCpf(String cpf);

    // ==================== ROLE-BASED QUERIES ====================
    /**
     * Busca usuários por role específica
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    // ==================== ADMIN QUERIES ====================
    /**
     * Busca todos os usuários (incluindo deletados) - apenas para admins
     */
    @Query("SELECT u FROM User u")
    Page<User> findAllIncludingDeleted(Pageable pageable);

    /**
     * Busca usuários deletados
     */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL")
    Page<User> findDeletedUsers(Pageable pageable);
}
