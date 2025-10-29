package br.dev.kajosama.dropship.domain.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CNPJ;

import br.dev.kajosama.dropship.domain.interfaces.Auditable;
import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.model.enums.SupplierTier;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "suppliers")
@Auditable
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @NotBlank
    @Column(name = "supplier_name", length = 150)
    @Size(max = 150)
    private String name;

    @CNPJ
    @NotBlank
    private String cnpj;

    @NotNull
    private Boolean approved;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SupplierTier tier;

    @Column(name = "db_url")
    private String dbUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @NotBlank
    @Column(name = "contact_email", nullable = false)
    private String email;

    @NotBlank
    @ValidPhone
    @Column(name = "contact_phone", nullable = false)
    private String phone;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    @Column(name = "commission_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal commissionRate;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupplierUser> supplierUsers = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Supplier() {
    }

    public Supplier(String name, String cnpj, Boolean approved, SupplierTier tier, String dbUrl, AccountStatus status, String email, String phone, BigDecimal commissionRate) {
        this.name = name;
        this.cnpj = cnpj;
        this.approved = approved;
        this.tier = tier;
        this.dbUrl = dbUrl;
        this.email = email;
        this.phone = phone;
        this.commissionRate = commissionRate;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return this.cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Boolean isApproved() {
        return this.approved;
    }

    public Boolean getApproved() {
        return this.approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public SupplierTier getTier() {
        return this.tier;
    }

    public void setTier(SupplierTier tier) {
        this.tier = tier;
    }

    public String getDbUrl() {
        return this.dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getcommissionRate() {
        return this.commissionRate;
    }

    public void setcommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Supplier id(Long id) {
        setId(id);
        return this;
    }

    public Supplier name(String name) {
        setName(name);
        return this;
    }

    public Supplier cnpj(String cnpj) {
        setCnpj(cnpj);
        return this;
    }

    public Supplier approved(Boolean approved) {
        setApproved(approved);
        return this;
    }

    public Supplier tier(SupplierTier tier) {
        setTier(tier);
        return this;
    }

    public Supplier dbUrl(String dbUrl) {
        setDbUrl(dbUrl);
        return this;
    }

    public Supplier createdAt(LocalDateTime createdAt) {
        setCreatedAt(createdAt);
        return this;
    }

    public Supplier updatedAt(LocalDateTime updatedAt) {
        setUpdatedAt(updatedAt);
        return this;
    }

    public Supplier deletedAt(LocalDateTime deletedAt) {
        setDeletedAt(deletedAt);
        return this;
    }

    public Supplier status(AccountStatus status) {
        setStatus(status);
        return this;
    }

    public Supplier email(String email) {
        setEmail(email);
        return this;
    }

    public Supplier phone(String phone) {
        setPhone(phone);
        return this;
    }

    public Supplier commissionRate(BigDecimal commissionRate) {
        setcommissionRate(commissionRate);
        return this;
    }

    public List<SupplierUser> getSupplierUsers() {
        return supplierUsers;
    }

    public void setSupplierUsers(List<SupplierUser> supplierUsers) {
        this.supplierUsers = supplierUsers;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Supplier)) {
            return false;
        }
        Supplier supplier = (Supplier) o;
        return Objects.equals(id, supplier.id) && Objects.equals(name, supplier.name) && Objects.equals(cnpj, supplier.cnpj) && Objects.equals(approved, supplier.approved) && Objects.equals(tier, supplier.tier) && Objects.equals(dbUrl, supplier.dbUrl) && Objects.equals(createdAt, supplier.createdAt) && Objects.equals(updatedAt, supplier.updatedAt) && Objects.equals(deletedAt, supplier.deletedAt) && Objects.equals(status, supplier.status) && Objects.equals(email, supplier.email) && Objects.equals(phone, supplier.phone) && Objects.equals(commissionRate, supplier.commissionRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cnpj, approved, tier, dbUrl, createdAt, updatedAt, deletedAt, status, email, phone, commissionRate);
    }

    @Override
    public String toString() {
        return "{"
                + " id='" + getId() + "'"
                + ", name='" + getName() + "'"
                + ", cnpj='" + getCnpj() + "'"
                + ", approved='" + isApproved() + "'"
                + ", tier='" + getTier() + "'"
                + ", dbUrl='" + getDbUrl() + "'"
                + ", createdAt='" + getCreatedAt() + "'"
                + ", updatedAt='" + getUpdatedAt() + "'"
                + ", deletedAt='" + getDeletedAt() + "'"
                + ", status='" + getStatus() + "'"
                + ", email='" + getEmail() + "'"
                + ", phone='" + getPhone() + "'"
                + ", commissionRate='" + getcommissionRate() + "'"
                + "}";
    }
}
