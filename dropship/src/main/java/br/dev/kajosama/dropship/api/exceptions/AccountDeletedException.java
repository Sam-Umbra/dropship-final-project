package br.dev.kajosama.dropship.api.exceptions;

public class AccountDeletedException extends RuntimeException {

    public AccountDeletedException() {
        super("Account has been deleted");
    }

    public AccountDeletedException(String message) {
        super(message);
    }
}