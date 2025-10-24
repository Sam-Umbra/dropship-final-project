package br.dev.kajosama.dropship.api.listeners;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.dev.kajosama.dropship.domain.interfaces.Auditable;
import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import br.dev.kajosama.dropship.domain.repositories.AuditLogRepository;
import br.dev.kajosama.dropship.domain.util.BeanUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

@Component
public class AuditableListener {

    private static final Logger logger = LoggerFactory.getLogger(AuditableListener.class);
    private static AuditLogRepository auditRepository;
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Autowired
    public void init(AuditLogRepository repo) {
        auditRepository = repo;
    }

    @PrePersist
    public void onCreate(Object entity) {
        saveAudit(entity, ActionType.CREATE, null);
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        Object oldEntity = null;
        try {
            Class<?> clazz = entity.getClass();
            var em = BeanUtil.getBean(EntityManager.class);
            oldEntity = em.find(clazz, getIdValue(entity));
        } catch (Exception ignored) {}
        saveAudit(entity, ActionType.UPDATE, oldEntity);
    }

    @PreRemove
    public void onDelete(Object entity) {
        saveAudit(entity, ActionType.DELETE, null);
    }

    private void saveAudit(Object entity, ActionType actionType, Object oldEntity) {
        if (!entity.getClass().isAnnotationPresent(Auditable.class)) return;

        try {
            String entityName = entity.getClass().getSimpleName();
            Long entityId = (Long) getIdValue(entity);
            String email = getCurrentUserEmail();

            // snapshot: diffs para update, estado completo para create/delete
            String snapshot = (actionType == ActionType.UPDATE && oldEntity != null)
                    ? getChangedFieldsJson(entity, oldEntity)
                    : mapper.writeValueAsString(entity);

            AuditLog log = new AuditLog(entityName, entityId, actionType, email, snapshot);
            auditRepository.save(log);

        } catch (Exception e) {
            logger.error("Error while saving audit log", e);
        }
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "system";

        Object principal = auth.getPrincipal();

        return switch (principal) {
            case User user -> user.getEmail();
            case org.springframework.security.core.userdetails.User springUser -> springUser.getUsername();
            case String username -> username;
            default -> "unknown";
        };
    }

    private Object getIdValue(Object entity) throws Exception {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                field.setAccessible(true);
                return field.get(entity);
            }
        }
        return null;
    }

    private String getChangedFieldsJson(Object newEntity, Object oldEntity) throws IllegalAccessException {
        Map<String, Map<String, Object>> changes = new HashMap<>();

        for (Field field : newEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object newVal = field.get(newEntity);
            Object oldVal = field.get(oldEntity);

            if ((newVal != null && !newVal.equals(oldVal)) || (newVal == null && oldVal != null)) {
                Map<String, Object> diff = new HashMap<>();
                diff.put("old", oldVal);
                diff.put("new", newVal);
                changes.put(field.getName(), diff);
            }
        }

        try {
            return mapper.writeValueAsString(changes);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}