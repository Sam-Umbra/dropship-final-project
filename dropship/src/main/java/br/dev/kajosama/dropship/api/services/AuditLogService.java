package br.dev.kajosama.dropship.api.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import br.dev.kajosama.dropship.domain.repositories.AuditLogRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link AuditLog} entities.
 *              Provides business logic for retrieving audit logs based on
 *              various criteria,
 *              such as action type, entity, user, and time interval.
 *              It interacts with {@link AuditLogRepository}.
 */
@Service
public class AuditLogService {

    /**
     * Repository for {@link AuditLog} entities.
     */
    @Autowired
    private AuditLogRepository auditRepo;

    /**
     * Retrieves a list of all audit logs.
     *
     * @return A {@link List} of all {@link AuditLog} entities.
     * @throws EntityNotFoundException If no audit logs are found in the system.
     */
    public List<AuditLog> findAll() {
        List<AuditLog> list = auditRepo.findAll();
        if (list.isEmpty()) {
            throw new EntityNotFoundException("No Audit Logs were found");
        }
        return list;
    }

    /**
     * Retrieves a list of audit logs filtered by a specific {@link ActionType}.
     *
     * @param actionType The {@link ActionType} to filter the audit logs by.
     * @return A {@link List} of {@link AuditLog} entities matching the specified
     *         action type.
     * @throws EntityNotFoundException If no audit logs are found for the given
     *                                 action type.
     */
    public List<AuditLog> findByActionType(ActionType actionType) {
        List<AuditLog> list = auditRepo.findByActionType(actionType);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("No Audit Logs with action type: { " + actionType + " } were found");
        }
        return list;
    }

    /**
     * Retrieves a list of audit logs for a specific entity, identified by its name
     * and ID.
     * The results are ordered by timestamp in descending order.
     *
     * @param name The name of the entity (e.g., "Product", "Order").
     * @param id   The ID of the entity.
     * @return A {@link List} of {@link AuditLog} entities for the specified entity.
     * @throws EntityNotFoundException If no audit logs are found for the given
     *                                 entity name and ID.
     */
    public List<AuditLog> findByEntityNameAndEntityId(String name, Long id) {
        var list = auditRepo.findByEntityNameAndEntityIdOrderByTimestampDesc(name, id);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("No Audit Logs with entity name : {" +
                    name + "} and entity id : {" +
                    id + "} were found");
        }
        return list;
    }

    /**
     * Retrieves a list of audit logs created by a specific user.
     * The results are ordered by timestamp in descending order.
     *
     * @param name The identifier (e.g., email) of the user who saved the audit log.
     * @return A {@link List} of {@link AuditLog} entities saved by the specified
     *         user.
     * @throws EntityNotFoundException If no audit logs are found for the given
     *                                 user.
     */
    public List<AuditLog> findBySavedBy(String name) {
        var list = auditRepo.findBySavedByOrderByTimestampDesc(name);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("No Audit Logs with registred email: {" + name + "} found");
        }
        return list;
    }

    /**
     * Retrieves a list of audit logs within a specified time interval.
     * The results are ordered by timestamp in descending order.
     *
     * @param start The start {@link LocalDateTime} of the interval (inclusive).
     * @param end   The end {@link LocalDateTime} of the interval (inclusive).
     * @return A {@link List} of {@link AuditLog} entities within the specified time
     *         range.
     * @throws EntityNotFoundException If no audit logs are found within the given
     *                                 time interval.
     */
    public List<AuditLog> findByTimeInterval(LocalDateTime start, LocalDateTime end) {
        var list = auditRepo.findByTimestampBetweenOrderByTimestampDesc(start, end);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("No Audit Logs found between the dates " +
                    start + " and " + end);
        }
        return list;
    }

    /**
     * Retrieves a single audit log by its unique identifier.
     *
     * @param id The ID of the {@link AuditLog} to retrieve.
     * @return The {@link AuditLog} entity with the specified ID.
     * @throws EntityNotFoundException If no audit log with the given ID is found.
     */
    public AuditLog findById(Long id) {
        return auditRepo.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Audit Log with id: {" + id + "} NOT FOUND"));
    }
}
