package com.remindmeofthat.data.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

/**
 * Class to hold common fields for all entities
 */
@MappedSuperclass
public class BaseEntity {
    @Column(name ="created_date", nullable = false, updatable = false)
    protected OffsetDateTime createdDate;

    @Column(name ="last_modified_date", nullable = false)
    protected OffsetDateTime lastModifiedDate;

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
