package br.dev.kajosama.dropship.domain.model.objects;


public enum SupplierTier {

    OFFICIAL(1, "Fornecedore Oficial/Official Supplier"),
    ESPECIALIZED(2, "Fornecedor Especializado/Especialized Supplier"),
    VERIFIED(3, "Fornecedor Verificado/Verified Supplier"),
    NORMAL(4, "Fornecedor Normal/Normal Supplier");

    private final Integer level;
    private final String name;

    private SupplierTier(Integer level, String name) {
        this.level = level;
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public static SupplierTier fromLevel(int level) {
        for (SupplierTier t : values()) {
            if (t.level == level) return t;
        }
        throw new IllegalArgumentException("Tier inválido: " + level);
    }

}
