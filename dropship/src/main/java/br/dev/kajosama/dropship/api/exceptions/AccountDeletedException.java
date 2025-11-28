package br.dev.kajosama.dropship.api.exceptions;

/**
 * @author Sam_Umbra
 * @Description Exception thrown when an operation is attempted on a user
 *              account that has been marked as deleted.
 *              This typically indicates that the account is no longer active
 *              and cannot be used for certain actions.
 */
public class AccountDeletedException extends RuntimeException {

    /**
     * Constructs a new {@code AccountDeletedException} with a default message.
     */
    public AccountDeletedException() {
        super("Account has been deleted");
    }

    /**
     * Constructs a new {@code AccountDeletedException} with the specified detail
     * message.
     * 
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     */
    public AccountDeletedException(String message) {
        super(message);
    }
}