package br.dev.kajosama.dropship.api.listeners;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaListenerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(JpaListenerRegistry.class);

    private final EntityManagerFactory emf;
    private final JpaAuditListener jpaAuditListener;

    public JpaListenerRegistry(EntityManagerFactory emf, JpaAuditListener jpaAuditListener) {
        this.emf = emf;
        this.jpaAuditListener = jpaAuditListener;
    }

    @PostConstruct
    public void registerListeners() {
        try {
            SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
            EventListenerRegistry registry = sessionFactory
                    .getServiceRegistry()
                    .getService(EventListenerRegistry.class);

            registry.appendListeners(EventType.PRE_INSERT, jpaAuditListener);
            registry.appendListeners(EventType.PRE_UPDATE, jpaAuditListener);
            registry.appendListeners(EventType.PRE_DELETE, jpaAuditListener);

            logger.info("✅ JpaAuditListener registered successfully for CREATE, UPDATE, DELETE");
        } catch (Exception e) {
            logger.error("❌ Failed to register JpaAuditListener", e);
        }
    }
}