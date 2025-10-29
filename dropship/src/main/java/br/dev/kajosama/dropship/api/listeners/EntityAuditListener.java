package br.dev.kajosama.dropship.api.listeners;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.dev.kajosama.dropship.domain.interfaces.Auditable;
import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import br.dev.kajosama.dropship.domain.repositories.AuditLogRepository;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Transient;

@Component
public class EntityAuditListener {

    private static final Logger logger = LoggerFactory.getLogger(EntityAuditListener.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final AuditLogRepository auditRepository;
    private final ThreadLocal<Map<String, Object>> snapshotHolder = ThreadLocal.withInitial(HashMap::new);

    public EntityAuditListener(AuditLogRepository auditRepository) {
        this.auditRepository = auditRepository;
        logger.info("✅ EntityAuditListener initialized");
    }

    // ===========================
    // Lifecycle JPA Callbacks
    // ===========================

    @PostLoad
    public void captureSnapshot(Object entity) {
        if (!isAuditable(entity)) {
            return;
        }

        try {
            Map<String, Object> snapshot = captureEntitySnapshot(entity);
            String key = generateCacheKey(entity);
            snapshotHolder.get().put(key, snapshot);
            logger.debug("📸 Snapshot captured for {} [key={}]", entity.getClass().getSimpleName(), key);
        } catch (IllegalAccessException e) {
            logger.error("❌ Error capturing snapshot for {}", entity.getClass().getSimpleName(), e);
        }
    }

    @PostPersist
    public void auditCreate(Object entity) {
        if (!isAuditable(entity)) {
            return;
        }

        try {
            logger.info("🟢 Entity created: {}", entity.getClass().getSimpleName());
            String snapshot = serializeEntity(entity);
            saveAuditLog(entity, ActionType.CREATE, snapshot);
        } catch (Exception e) {
            logger.error("❌ Error auditing CREATE for {}", entity.getClass().getSimpleName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @PostUpdate
    public void auditUpdate(Object entity) {
        if (!isAuditable(entity)) {
            return;
        }

        try {
            logger.info("🟡 Entity updated: {}", entity.getClass().getSimpleName());
            String key = generateCacheKey(entity);
            Map<String, Object> oldSnapshot = (Map<String, Object>) snapshotHolder.get().get(key);

            String snapshot;
            if (oldSnapshot != null) {
                snapshot = serializeChanges(entity, oldSnapshot);
                logger.debug("🔄 Changes detected and serialized");
            } else {
                snapshot = serializeEntity(entity);
                logger.debug("📝 No old snapshot found, serializing current entity");
            }

            saveAuditLog(entity, ActionType.UPDATE, snapshot);
        } catch (Exception e) {
            logger.error("❌ Error auditing UPDATE for {}", entity.getClass().getSimpleName(), e);
        }
    }

    @PostRemove
    public void auditDelete(Object entity) {
        if (!isAuditable(entity)) {
            return;
        }

        try {
            logger.info("🔴 Entity deleted: {}", entity.getClass().getSimpleName());
            String snapshot = serializeEntity(entity);
            saveAuditLog(entity, ActionType.DELETE, snapshot);
        } catch (Exception e) {
            logger.error("❌ Error auditing DELETE for {}", entity.getClass().getSimpleName(), e);
        }
    }

    // ===========================
    // Métodos Privados
    // ===========================

    private void saveAuditLog(Object entity, ActionType actionType, String snapshotJson) {
        try {
            String entityName = entity.getClass().getSimpleName();
            Long entityId = extractEntityId(entity);
            String userEmail = extractCurrentUserEmail();

            if (entityId == null) {
                logger.warn("⚠️ Entity ID is null for {}, skipping audit", entityName);
                return;
            }

            AuditLog auditLog = new AuditLog(entityName, entityId, actionType, userEmail, snapshotJson);
            AuditLog saved = auditRepository.save(auditLog);

            logger.info("✅ Audit logged successfully: {} [ID: {}] - Action: {} - User: {}", 
                entityName, saved.getId(), actionType, userEmail);

        } catch (Exception e) {
            logger.error("❌ Failed to save audit log", e);
        }
    }

    private String serializeEntity(Object entity) {
        try {
            Map<String, Object> entityData = new HashMap<>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                if (shouldSkipField(field)) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(entity);
                entityData.put(field.getName(), value);
            }

            String json = mapper.writeValueAsString(entityData);
            logger.debug("📋 Entity serialized: {} bytes", json.length());
            return json;

        } catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            logger.error("❌ Error serializing entity", e);
            return "{}";
        }
    }

    private String serializeChanges(Object entity, Map<String, Object> oldSnapshot) {
        try {
            Map<String, Map<String, Object>> changes = new HashMap<>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                if (shouldSkipField(field)) {
                    continue;
                }

                field.setAccessible(true);
                Object newValue = field.get(entity);
                Object oldValue = oldSnapshot.get(field.getName());

                if (!Objects.equals(newValue, oldValue)) {
                    Map<String, Object> change = new HashMap<>();
                    change.put("old", oldValue);
                    change.put("new", newValue);
                    changes.put(field.getName(), change);
                    logger.debug("🔄 Field changed: {} (old: {} -> new: {})", field.getName(), oldValue, newValue);
                }
            }

            String json = mapper.writeValueAsString(changes);
            logger.debug("📊 Changes serialized: {} fields, {} bytes", changes.size(), json.length());
            return json;

        } catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            logger.error("❌ Error serializing changes", e);
            return "{}";
        }
    }

    private Map<String, Object> captureEntitySnapshot(Object entity) throws IllegalAccessException {
        Map<String, Object> snapshot = new HashMap<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (shouldSkipField(field)) {
                continue;
            }

            field.setAccessible(true);
            snapshot.put(field.getName(), field.get(entity));
        }

        return snapshot;
    }

    private Long extractEntityId(Object entity) {
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                    field.setAccessible(true);
                    Object id = field.get(entity);
                    return id instanceof Long ? (Long) id : null;
                }
            }
        } catch (IllegalAccessException e) {
            logger.warn("Could not extract entity ID", e);
        }
        return null;
    }

    private String extractCurrentUserEmail() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "system";
            }

            Object principal = auth.getPrincipal();
            return switch (principal) {
                case User user -> user.getEmail();
                case org.springframework.security.core.userdetails.User springUser -> springUser.getUsername();
                case String username -> username;
                default -> "unknown";
            };
        } catch (Exception e) {
            logger.warn("Could not extract current user email, using 'system'", e);
            return "system";
        }
    }

    private boolean isAuditable(Object entity) {
        return entity != null && entity.getClass().isAnnotationPresent(Auditable.class);
    }

    private boolean shouldSkipField(Field field) {
        return field.isAnnotationPresent(Transient.class) ||
               java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
               java.lang.reflect.Modifier.isTransient(field.getModifiers());
    }

    private String generateCacheKey(Object entity) {
        Long id = extractEntityId(entity);
        return entity.getClass().getSimpleName() + ":" + id;
    }
}