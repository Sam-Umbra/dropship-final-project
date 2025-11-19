package br.dev.kajosama.dropship.api.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import br.dev.kajosama.dropship.domain.repositories.AuditLogRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditRepo;

    public List<AuditLog> findAll() {
        List<AuditLog> list = auditRepo.findAll();
        if (list.isEmpty()) {
            throw new EntityNotFoundException
            ("No Audit Logs were found");
        }
        return list;
    }

    public List<AuditLog> findByActionType(ActionType actionType) {
        List<AuditLog> list = auditRepo.findByActionType(actionType);
        if (list.isEmpty()) {
            throw new EntityNotFoundException
            ("No Audit Logs with action type: { " + actionType + " } were found");
        }
        return list;
    }

    public List<AuditLog> findByEntityNameAndEntityId(String name, Long id) {
        var list = auditRepo.findByEntityNameAndEntityIdOrderByTimestampDesc(name, id);
        if (list.isEmpty()) {
            throw new EntityNotFoundException
            ("No Audit Logs with entity name : {" + 
            name + "} and entity id : {" + 
            id + "} were found");
        }
        return list;
    }

    public List<AuditLog> findBySavedBy(String name) {
        var list = auditRepo.findBySavedByOrderByTimestampDesc(name);
        if (list.isEmpty()) {
            throw new EntityNotFoundException
            ("No Audit Logs with registred email: {" + name + "} found");
        }
        return list;
    }

    public List<AuditLog> findByTimeInterval(LocalDateTime start, LocalDateTime end) {
        var list = auditRepo.findByTimestampBetweenOrderByTimestampDesc(start, end);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("No Audit Logs found between the dates " + 
            start + " and " + end);
        }
        return list;
    }

    public AuditLog findById(Long id) {
        return auditRepo.findById(id)
                .orElseThrow(
                    () -> new EntityNotFoundException
                    ("Audit Log with id: {" + id + "} NOT FOUND")
                );
    }

}
