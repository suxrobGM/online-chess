package com.silyosbekov.chessmate.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * AuditableEntity class. Represents an entity with audit fields.
 * It provides an id field for all entities.
 * It also provides fields for the creation and update dates and the user who created and updated the entity.
 * The id is a UUID and is generated automatically when the entity is created in the database.
 */
@MappedSuperclass
public abstract class AuditableEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Instant updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * Gets the entity's id.
     * @return The entity's id.
     */
    public UUID getId() {
        return id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(final Instant updatedAt) {
        this.updatedDate = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
