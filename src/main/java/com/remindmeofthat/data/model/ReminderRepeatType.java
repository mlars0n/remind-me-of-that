package com.remindmeofthat.data.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reminder_repeat_type")
public class ReminderRepeatType extends BaseEntityZonedDates {

    //Create an enum to track the keys for the repeat types
    public enum RepeatTypeKey {
        NEVER("NEVER"),
        DAILY("DAILY"),
        WEEKLY("WEEKLY"),
        MONTHLY("MONTHLY"),
        YEARLY("YEARLY");

        private String key;

        RepeatTypeKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "description", nullable = true)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReminderRepeatType that = (ReminderRepeatType) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}


