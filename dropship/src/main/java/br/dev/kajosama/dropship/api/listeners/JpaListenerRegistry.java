package br.dev.kajosama.dropship.api.listeners;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sam_Umbra
 * @Description Configuration class responsible for registering JPA event
 *              listeners,
 *              specifically the {@link JpaAuditListener}, with Hibernate's
 *              EventListenerRegistry.
 *              This ensures that the audit listener intercepts entity lifecycle
 *              events (INSERT, UPDATE, DELETE)
 *              to perform auditing operations.
 */
@Configuration
public class JpaListenerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(JpaListenerRegistry.class);

    /**
     * The {@link EntityManagerFactory} used to unwrap the Hibernate
     * {@link SessionFactoryImpl}.
     */
    private final EntityManagerFactory emf;
    /**
     * The {@link JpaAuditListener} instance to be registered.
     */
    private final JpaAuditListener jpaAuditListener;

    /**
     * Constructs a new JpaListenerRegistry.
     *
     * @param emf              The {@link EntityManagerFactory} provided by Spring.
     * @param jpaAuditListener The {@link JpaAuditListener} instance to be
     *                         registered,
     *                         injected by Spring.
     */
    public JpaListenerRegistry(EntityManagerFactory emf, JpaAuditListener jpaAuditListener) {
        this.emf = emf;
        this.jpaAuditListener = jpaAuditListener;
    }

    /**
     * Registers the {@link JpaAuditListener} for pre-insert, pre-update, and
     * pre-delete events.
     * This method is called automatically after the bean's properties have been
     * set.
     * It unwraps the Hibernate {@link SessionFactoryImpl} from the
     * {@link EntityManagerFactory}
     * and appends the audit listener to the appropriate event types in the
     * {@link EventListenerRegistry}.
     */
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