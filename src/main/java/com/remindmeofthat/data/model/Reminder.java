package com.remindmeofthat.data.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reminder")
@EntityListeners(AuditingEntityListener.class)
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reminder_time")
    private OffsetDateTime reminderTime;

    @Column(name = "sent", nullable = false)
    private boolean sent;

    @ManyToOne
    @JoinColumn(name = "reminder_config_id", nullable = false)
    private ReminderConfig reminderConfig;

    @Column(name ="created_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name ="last_modified_date", nullable = true)
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    // Getters and setters for the fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(OffsetDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public ReminderConfig getReminderConfig() {
        return reminderConfig;
    }

    public void setReminderConfig(ReminderConfig reminderConfig) {
        this.reminderConfig = reminderConfig;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
