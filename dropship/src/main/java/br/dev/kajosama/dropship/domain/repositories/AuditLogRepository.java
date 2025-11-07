package br.dev.kajosama.dropship.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId);

    Page<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId, Pageable pageable);

    List<AuditLog> findByActionType(ActionType actionType);

    List<AuditLog> findBySavedByOrderByTimestampDesc(String savedBy);

    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);

    Long countByEntityNameAndEntityId(String entityName, Long entityId);

}