package com.recipely.model;

import java.time.LocalDateTime;

/** Shared identity and audit fields for persistent application objects. */
public abstract class BaseEntity {
    private long id;
    private LocalDateTime createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
