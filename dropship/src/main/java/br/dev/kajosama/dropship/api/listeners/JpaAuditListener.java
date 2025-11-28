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

/**
 * @author Sam_Umbra
 * @Description JPA event listener for auditing entity changes (CREATE, UPDATE,
 *              DELETE).
 *              This listener intercepts Hibernate events to capture entity
 *              state changes and
 *              persists them as {@link AuditLog} records. It uses an
 *              {@link ObjectMapper}
 *              to serialize entity snapshots and changed fields into JSON
 *              format.
 */
@Component
public class JpaAuditListener implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

    private static final Logger logger = LoggerFactory.getLogger(JpaAuditListener.class);

    private final EntityManagerFactory emf;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new JpaAuditListener.
     *
     * @param emf The {@link EntityManagerFactory} used to create
     *            {@link EntityManager} instances for persisting audit logs.
     */
    public JpaAuditListener(EntityManagerFactory emf) {
        this.emf = emf;
        this.objectMapper = configureMapper();
    }

    /**
     * Configures and returns an {@link ObjectMapper} instance for JSON
     * serialization.
     * The mapper is configured to handle Java 8 Date/Time API types, Hibernate lazy
     * loading,
     * include non-null properties, and access fields directly for serialization.
     *
     * @return A configured {@link ObjectMapper}.
     */
    private ObjectMapper configureMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Hibernate6Module hibernateModule = new Hibernate6Module();
        // Disable transient annotation handling to ensure all fields are considered for
        // serialization
        hibernateModule.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        // Enable serialization of identifiers for lazy-loaded objects that are not yet
        // initialized
        hibernateModule.enable(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        mapper.registerModule(hibernateModule);

        // Include only non-null properties in the JSON output
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Set visibility to ANY for fields, allowing serialization of private fields
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    /**
     * Callback for pre-insert events.
     * Captures the state of the entity before it is inserted into the database and
     * creates an audit log.
     *
     * @param event The {@link PreInsertEvent} containing information about the
     *              entity being inserted.
     * @return Always returns {@code false} to indicate that the event should not be
     *         vetoed.
     */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        persistAudit(event.getEntity(), ActionType.CREATE, null);
        return false;
    }

    /**
     * Callback for pre-update events.
     * Captures the changes made to an entity before it is updated in the database
     * and creates an audit log.
     *
     * @param event The {@link PreUpdateEvent} containing information about the
     *              entity being updated.
     * @return Always returns {@code false} to indicate that the event should not be
     *         vetoed.
     */
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Map<String, Map<String, Object>> changedFields = getChangedFieldsWithOldValues(event);
        persistAudit(event.getEntity(), ActionType.UPDATE, changedFields);
        return false;
    }

    /**
     * Callback for pre-delete events.
     * Captures the state of the entity before it is deleted from the database and
     * creates an audit log.
     *
     * @param event The {@link PreDeleteEvent} containing information about the
     *              entity being deleted.
     * @return Always returns {@code false} to indicate that the event should not be
     *         vetoed.
     */
    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        persistAudit(event.getEntity(), ActionType.DELETE, null);
        return false;
    }

    /**
     * Persists an {@link AuditLog} entry for the given entity and action type.
     * It serializes the entity's state or the specific changes into a JSON string.
     *
     * @param entity     The entity involved in the event.
     * @param actionType The type of action performed (CREATE, UPDATE, DELETE).
     * @param changes    A map of changed fields with old and new values, used
     *                   specifically for UPDATE actions.
     */
    private void persistAudit(Object entity, ActionType actionType, Map<String, ?> changes) {
        if (entity == null || entity instanceof AuditLog)
            return;
        // Only audit entities annotated with @Auditable
        if (!entity.getClass().isAnnotationPresent(Auditable.class))
            return;

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
     * Identifies and returns a map of changed fields along with their old and new
     * values
     * during an update event.
     * The format is: {@code { "fieldName": {"old": oldValue, "new": newValue} }}.
     * Collections and Maps are simplified to generic labels ("list", "map") to
     * prevent large JSON outputs.
     *
     * @param event The {@link PreUpdateEvent} containing the old and new state of
     *              the entity.
     * @return A {@link Map} where keys are field names and values are maps
     *         containing "old" and "new" values.
     *         Returns an empty map if no changes are detected or if old/new states
     *         are unavailable.
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
     * Simplifies the representation of complex objects (like collections, maps, or
     * other entities)
     * to prevent excessively large JSON outputs in the audit log.
     * Collections and Maps are represented by their type names ("list", "map").
     * Other entities are represented by their simple class name.
     *
     * @param value The object value to simplify.
     * @return A simplified representation of the value, or the value itself if it's
     *         a primitive or simple type.
     */
    private Object simplifyValue(Object value) {
        if (value == null)
            return null;
        if (value instanceof Collection)
            return "list";
        if (value instanceof Map)
            return "map";
        if (value.getClass().getPackageName().startsWith("br.dev.kajosama.dropship.domain.model.entities")) {
            // Evita recursão com entidades internas
            return value.getClass().getSimpleName();
        }
        return value;
    }

    /**
     * Creates a simplified map representation of an entity by iterating over its
     * fields
     * and applying the {@link #simplifyValue(Object)} method to each field's value.
     * This is used when serializing the entire entity (e.g., for CREATE or DELETE
     * actions)
     * to avoid deep serialization of related entities or large collections.
     *
     * @param entity The entity object to flatten.
     * @return A {@link Map} representing the simplified entity state, or the
     *         original entity if an error occurs.
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

    /**
     * Extracts the 'id' field value from an entity using reflection.
     *
     * @param entity The entity object from which to extract the ID.
     * @return The {@code Long} value of the 'id' field, or {@code null} if the
     *         field is not found, inaccessible, or its value is null.
     */
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

    /**
     * Resolves the name of the current authenticated user from the Spring Security
     * context.
     * If no user is authenticated or an error occurs, it defaults to "system".
     *
     * @return The username of the current authenticated user, or "system" if not
     *         available.
     */
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
