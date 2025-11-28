package br.dev.kajosama.dropship.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;

/**
 * Repository interface for managing {@link AuditLog} entities.
 * Provides methods for performing CRUD operations and custom queries related to audit logs.
 * @author Sam_Umbra
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
 * Finds a list of audit logs for a specific entity, ordered by timestamp in descending order.
 *
 * @param entityName The name of the entity.
 * @param entityId The ID of the entity.
 * @return A list of {@link AuditLog} entities.
 */
    List<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId);

    /**
 * Finds a page of audit logs for a specific entity, ordered by timestamp in descending order.
 *
 * @param entityName The name of the entity.
 * @param entityId The ID of the entity.
 * @param pageable The pagination information.
 * @return A {@link Page} of {@link AuditLog} entities.
 */
    Page<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId, Pageable pageable);

    /**
 * Finds a list of audit logs by a specific action type.
 *
 * @param actionType The {@link ActionType} of the audit logs.
 * @return A list of {@link AuditLog} entities.
 */
    List<AuditLog> findByActionType(ActionType actionType);

    /**
 * Finds a list of audit logs saved by a specific user, ordered by timestamp in descending order.
 *
 * @param savedBy The identifier of the user who saved the audit log.
 * @return A list of {@link AuditLog} entities.
 */
    List<AuditLog> findBySavedByOrderByTimestampDesc(String savedBy);

    /**
 * Finds a list of audit logs within a specified date range, ordered by timestamp in descending order.
 *
 * @param startDate The start date and time of the range.
 * @param endDate The end date and time of the range.
 * @return A list of {@link AuditLog} entities.
 */
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
 * Counts the number of audit logs for a specific entity.
 *
 * @param entityName The name of the entity.
 * @param entityId The ID of the entity.
 * @return The count of audit logs for the specified entity.
 */
    Long countByEntityNameAndEntityId(String entityName, Long entityId);

}