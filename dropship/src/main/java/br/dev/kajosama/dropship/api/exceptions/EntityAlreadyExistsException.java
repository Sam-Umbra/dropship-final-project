package br.dev.kajosama.dropship.api.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String entityName, String fieldName, String fieldValue) {
        super(String.format("%s with %s '%s' already exists", entityName, fieldName, fieldValue));
    }

}
