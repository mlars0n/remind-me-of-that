package com.remindmeofthat.data.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reminder")
public class Reminder extends BaseEntity {
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
}
