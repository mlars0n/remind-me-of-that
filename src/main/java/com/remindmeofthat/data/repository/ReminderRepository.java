package com.remindmeofthat.data.repository;

import com.remindmeofthat.data.model.Reminder;
import com.remindmeofthat.data.model.ReminderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    @Query("delete from Reminder r where r.sent = false")
    @Modifying
    void deleteAllNotSentByReminderConfig(ReminderConfig reminderConfig);
}
