package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplier_user")
public class SupplierUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_user_id")
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "association_date")
    @CreationTimestamp
    private LocalDateTime associationDate;

    public SupplierUser() {
    }

    public SupplierUser(Long id, Supplier supplier, User user, LocalDateTime associationDate) {
        this.id = id;
        this.supplier = supplier;
        this.user = user;
        this.associationDate = associationDate;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getAssociationDate() {
        return this.associationDate;
    }

    public void setAssociationDate(LocalDateTime associationDate) {
        this.associationDate = associationDate;
    }

    public SupplierUser id(Long id) {
        setId(id);
        return this;
    }

    public SupplierUser supplier(Supplier supplier) {
        setSupplier(supplier);
        return this;
    }

    public SupplierUser user(User user) {
        setUser(user);
        return this;
    }

    public SupplierUser associationDate(LocalDateTime associationDate) {
        setAssociationDate(associationDate);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SupplierUser)) {
            return false;
        }
        SupplierUser supplierUser = (SupplierUser) o;
        return Objects.equals(id, supplierUser.id) && Objects.equals(supplier, supplierUser.supplier) && Objects.equals(user, supplierUser.user) && Objects.equals(associationDate, supplierUser.associationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, supplier, user, associationDate);
    }

    @Override
    public String toString() {
        return "{"
                + " id='" + getId() + "'"
                + ", supplier='" + getSupplier() + "'"
                + ", user='" + getUser() + "'"
                + ", associationDate='" + getAssociationDate() + "'"
                + "}";
    }

}