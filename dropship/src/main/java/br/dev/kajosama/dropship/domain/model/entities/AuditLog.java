package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDateTime;

import br.dev.kajosama.dropship.domain.model.enums.ActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Represents an audit log entry for tracking changes to entities.
 * Each entry records the entity name, its ID, the type of action performed,
 * the user who performed the action, a JSON snapshot of the entity's state,
 * and the timestamp of the action.
 */
@Entity
@Table(name = "audit_log")
public class AuditLog {

    /**
     * The unique identifier for the audit log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the entity that was audited.
     */
    @Column(name = "entity_name", nullable = false, length = 150)
    private String entityName;

    /**
     * The ID of the entity that was audited.
     */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /**
     * The type of action performed on the entity (e.g., CREATE, UPDATE, DELETE).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionType actionType;

    /**
     * The identifier of the user who performed the action.
     */
    @Column(name = "saved_by", nullable = false, length = 200)
    private String savedBy;

    /**
     * A JSON snapshot of the entity's state at the time of the audit.
     * Stored as a large object (LONGTEXT in database).
     */
    @Lob
    @Column(name = "snapshot_json", columnDefinition = "LONGTEXT")
    private String snapshotJson;

    /**
     * The timestamp when the audit log entry was created.
     */
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Default constructor required by JPA.
     */
    public AuditLog() {
    }

    /**
     * Constructs a new AuditLog entry with the specified details.
     *
     * @param entityName   The name of the audited entity.
     * @param entityId     The ID of the audited entity.
     * @param actionType   The type of action performed.
     * @param savedBy      The user who performed the action.
     * @param snapshotJson A JSON representation of the entity's state.
     */
    public AuditLog(String entityName, Long entityId, ActionType actionType, String savedBy, String snapshotJson) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.actionType = actionType;
        this.savedBy = savedBy;
        this.snapshotJson = snapshotJson;
    }

    /**
     * Returns the unique identifier for the audit log entry.
     * 
     * @return The ID of the audit log entry.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the audit log entry.
     * 
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the entity that was audited.
     * 
     * @return The entity name.
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the name of the entity that was audited.
     * 
     * @param entityName The entity name to set.
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Returns the ID of the entity that was audited.
     * 
     * @return The entity ID.
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets the ID of the entity that was audited.
     * 
     * @param entityId The entity ID to set.
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Returns the type of action performed on the entity.
     * 
     * @return The action type.
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * Sets the type of action performed on the entity.
     * 
     * @param actionType The action type to set.
     */
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    /**
     * Returns the identifier of the user who performed the action.
     * 
     * @return The user identifier.
     */
    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    /**
     * Returns the JSON snapshot of the entity's state.
     * 
     * @return The JSON snapshot.
     */
    public String getSnapshotJson() {
        return snapshotJson;
    }

    /**
     * Sets the JSON snapshot of the entity's state.
     * 
     * @param snapshotJson The JSON snapshot to set.
     */
    public void setSnapshotJson(String snapshotJson) {
        this.snapshotJson = snapshotJson;
    }

    /**
     * Returns the timestamp when the audit log entry was created.
     * 
     * @return The timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the audit log entry was created.
     * 
     * @param timestamp The timestamp to set.
     */

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}