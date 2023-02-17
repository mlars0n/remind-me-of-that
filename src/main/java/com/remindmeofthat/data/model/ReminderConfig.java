package com.remindmeofthat.data.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "reminder_config")
@EntityListeners(AuditingEntityListener.class)
public class ReminderConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "recurring")
    private boolean recurring;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ReminderUser reminderUser;

    @OneToMany(mappedBy="reminderConfig", cascade = {CascadeType.ALL, CascadeType.MERGE})
    private Set<Reminder> reminders;

    // Getters and setters for the fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public ReminderUser getReminderUser() {
        return reminderUser;
    }

    public void setReminderUser(ReminderUser reminderUser) {
        this.reminderUser = reminderUser;
    }

    @Column(name ="start_date", nullable = false)
    private OffsetDateTime startDate;

    @Column(name ="created_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name ="last_modified_date", nullable = true)
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Set<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(Set<Reminder> reminders) {
        this.reminders = reminders;
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

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "ReminderConfig{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", recurring=" + recurring +
                ", reminderUser=" + reminderUser +
                ", reminders=" + reminders +
                ", startDate=" + startDate +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}



