package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_name", nullable = false, length = 150)
    private String entityName;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionType actionType;

    @Column(name = "saved_by", nullable = false, length = 200)
    private String savedBy;

    @Lob
    @Column(name = "snapshot_json", columnDefinition = "LONGTEXT", nullable = false)
    private String snapshotJson;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // ===========================
    // Construtores
    // ===========================
    public AuditLog() {
    }

    public AuditLog(String entityName, Long entityId, ActionType actionType, String savedBy, String snapshotJson) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.actionType = actionType;
        this.savedBy = savedBy;
        this.snapshotJson = snapshotJson != null ? snapshotJson : "{}";
    }

    // ===========================
    // Getters e Setters
    // ===========================
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getSavedBy() {
        return this.savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public String getSnapshotJson() {
        return this.snapshotJson;
    }

    public void setSnapshotJson(String snapshotJson) {
        this.snapshotJson = snapshotJson;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // ===========================
    // equals() e hashCode()
    // ===========================
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ===========================
    // toString()
    // ===========================
    @Override
    public String toString() {
        return "AuditLog{"
                + "id=" + id
                + ", entityName='" + entityName + '\''
                + ", entityId=" + entityId
                + ", actionType=" + actionType
                + ", savedBy='" + savedBy + '\''
                + ", timestamp=" + timestamp
                + ", snapshotSize=" + (snapshotJson != null ? snapshotJson.length() : 0)
                + '}';
    }
}