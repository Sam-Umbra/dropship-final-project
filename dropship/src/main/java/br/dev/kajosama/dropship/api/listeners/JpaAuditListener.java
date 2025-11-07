package br.dev.kajosama.dropship.api.listeners;

import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;

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

        // Configuração robusta do ObjectMapper
        this.objectMapper = new ObjectMapper();

        // Serializa Java 8 Date/Time
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Suporte a entidades Hibernate, evita loops e problemas com lazy
        Hibernate6Module hibernateModule = new Hibernate6Module();
        hibernateModule.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        hibernateModule.enable(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        this.objectMapper.registerModule(hibernateModule);

        // Ignora nulls e proxies
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        persistAudit(event.getEntity(), ActionType.CREATE);
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        persistAudit(event.getEntity(), ActionType.UPDATE);
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        persistAudit(event.getEntity(), ActionType.DELETE);
        return false;
    }

    private void persistAudit(Object entity, ActionType actionType) {
        try {
            if (entity == null || entity instanceof AuditLog) {
                return;
            }

            // Audita somente entidades marcadas com @Auditable
            if (!entity.getClass().isAnnotationPresent(Auditable.class)) {
                return;
            }

            String entityName;
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                entityName = entity.getClass().getSimpleName();
                Long entityId = extractEntityId(entity);
                String savedBy = resolveCurrentUser();
                // Serializa a entidade para JSON, protegendo contra loops
                String snapshotJson = objectMapper.writeValueAsString(entity);
                AuditLog auditLog = new AuditLog(entityName, entityId, actionType, savedBy, snapshotJson);
                em.persist(auditLog);
                em.getTransaction().commit();
            }

            logger.debug("✅ Audit log persisted for {} [{}]", entityName, actionType);

        } catch (JsonProcessingException e) {
            logger.error("❌ Error persisting audit log for " + entity.getClass().getSimpleName(), e);
        }
    }

    private Long extractEntityId(Object entity) {
        try {
            var field = entity.getClass().getDeclaredField("id");
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