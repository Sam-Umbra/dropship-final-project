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

/**
 * @author Sam_Umbra
 * @Description Repository interface for managing {@link Role} entities.
 *              Provides methods for performing CRUD operations and custom
 *              queries related to roles,
 *              including searching, filtering, and managing user-role
 *              relationships.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

        // ==================== BASIC QUERIES ====================
        /**
         * Finds a role by its name, ignoring case.
         *
         * @param name The name of the role to find.
         * @return An {@link Optional} containing the found {@link Role} if it exists,
         *         or an empty {@link Optional} otherwise.
         */
        @Query("SELECT r FROM Role r WHERE UPPER(r.name) = UPPER(:name)")
        Optional<Role> findByName(@Param("name") String name);

        /**
         * Busca role por nome exato (case sensitive)
         */
        Optional<Role> findByNameIgnoreCase(String name);

        /**
         * Checks if a role with the given name (case-insensitive) exists.
         *
         * @param name The name of the role to check.
         * @return True if a role with the specified name exists, false otherwise.
         */
        boolean existsByNameIgnoreCase(String name);

        /**
         * Retrieves a list of all roles, ordered alphabetically by name.
         *
         * @return A {@link List} of all {@link Role} entities, sorted by name.
         */

        /**
         * Lista todas as roles ordenadas por nome
         */
        @Query("SELECT r FROM Role r ORDER BY r.name ASC")
        List<Role> findAllOrderByName();

        // ==================== SEARCH QUERIES ====================
        /**
         * Finds a page of roles whose names contain the given string, ignoring case.
         *
         * @param name     The string to search for in role names.
         * @param pageable The pagination information.
         * @return A {@link Page} of {@link Role} entities matching the search criteria.
         */
        @Query("SELECT r FROM Role r "
                        + "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
        Page<Role> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

        /**
         * Finds a list of roles whose descriptions contain the given string, ignoring
         * case.
         *
         * @param description The string to search for in role descriptions.
         * @return A {@link List} of {@link Role} entities matching the search criteria.
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
                        Pageable pageable);

        // ==================== USER-ROLE RELATIONSHIP QUERIES ====================
        /**
         * Finds all roles assigned to a specific user.
         *
         * @param userId The ID of the user.
         * @return A {@link List} of {@link Role} entities assigned to the specified
         *         user.
         */
        @Query("SELECT r FROM Role r "
                        + "JOIN r.userRoles ur "
                        + "WHERE ur.user.id = :userId")
        List<Role> findRolesByUserId(@Param("userId") Long userId);

        /**
         * Finds all roles that are not currently assigned to a specific user.
         *
         * @param userId The ID of the user.
         * @return A {@link List} of {@link Role} entities not assigned to the specified
         *         user.
         */
        @Query("SELECT r FROM Role r "
                        + "WHERE r.id NOT IN ("
                        + "    SELECT ur.role.id FROM UserRole ur WHERE ur.user.id = :userId"
                        + ")")
        List<Role> findRolesNotAssignedToUser(@Param("userId") Long userId);

        /**
         * Counts the number of active users who have a specific role.
         *
         * @param roleId The ID of the role.
         * @return The count of active users with the specified role.
         */
        @Query("SELECT COUNT(ur) FROM UserRole ur "
                        + "JOIN ur.user u "
                        + "WHERE ur.role.id = :roleId AND u.deletedAt IS NULL")
        long countActiveUsersByRole(@Param("roleId") Long roleId);

        /**
         * Finds roles that are assigned to at least a minimum number of active users.
         *
         * @param minUsers The minimum number of active users a role must have.
         * @return A {@link List} of {@link Role} entities meeting the criteria.
         */
        @Query("SELECT r FROM Role r "
                        + "WHERE (SELECT COUNT(ur) FROM UserRole ur "
                        + "        JOIN ur.user u "
                        + "        WHERE ur.role.id = r.id AND u.deletedAt IS NULL) >= :minUsers")
        List<Role> findRolesWithMinimumUsers(@Param("minUsers") long minUsers);

        /**
         * Finds roles that are not assigned to any active user (orphan roles).
         *
         * @return A {@link List} of orphan {@link Role} entities.
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
         * Finds roles that were created within a specified date range.
         * Results are ordered by creation date in descending order.
         *
         * @param startDate The start date and time of the range.
         * @param endDate   The end date and time of the range.
         * @return A {@link List} of {@link Role} entities created within the specified
         *         period.
         */
        @Query("SELECT r FROM Role r "
                        + "WHERE r.createdAt BETWEEN :startDate AND :endDate "
                        + "ORDER BY r.createdAt DESC")
        List<Role> findRolesCreatedBetween(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * Finds roles that were created after a specific date.
         * Results are ordered by creation date in descending order.
         *
         * @param date The date and time after which roles should have been created.
         * @return A {@link List} of {@link Role} entities created after the specified
         *         date.
         */
        @Query("SELECT r FROM Role r "
                        + "WHERE r.createdAt >= :date "
                        + "ORDER BY r.createdAt DESC")
        List<Role> findRolesCreatedAfter(@Param("date") LocalDateTime date);

        /**
         * Finds the oldest roles, based on their creation date.
         * The number of roles returned is determined by the {@link Pageable} object.
         *
         * @param pageable The pagination information (e.g., to limit the number of
         *                 results).
         * @return A {@link List} of the oldest {@link Role} entities.
         */
        @Query("SELECT r FROM Role r "
                        + "ORDER BY r.createdAt ASC")
        List<Role> findOldestRoles(Pageable pageable);

        /**
         * Finds the most recently created roles.
         * The number of roles returned is determined by the {@link Pageable} object.
         *
         * @param pageable The pagination information (e.g., to limit the number of
         *                 results).
         * @return A {@link List} of the newest {@link Role} entities.
         */
        @Query("SELECT r FROM Role r "
                        + "ORDER BY r.createdAt DESC")
        List<Role> findNewestRoles(Pageable pageable);

        // ==================== STATISTICS QUERIES ====================
        /**
         * Counts the number of roles created per month, starting from a given date.
         *
         * @param startDate The start date from which to count roles.
         * @return A {@link List} of {@code Object[]} where each array contains the
         *         year, month, and count of roles created.
         */
        @Query("SELECT YEAR(r.createdAt) as year, MONTH(r.createdAt) as month, COUNT(r) as count "
                        + "FROM Role r "
                        + "WHERE r.createdAt >= :startDate "
                        + "GROUP BY YEAR(r.createdAt), MONTH(r.createdAt) "
                        + "ORDER BY year DESC, month DESC")
        List<Object[]> countRolesByMonth(@Param("startDate") LocalDateTime startDate);

        /**
         * Retrieves statistics on how many active users are assigned to each role.
         *
         * @return A {@link List} of {@code Object[]} where each array contains the role
         *         name and the count of active users.
         */
        @Query("SELECT r.name, COUNT(ur) as userCount "
                        + "FROM Role r "
                        + "LEFT JOIN r.userRoles ur "
                        + "LEFT JOIN ur.user u ON u.deletedAt IS NULL "
                        + "GROUP BY r.id, r.name "
                        + "ORDER BY userCount DESC")
        List<Object[]> getRoleUsageStatistics();

        /**
         * Finds the most used roles, based on the number of active users assigned to
         * them.
         * The number of roles returned is determined by the {@link Pageable} object.
         *
         * @param pageable The pagination information (e.g., to limit the number of
         *                 results).
         * @return A {@link List} of {@link Role} entities, ordered by the count of
         *         active users in descending order.
         */
        @Query("SELECT r FROM Role r "
                        + "LEFT JOIN r.userRoles ur "
                        + "LEFT JOIN ur.user u ON u.deletedAt IS NULL "
                        + "GROUP BY r.id "
                        + "ORDER BY COUNT(ur) DESC")
        List<Role> findMostUsedRoles(Pageable pageable);

        // ==================== ADMIN SPECIFIC QUERIES ====================
        /**
         * Finds the "ADMIN" role. This query assumes there is a role named 'ADMIN' in
         * the system.
         *
         * @return An {@link Optional} containing the "ADMIN" {@link Role} if found, or
         *         an empty {@link Optional} otherwise.
         */
        @Query("SELECT r FROM Role r WHERE UPPER(r.name) = 'ADMIN'")
        Optional<Role> findAdminRole();

        /**
         * Finds all roles that have 'ADMIN' in their name (case-insensitive).
         *
         * @return A {@link List} of administrative {@link Role} entities.
         */
        @Query("SELECT r FROM Role r WHERE UPPER(r.name) LIKE '%ADMIN%'")
        List<Role> findAdminRoles();

        /**
         * Finds all roles that have 'USER' in their name (case-insensitive).
         *
         * @return A {@link List} of common user {@link Role} entities.
         */
        @Query("SELECT r FROM Role r WHERE UPPER(r.name) LIKE '%USER%'")
        List<Role> findUserRoles();

        /**
         * Checks if there is at least one active user with the 'ADMIN' role in the
         * system.
         *
         * @return True if an active admin exists, false otherwise.
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
         * Finds a list of roles by their IDs.
         *
         * @param roleIds A {@link List} of role IDs to search for.
         * @return A {@link List} of {@link Role} entities whose IDs are in the provided
         *         list.
         */
        @Query("SELECT r FROM Role r WHERE r.id IN :roleIds")
        List<Role> findByIdIn(@Param("roleIds") List<Long> roleIds);

        /**
         * Finds a list of roles by their names (case-insensitive).
         *
         * @param roleNames A {@link List} of role names to search for.
         * @return A {@link List} of {@link Role} entities whose names are in the
         *         provided list.
         */
        @Query("SELECT r FROM Role r WHERE UPPER(r.name) IN :roleNames")
        List<Role> findByNameIn(@Param("roleNames") List<String> roleNames);

        // ==================== CUSTOM NAMED QUERIES ====================
        /**
         * Finds roles whose names match a given pattern using SQL LIKE operator.
         *
         * @param pattern The pattern to match against role names (e.g., "%ADMIN%").
         * @return A {@link List} of {@link Role} entities matching the pattern.
         */
        @Query("SELECT r FROM Role r WHERE r.name LIKE :pattern")
        List<Role> findByNamePattern(@Param("pattern") String pattern);

        /**
         * Finds all active roles, defined as roles that are assigned to at least one
         * active user.
         *
         * @return A {@link List} of active {@link Role} entities.
         */
        @Query("SELECT DISTINCT r FROM Role r "
                        + "JOIN r.userRoles ur "
                        + "JOIN ur.user u "
                        + "WHERE u.status = 'ACTIVE' AND u.deletedAt IS NULL")
        List<Role> findActiveRoles();

        /**
         * Finds all inactive roles, defined as roles that are not assigned to any
         * active user.
         *
         * @return A {@link List} of inactive {@link Role} entities.
         */
        @Query("SELECT r FROM Role r "
                        + "WHERE r.id NOT IN ("
                        + "    SELECT DISTINCT ur.role.id FROM UserRole ur "
                        + "    JOIN ur.user u "
                        + "    WHERE u.status = 'ACTIVE' AND u.deletedAt IS NULL"
                        + ")")
        List<Role> findInactiveRoles();
}
