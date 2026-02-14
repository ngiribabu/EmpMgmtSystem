package com.empmgmt.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "CREATEDAT", updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATEDAT")
    private Timestamp updatedAt;

    @Column(name = "CREATEDBY", updatable = false, length = 30)
    private String createdBy;

    @Column(name = "UPDATEDBY", length = 30)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = "WEBAPP";
        this.updatedBy = "WEBAPP";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.updatedBy = "WEBAPP";
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
