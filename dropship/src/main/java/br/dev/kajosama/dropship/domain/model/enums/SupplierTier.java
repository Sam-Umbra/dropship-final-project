package br.dev.kajosama.dropship.domain.model.enums;

/**
 * Represents the classification or tier of a supplier.
 * Tiers are used to categorize suppliers based on criteria like reliability, sales volume, or official status.
 */
public enum SupplierTier {

    /**
     * An official partner or manufacturer of the products. The highest tier.
     */
    OFFICIAL(1, "Official Supplier"),
    /**
     * A supplier who specializes in a specific category of products.
     */
    ESPECIALIZED(2, "Specialized Supplier"),
    /**
     * A supplier that has been verified by the platform for reliability and quality.
     */
    VERIFIED(3, "Verified Supplier"),
    /**
     * A standard supplier without any special designation. The default tier.
     */
    NORMAL(4, "Normal Supplier");

    /**
     * The numerical level of the tier, where a lower number indicates a higher tier.
     */
    private final Integer level;
    /**
     * The descriptive name of the tier.
     */
    private final String name;

    /**
     * Private constructor to initialize the enum constants.
     *
     * @param level The numerical level of the tier.
     * @param name  The descriptive name of the tier.
     */
    private SupplierTier(Integer level, String name) {
        this.level = level;
        this.name = name;
    }

    /**
     * @return The numerical level of the tier.
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * @return The descriptive name of the tier.
     */
    public String getName() {
        return name;
    }

    /**
     * Finds a SupplierTier by its numerical level.
     *
     * @param level The level of the tier to find.
     * @return The corresponding SupplierTier.
     * @throws IllegalArgumentException if no tier with the given level is found.
     */
    public static SupplierTier fromLevel(int level) {
        for (SupplierTier t : values()) {
            if (t.level == level) return t;
        }
        throw new IllegalArgumentException("Invalid tier level: " + level);
    }

}
