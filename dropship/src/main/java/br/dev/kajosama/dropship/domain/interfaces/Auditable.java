package br.dev.kajosama.dropship.domain.interfaces;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @author Sam_Umbra
 * @Description Annotation to mark an entity as auditable.
 * Entities annotated with @Auditable will have their changes (create, update, delete)
 * automatically logged to the audit_log table.
 */
public @interface Auditable {

}
