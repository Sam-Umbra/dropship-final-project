package br.dev.kajosama.dropship.domain.model.enums;

/**
 * Represents the type of action performed on an entity.
 * This is commonly used for auditing, logging, or tracking changes.
 */
public enum ActionType {
    /**
     * Represents the creation of a new entity.
     */
    CREATE,
    /**
     * Represents the update or modification of an existing entity.
     */
    UPDATE,
    /**
     * Represents the deletion of an entity.
     */
    DELETE
}