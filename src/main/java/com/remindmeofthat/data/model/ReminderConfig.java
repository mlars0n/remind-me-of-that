package com.remindmeofthat.data.model;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "reminder_config")
public class ReminderConfig extends BaseEntityZonedDates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name ="start_date", nullable = false)
    private OffsetDateTime startDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ReminderUser reminderUser;

    @ManyToOne
    @JoinColumn(name = "reminder_repeat_type_id", nullable = false)
    private ReminderRepeatType reminderRepeatType;

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

    public ReminderUser getReminderUser() {
        return reminderUser;
    }

    public void setReminderUser(ReminderUser reminderUser) {
        this.reminderUser = reminderUser;
    }

    public Set<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(Set<Reminder> reminders) {
        this.reminders = reminders;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public ReminderRepeatType getReminderRepeatType() {
        return reminderRepeatType;
    }

    public void setReminderRepeatType(ReminderRepeatType reminderRepeatType) {
        this.reminderRepeatType = reminderRepeatType;
    }

    @Override
    public String toString() {
        return "ReminderConfig{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", startDate=" + startDate +
                ", reminderUser=" + reminderUser +
                ", reminderRepeatType=" + reminderRepeatType +
                ", reminders=" + reminders +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}



