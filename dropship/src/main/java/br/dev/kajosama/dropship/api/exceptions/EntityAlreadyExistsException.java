package br.dev.kajosama.dropship.api.exceptions;

/**
 * @author Sam_Umbra
 * @Description Exception thrown when an attempt is made to create an entity
 *              that already exists in the system, based on a unique identifier
 *              (e.g., email, CPF, CNPJ).
 *              This exception provides a clear message indicating which entity
 *              and field caused the conflict.
 */
public class EntityAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code EntityAlreadyExistsException} with a detailed
     * message.
     * The message is formatted to indicate which entity, field, and value caused
     * the conflict.
     *
     * @param entityName The name of the entity that already exists (e.g., "User",
     *                   "Supplier").
     * @param fieldName  The name of the field that caused the uniqueness violation
     *                   (e.g., "email", "cpf").
     * @param fieldValue The value of the field that already exists (e.g.,
     *                   "test@example.com", "123.456.789-00").
     */
    public EntityAlreadyExistsException(String entityName, String fieldName, String fieldValue) {
        super(String.format("%s with %s '%s' already exists", entityName, fieldName, fieldValue));
    }

}
