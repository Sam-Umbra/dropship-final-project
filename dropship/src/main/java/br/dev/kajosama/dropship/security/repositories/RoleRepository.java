package br.dev.kajosama.dropship.security.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.dev.kajosama.dropship.security.entities.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // ==================== BASIC QUERIES ====================
    /**
     * Busca role por nome (case insensitive)
     */
    @Query("SELECT r FROM Role r WHERE UPPER(r.name) = UPPER(:name)")
    Optional<Role> findByName(@Param("name") String name);

    /**
     * Busca role por nome exato (case sensitive)
     */
    Optional<Role> findByNameIgnoreCase(String name);

    /**
     * Verifica se existe role com o nome
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Lista todas as roles ordenadas por nome
     */
    @Query("SELECT r FROM Role r ORDER BY r.name ASC")
    List<Role> findAllOrderByName();

    // ==================== SEARCH QUERIES ====================
    /**
     * Busca roles por nome parcial (case insensitive)
     */
    @Query("SELECT r FROM Role r "
            + "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Role> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Busca roles por descrição parcial
     */
    @Query("SELECT r FROM Role r "
            + "WHERE LOWER(r.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<Role> findByDescriptionContainingIgnoreCase(@Param("description") String description);

    /**
     * Busca avançada de roles
     */
    @Query("SELECT r FROM Role r "
            + "WHERE (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) "
            + "AND (:description IS NULL OR LOWER(r.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    Page<Role> findWithFilters(
            @Param("name") String name,
            @Param("description") String description,
            Pageable pageable
    );

    // ==================== USER-ROLE RELATIONSHIP QUERIES ====================
    /**
     * Busca roles de um usuário específico
     */
    @Query("SELECT r FROM Role r "
            + "JOIN r.userRoles ur "
            + "WHERE ur.user.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    /**
     * Busca roles que NÃO estão atribuídas a um usuário
     */
    @Query("SELECT r FROM Role r "
            + "WHERE r.id NOT IN ("
            + "    SELECT ur.role.id FROM UserRole ur WHERE ur.user.id = :userId"
            + ")")
    List<Role> findRolesNotAssignedToUser(@Param("userId") Long userId);

    /**
     * Conta quantos usuários têm uma role específica
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur "
            + "JOIN ur.user u "
            + "WHERE ur.role.id = :roleId AND u.deletedAt IS NULL")
    long countActiveUsersByRole(@Param("roleId") Long roleId);

    /**
     * Busca roles com mais de X usuários
     */
    @Query("SELECT r FROM Role r "
            + "WHERE (SELECT COUNT(ur) FROM UserRole ur "
            + "        JOIN ur.user u "
            + "        WHERE ur.role.id = r.id AND u.deletedAt IS NULL) >= :minUsers")
    List<Role> findRolesWithMinimumUsers(@Param("minUsers") long minUsers);

    /**
     * Busca roles órfãs (sem usuários)
     */
    @Query("SELECT r FROM Role r "
            + "WHERE NOT EXISTS ("
            + "    SELECT ur FROM UserRole ur "
            + "    JOIN ur.user u "
            + "    WHERE ur.role.id = r.id AND u.deletedAt IS NULL"
            + ")")
    List<Role> findOrphanRoles();

    // ==================== DATE-BASED QUERIES ====================
    /**
     * Busca roles criadas em um período
     */
    @Query("SELECT r FROM Role r "
            + "WHERE r.createdAt BETWEEN :startDate AND :endDate "
            + "ORDER BY r.createdAt DESC")
    List<Role> findRolesCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Busca roles criadas após uma data
     */
    @Query("SELECT r FROM Role r "
            + "WHERE r.createdAt >= :date "
            + "ORDER BY r.createdAt DESC")
    List<Role> findRolesCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Busca roles mais antigas
     */
    @Query("SELECT r FROM Role r "
            + "ORDER BY r.createdAt ASC")
    List<Role> findOldestRoles(Pageable pageable);

    /**
     * Busca roles mais recentes
     */
    @Query("SELECT r FROM Role r "
            + "ORDER BY r.createdAt DESC")
    List<Role> findNewestRoles(Pageable pageable);

    // ==================== STATISTICS QUERIES ====================
    /**
     * Conta roles criadas por mês
     */
    @Query("SELECT YEAR(r.createdAt) as year, MONTH(r.createdAt) as month, COUNT(r) as count "
            + "FROM Role r "
            + "WHERE r.createdAt >= :startDate "
            + "GROUP BY YEAR(r.createdAt), MONTH(r.createdAt) "
            + "ORDER BY year DESC, month DESC")
    List<Object[]> countRolesByMonth(@Param("startDate") LocalDateTime startDate);

    /**
     * Estatísticas de uso de roles
     */
    @Query("SELECT r.name, COUNT(ur) as userCount "
            + "FROM Role r "
            + "LEFT JOIN r.userRoles ur "
            + "LEFT JOIN ur.user u ON u.deletedAt IS NULL "
            + "GROUP BY r.id, r.name "
            + "ORDER BY userCount DESC")
    List<Object[]> getRoleUsageStatistics();

    /**
     * Top roles mais utilizadas
     */
    @Query("SELECT r FROM Role r "
            + "LEFT JOIN r.userRoles ur "
            + "LEFT JOIN ur.user u ON u.deletedAt IS NULL "
            + "GROUP BY r.id "
            + "ORDER BY COUNT(ur) DESC")
    List<Role> findMostUsedRoles(Pageable pageable);

    // ==================== ADMIN SPECIFIC QUERIES ====================
    /**
     * Busca role ADMIN (assumindo que existe uma)
     */
    @Query("SELECT r FROM Role r WHERE UPPER(r.name) = 'ADMIN'")
    Optional<Role> findAdminRole();

    /**
     * Busca roles administrativas (contém 'ADMIN' no nome)
     */
    @Query("SELECT r FROM Role r WHERE UPPER(r.name) LIKE '%ADMIN%'")
    List<Role> findAdminRoles();

    /**
     * Busca roles de usuário comum (contém 'USER' no nome)
     */
    @Query("SELECT r FROM Role r WHERE UPPER(r.name) LIKE '%USER%'")
    List<Role> findUserRoles();

    /**
     * Verifica se existe pelo menos um admin no sistema
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur "
            + "JOIN ur.user u "
            + "JOIN ur.role r "
            + "WHERE UPPER(r.name) = 'ADMIN' "
            + "AND u.status = 'ACTIVE' "
            + "AND u.deletedAt IS NULL")
    boolean hasActiveAdmin();

    // ==================== BULK OPERATIONS ====================
    /**
     * Lista roles por lista de IDs
     */
    @Query("SELECT r FROM Role r WHERE r.id IN :roleIds")
    List<Role> findByIdIn(@Param("roleIds") List<Long> roleIds);

    /**
     * Lista roles por lista de nomes
     */
    @Query("SELECT r FROM Role r WHERE UPPER(r.name) IN :roleNames")
    List<Role> findByNameIn(@Param("roleNames") List<String> roleNames);

    // ==================== CUSTOM NAMED QUERIES ====================
    /**
     * Busca roles por padrão de nome usando LIKE
     */
    @Query("SELECT r FROM Role r WHERE r.name LIKE :pattern")
    List<Role> findByNamePattern(@Param("pattern") String pattern);

    /**
     * Busca roles ativas (que têm pelo menos um usuário ativo)
     */
    @Query("SELECT DISTINCT r FROM Role r "
            + "JOIN r.userRoles ur "
            + "JOIN ur.user u "
            + "WHERE u.status = 'ACTIVE' AND u.deletedAt IS NULL")
    List<Role> findActiveRoles();

    /**
     * Busca roles inativas (sem usuários ativos)
     */
    @Query("SELECT r FROM Role r "
            + "WHERE r.id NOT IN ("
            + "    SELECT DISTINCT ur.role.id FROM UserRole ur "
            + "    JOIN ur.user u "
            + "    WHERE u.status = 'ACTIVE' AND u.deletedAt IS NULL"
            + ")")
    List<Role> findInactiveRoles();
}
