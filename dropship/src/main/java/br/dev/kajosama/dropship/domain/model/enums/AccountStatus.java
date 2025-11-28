package br.dev.kajosama.dropship.domain.model.enums;

/**
 * Represents the various statuses an account (like a User or Supplier) can have within the system.
 * This enum is used to manage the lifecycle and access rights of an account.
 */
public enum AccountStatus {
    /**
     * The account is active and fully operational.
     */
    ACTIVE,

    /**
     * The account has been deactivated by a user or an administrator but can potentially be reactivated.
     */
    INACTIVE,

    /**
     * The account has been temporarily suspended by an administrator. The user cannot log in or perform actions.
     */
    SUSPENDED,

    /**
     * The account has been created but is awaiting confirmation or approval (e.g., email verification).
     */
    PENDING,

    /**
     * The account has been marked for deletion (soft delete). It is not accessible and may be permanently removed later.
     */
    DELETED
}
