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
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Sam_Umbra
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ==================== NON DELETED QUERIES ====================
    /**
     * Busca usuário por email (apenas não deletados)
     */
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    /**
     * Busca usuário por ID (apenas não deletados)
     */
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Lista todos os usuários não deletados
     */
    Page<User> findByDeletedAtIsNull(Pageable pageable);

    /**
     * Busca usuários por CPF (apenas não deletados)
     */
    Optional<User> findByCpfAndDeletedAtIsNull(String cpf);

    // ==================== ALL QUERIES =========================
    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);

    // ==================== EXISTENCE CHECKS ====================
    /**
     * Verifica se email existe (apenas não deletados)
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * Verifica se CPF existe (apenas não deletados)
     */
    boolean existsByCpfAndDeletedAtIsNull(String cpf);

    // ==================== ROLE-BASED QUERIES ====================
    /**
     * Busca usuários por role específica
     */
    @Query("SELECT DISTINCT u FROM User u "
            + "JOIN u.userRoles ur "
            + "JOIN ur.role r "
            + "WHERE r.name = :roleName AND u.deletedAt IS NULL")
    List<User> findByUserRoles_Role_NameAndDeletedAtIsNull(@Param("roleName") String roleName);

    /**
     * Busca usuários com múltiplas roles
     */
    @Query("SELECT DISTINCT u FROM User u "
            + "JOIN u.userRoles ur "
            + "JOIN ur.role r "
            + "WHERE r.name IN :roleNames AND u.deletedAt IS NULL")
    List<User> findByMultipleRoles(@Param("roleNames") List<String> roleNames);

    /**
     * Verifica se usuário tem role específica
     */
    @Query("SELECT COUNT(u) > 0 FROM User u "
            + "JOIN u.userRoles ur "
            + "JOIN ur.role r "
            + "WHERE u.id = :userId AND r.name = :roleName AND u.deletedAt IS NULL")
    boolean userHasRole(@Param("userId") Long userId, @Param("roleName") String roleName);

    // ==================== STATUS-BASED QUERIES ====================
    /**
     * Conta usuários por status
     */
    long countByStatusAndDeletedAtIsNull(AccountStatus status);

    /**
     * Busca usuários por status
     */
    List<User> findByStatusAndDeletedAtIsNull(AccountStatus status);

    /**
     * Busca usuários ativos com email verificado
     */
    @Query("SELECT u FROM User u "
            + "WHERE u.status = 'ACTIVE' "
            + "AND u.emailVerifiedAt IS NOT NULL "
            + "AND u.deletedAt IS NULL")
    List<User> findActiveVerifiedUsers();

    // ==================== DATE-BASED QUERIES ====================
    /**
     * Busca usuários criados em um período
     */
    @Query("SELECT u FROM User u "
            + "WHERE u.createdAt BETWEEN :startDate AND :endDate "
            + "AND u.deletedAt IS NULL")
    List<User> findUsersCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Busca usuários que fizeram login recentemente
     */
    @Query("SELECT u FROM User u "
            + "WHERE u.lastLogin >= :since "
            + "AND u.deletedAt IS NULL "
            + "ORDER BY u.lastLogin DESC")
    List<User> findUsersWithRecentLogin(@Param("since") LocalDateTime since);

    /**
     * Busca usuários inativos (sem login há X tempo)
     */
    @Query("SELECT u FROM User u "
            + "WHERE (u.lastLogin IS NULL OR u.lastLogin < :threshold) "
            + "AND u.deletedAt IS NULL "
            + "AND u.createdAt < :threshold")
    List<User> findInactiveUsers(@Param("threshold") LocalDateTime threshold);

    // ==================== EMAIL VERIFICATION ====================
    /**
     * Busca usuários com email não verificado
     */
    @Query("SELECT u FROM User u "
            + "WHERE u.emailVerifiedAt IS NULL "
            + "AND u.deletedAt IS NULL")
    List<User> findUsersWithUnverifiedEmail();

    /**
     * Busca usuários com email verificado em um período
     */
    @Query("SELECT u FROM User u "
            + "WHERE u.emailVerifiedAt BETWEEN :startDate AND :endDate "
            + "AND u.deletedAt IS NULL")
    List<User> findUsersVerifiedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ==================== SEARCH QUERIES ====================
    /**
     * Busca usuários por nome (busca parcial, case insensitive)
     */
    @Query("SELECT u FROM User u "
            + "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "AND u.deletedAt IS NULL")
    Page<User> findByNameContainingIgnoreCase(
            @Param("name") String name,
            Pageable pageable
    );

    /**
     * Busca avançada de usuários
     */
    @Query("SELECT u FROM User u "
            + "WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) "
            + "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) "
            + "AND (:status IS NULL OR u.status = :status) "
            + "AND u.deletedAt IS NULL")
    Page<User> findWithFilters(
            @Param("name") String name,
            @Param("email") String email,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    // ==================== STATISTICS QUERIES ====================
    /**
     * Conta novos usuários por mês
     */
    @Query("SELECT YEAR(u.createdAt) as year, MONTH(u.createdAt) as month, COUNT(u) as count "
            + "FROM User u "
            + "WHERE u.deletedAt IS NULL "
            + "AND u.createdAt >= :startDate "
            + "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt) "
            + "ORDER BY year DESC, month DESC")
    List<Object[]> countNewUsersByMonth(@Param("startDate") LocalDateTime startDate);

    /**
     * Estatísticas de usuários por status
     */
    @Query("SELECT u.status, COUNT(u) "
            + "FROM User u "
            + "WHERE u.deletedAt IS NULL "
            + "GROUP BY u.status")
    List<Object[]> getUserStatsByStatus();

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
