package br.dev.kajosama.dropship.api.listeners;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.dev.kajosama.dropship.domain.interfaces.Auditable;
import br.dev.kajosama.dropship.domain.model.entities.AuditLog;
import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Component
public class JpaAuditListener implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

    private static final Logger logger = LoggerFactory.getLogger(JpaAuditListener.class);

    private final EntityManagerFactory emf;
    private final ObjectMapper objectMapper;

    public JpaAuditListener(EntityManagerFactory emf) {
        this.emf = emf;
        this.objectMapper = configureMapper();
    }

    private ObjectMapper configureMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Hibernate6Module hibernateModule = new Hibernate6Module();
        hibernateModule.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        hibernateModule.enable(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        mapper.registerModule(hibernateModule);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        persistAudit(event.getEntity(), ActionType.CREATE, null);
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Map<String, Map<String, Object>> changedFields = getChangedFieldsWithOldValues(event);
        persistAudit(event.getEntity(), ActionType.UPDATE, changedFields);
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        persistAudit(event.getEntity(), ActionType.DELETE, null);
        return false;
    }

    private void persistAudit(Object entity, ActionType actionType, Map<String, ?> changes) {
        if (entity == null || entity instanceof AuditLog) return;
        if (!entity.getClass().isAnnotationPresent(Auditable.class)) return;

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String entityName = entity.getClass().getSimpleName();
            Long entityId = extractEntityId(entity);
            String savedBy = resolveCurrentUser();

            String snapshotJson;
            if (actionType == ActionType.UPDATE && changes != null && !changes.isEmpty()) {
                snapshotJson = objectMapper.writeValueAsString(changes);
            } else {
                snapshotJson = objectMapper.writeValueAsString(flattenCollections(entity));
            }

            AuditLog auditLog = new AuditLog(entityName, entityId, actionType, savedBy, snapshotJson);
            em.persist(auditLog);
            em.getTransaction().commit();

            logger.debug("✅ Audit log persisted for {} [{}]", entityName, actionType);
        } catch (Exception e) {
            logger.error("❌ Error persisting audit log for " + entity.getClass().getSimpleName(), e);
        }
    }

    /**
     * Retorna apenas os campos alterados e seus valores antigos e novos.
     * Exemplo: { "phone": {"old": "+5511999", "new": "+5511888"} }
     */
    private Map<String, Map<String, Object>> getChangedFieldsWithOldValues(PreUpdateEvent event) {
        Map<String, Map<String, Object>> changes = new LinkedHashMap<>();
        String[] propertyNames = event.getPersister().getPropertyNames();
        Object[] oldState = event.getOldState();
        Object[] newState = event.getState();

        for (int i = 0; i < propertyNames.length; i++) {
            Object oldVal = oldState != null && i < oldState.length ? oldState[i] : null;
            Object newVal = i < newState.length ? newState[i] : null;

            if (!Objects.equals(oldVal, newVal)) {
                Map<String, Object> valueDiff = new LinkedHashMap<>();
                valueDiff.put("old", simplifyValue(oldVal));
                valueDiff.put("new", simplifyValue(newVal));
                changes.put(propertyNames[i], valueDiff);
            }
        }

        return changes;
    }

    /**
     * Simplifica listas e mapas para evitar JSON gigante.
     */
    private Object simplifyValue(Object value) {
        if (value == null) return null;
        if (value instanceof Collection) return "list";
        if (value instanceof Map) return "map";
        if (value.getClass().getPackageName().startsWith("br.dev.kajosama.dropship.domain.model.entities")) {
            // Evita recursão com entidades internas
            return value.getClass().getSimpleName();
        }
        return value;
    }

    /**
     * Converte listas/mapas em rótulos genéricos antes de serializar a entidade inteira.
     */
    private Object flattenCollections(Object entity) {
        try {
            Map<String, Object> simplified = new LinkedHashMap<>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(entity);
                simplified.put(field.getName(), simplifyValue(value));
            }
            return simplified;
        } catch (IllegalAccessException e) {
            return entity;
        }
    }

    private Long extractEntityId(Object entity) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            Object idValue = field.get(entity);
            return idValue != null ? ((Number) idValue).longValue() : null;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            return null;
        }
    }

    private String resolveCurrentUser() {
        try {
            return org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
