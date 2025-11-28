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

@Auditable
@Entity
@Table(name = "suppliers")
public class Supplier {

    /**
     * The unique identifier for the supplier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    /**
     * The name of the supplier.
     * Must be between 1 and 150 characters long.
     */
    @NotBlank
    @Column(name = "supplier_name", length = 150)
    @Size(max = 150)
    private String name;

    /**
     * The CNPJ (Brazilian company registration number) of the supplier.
     * Must be a valid CNPJ.
     */
    @CNPJ
    @NotBlank
    private String cnpj;

    /**
     * Indicates whether the supplier has been approved.
     */
    @NotNull
    private Boolean approved;

    /**
     * The tier of the supplier (e.g., BRONZE, SILVER, GOLD).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private SupplierTier tier;

    /**
     * The URL for the supplier's database, if applicable.
     */
    @Column(name = "db_url")
    private String dbUrl;

    /**
     * The timestamp when the supplier record was created.
     * Automatically set upon creation and cannot be updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the supplier record was last updated.
     * Automatically updated on entity modification.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * The timestamp when the supplier record was logically deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * The current status of the supplier's account (e.g., ACTIVE, SUSPENDED, DELETED).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    /**
     * The contact email of the supplier.
     */
    @NotBlank
    @Column(name = "contact_email", nullable = false)
    private String email;

    /**
     * The contact phone number of the supplier.
     * Must be a valid phone number.
     */
    @NotBlank
    @ValidPhone
    @Column(name = "contact_phone", nullable = false)
    private String phone;

    /**
     * The URL of the supplier's image or logo.
     */
    @Column(name = "supplier_image")
    private String image;

    /**
     * The commission rate applied to products from this supplier.
     * Value ranges from 0.0 to 100.0.
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    @Column(name = "commission_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal commissionRate;

    /**
     * A list of users associated with this supplier.
     * Changes to the supplier cascade to its associated users.
     */
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupplierUser> supplierUsers = new ArrayList<>();

    /**
     * A list of products provided by this supplier.
     * Changes to the supplier cascade to its products.
     */
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Supplier() {
    }

    /**
     * Constructs a new Supplier with the specified details.
     *
     * @param name The name of the supplier.
     * @param cnpj The CNPJ of the supplier.
     * @param approved Whether the supplier is approved.
     * @param tier The supplier's tier.
     * @param dbUrl The supplier's database URL.
     * @param status The account status of the supplier.
     * @param email The contact email.
     * @param phone The contact phone number.
     * @param commissionRate The commission rate.
     */
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

    /**
     * Returns the unique identifier for the supplier.
     *
     * @return The ID of the supplier.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for the supplier.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the supplier.
     *
     * @return The supplier name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the supplier.
     *
     * @param name The supplier name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the CNPJ of the supplier.
     *
     * @return The CNPJ.
     */
    public String getCnpj() {
        return this.cnpj;
    }

    /**
     * Sets the CNPJ of the supplier.
     *
     * @param cnpj The CNPJ to set.
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    /**
     * Checks if the supplier is approved.
     *
     * @return True if approved, false otherwise.
     */
    public Boolean isApproved() {
        return this.approved;
    }

    /**
     * Returns the approval status of the supplier.
     *
     * @return The approval status.
     */
    public Boolean getApproved() {
        return this.approved;
    }

    /**
     * Sets the approval status of the supplier.
     *
     * @param approved The approval status to set.
     */
    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    /**
     * Returns the tier of the supplier.
     *
     * @return The SupplierTier enum.
     */
    public SupplierTier getTier() {
        return this.tier;
    }

    /**
     * Sets the tier of the supplier.
     *
     * @param tier The SupplierTier enum to set.
     */
    public void setTier(SupplierTier tier) {
        this.tier = tier;
    }

    /**
     * Returns the database URL of the supplier.
     *
     * @return The database URL.
     */
    public String getDbUrl() {
        return this.dbUrl;
    }

    /**
     * Sets the database URL of the supplier.
     *
     * @param dbUrl The database URL to set.
     */
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Returns the creation timestamp of the supplier record.
     *
     * @return The LocalDateTime of creation.
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Sets the creation timestamp of the supplier record.
     *
     * @param createdAt The LocalDateTime to set.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the last update timestamp of the supplier record.
     *
     * @return The LocalDateTime of last update.
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Sets the last update timestamp of the supplier record.
     *
     * @param updatedAt The LocalDateTime to set.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the deletion timestamp of the supplier record.
     *
     * @return The LocalDateTime of deletion.
     */
    public LocalDateTime getDeletedAt() {
        return this.deletedAt;
    }

    /**
     * Sets the deletion timestamp of the supplier record.
     *
     * @param deletedAt The LocalDateTime to set.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * Returns the account status of the supplier.
     *
     * @return The AccountStatus enum.
     */
    public AccountStatus getStatus() {
        return this.status;
    }

    /**
     * Sets the account status of the supplier.
     *
     * @param status The AccountStatus enum to set.
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * Returns the contact email of the supplier.
     *
     * @return The contact email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the contact email of the supplier.
     *
     * @param email The contact email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the contact phone number of the supplier.
     *
     * @return The contact phone number.
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * Sets the contact phone number of the supplier.
     *
     * @param phone The contact phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the commission rate of the supplier.
     *
     * @return The commission rate as a BigDecimal.
     */
    public BigDecimal getcommissionRate() {
        return this.commissionRate;
    }

    /**
     * Sets the commission rate of the supplier.
     *
     * @param commissionRate The commission rate to set.
     */
    public void setcommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    /**
     * Sets the ID and returns the current instance.
     *
     * @param id The ID to set.
     * @return The current Supplier instance.
     */
    public Supplier id(Long id) {
        setId(id);
        return this;
    }

    /**
     * Sets the name and returns the current instance.
     *
     * @param name The name to set.
     * @return The current Supplier instance.
     */
    public Supplier name(String name) {
        setName(name);
        return this;
    }

    /**
     * Sets the CNPJ and returns the current instance.
     *
     * @param cnpj The CNPJ to set.
     * @return The current Supplier instance.
     */
    public Supplier cnpj(String cnpj) {
        setCnpj(cnpj);
        return this;
    }

    /**
     * Sets the approved status and returns the current instance.
     *
     * @param approved The approved status to set.
     * @return The current Supplier instance.
     */
    public Supplier approved(Boolean approved) {
        setApproved(approved);
        return this;
    }

    /**
     * Sets the tier and returns the current instance.
     *
     * @param tier The tier to set.
     * @return The current Supplier instance.
     */
    public Supplier tier(SupplierTier tier) {
        setTier(tier);
        return this;
    }

    /**
     * Sets the database URL and returns the current instance.
     *
     * @param dbUrl The database URL to set.
     * @return The current Supplier instance.
     */
    public Supplier dbUrl(String dbUrl) {
        setDbUrl(dbUrl);
        return this;
    }

    /**
     * Sets the creation timestamp and returns the current instance.
     *
     * @param createdAt The creation timestamp to set.
     * @return The current Supplier instance.
     */
    public Supplier createdAt(LocalDateTime createdAt) {
        setCreatedAt(createdAt);
        return this;
    }

    /**
     * Sets the update timestamp and returns the current instance.
     *
     * @param updatedAt The update timestamp to set.
     * @return The current Supplier instance.
     */
    public Supplier updatedAt(LocalDateTime updatedAt) {
        setUpdatedAt(updatedAt);
        return this;
    }

    /**
     * Sets the deletion timestamp and returns the current instance.
     *
     * @param deletedAt The deletion timestamp to set.
     * @return The current Supplier instance.
     */
    public Supplier deletedAt(LocalDateTime deletedAt) {
        setDeletedAt(deletedAt);
        return this;
    }

    /**
     * Sets the account status and returns the current instance.
     *
     * @param status The account status to set.
     * @return The current Supplier instance.
     */
    public Supplier status(AccountStatus status) {
        setStatus(status);
        return this;
    }

    /**
     * Sets the email and returns the current instance.
     *
     * @param email The email to set.
     * @return The current Supplier instance.
     */
    public Supplier email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the phone number and returns the current instance.
     *
     * @param phone The phone number to set.
     * @return The current Supplier instance.
     */
    public Supplier phone(String phone) {
        setPhone(phone);
        return this;
    }

    /**
     * Sets the commission rate and returns the current instance.
     *
     * @param commissionRate The commission rate to set.
     * @return The current Supplier instance.
     */
    public Supplier commissionRate(BigDecimal commissionRate) {
        setcommissionRate(commissionRate);
        return this;
    }

    /**
     * Returns the list of users associated with this supplier.
     *
     * @return A List of SupplierUser objects.
     */
    public List<SupplierUser> getSupplierUsers() {
        return supplierUsers;
    }

    /**
     * Sets the list of users associated with this supplier.
     *
     * @param supplierUsers The List of SupplierUser objects to set.
     */
    public void setSupplierUsers(List<SupplierUser> supplierUsers) {
        this.supplierUsers = supplierUsers;
    }

    /**
     * Returns the list of products provided by this supplier.
     *
     * @return A List of Product objects.
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Sets the list of products provided by this supplier.
     *
     * @param products The List of Product objects to set.
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * Compares this Supplier object with another object for equality.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
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

    /**
     * Returns the hash code for this Supplier object.
     *
     * @return The hash code.
     */
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

    /**
     * Returns the URL of the supplier's image or logo.
     *
     * @return The image URL string.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the URL of the supplier's image or logo.
     *
     * @param image The image URL to set.
     */
    public void setImage(String image) {
        this.image = image;
    }
}
