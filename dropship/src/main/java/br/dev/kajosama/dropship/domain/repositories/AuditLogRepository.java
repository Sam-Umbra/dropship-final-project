package br.dev.kajosama.dropship.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Encontra todos os logs de auditoria para uma entidade específica
     */
    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);

    /**
     * Encontra todos os logs de auditoria para uma entidade com paginação
     */
    Page<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId, Pageable pageable);

    /**
     * Encontra logs por tipo de ação
     */
    List<AuditLog> findByActionType(ActionType actionType);

    /**
     * Encontra logs por usuário que realizou a ação
     */
    List<AuditLog> findBySavedBy(String savedBy);

    /**
     * Encontra logs de auditoria em um intervalo de tempo
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Encontra logs de auditoria para uma entidade dentro de um intervalo de tempo
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityName = :entityName AND a.entityId = :entityId " +
           "AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLog> findAuditHistoryByEntity(
        @Param("entityName") String entityName,
        @Param("entityId") Long entityId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Encontra logs de auditoria com paginação em um intervalo de tempo
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityName = :entityName AND a.entityId = :entityId " +
           "AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    Page<AuditLog> findAuditHistoryByEntityPaginated(
        @Param("entityName") String entityName,
        @Param("entityId") Long entityId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    /**
     * Encontra todos os logs de ações realizadas por um usuário específico
     */
    @Query("SELECT a FROM AuditLog a WHERE a.savedBy = :savedBy ORDER BY a.timestamp DESC")
    Page<AuditLog> findAuditLogsByUser(@Param("savedBy") String savedBy, Pageable pageable);

    /**
     * Encontra logs de auditoria por nome de entidade
     */
    List<AuditLog> findByEntityName(String entityName);

    /**
     * Encontra logs de auditoria por nome de entidade com paginação
     */
    Page<AuditLog> findByEntityName(String entityName, Pageable pageable);

    /**
     * Conta quantas alterações foram feitas em uma entidade específica
     */
    long countByEntityNameAndEntityId(String entityName, Long entityId);

    /**
     * Encontra o último log de auditoria para uma entidade específica
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityName = :entityName AND a.entityId = :entityId " +
           "ORDER BY a.timestamp DESC LIMIT 1")
    AuditLog findLastAuditByEntity(@Param("entityName") String entityName, @Param("entityId") Long entityId);
}