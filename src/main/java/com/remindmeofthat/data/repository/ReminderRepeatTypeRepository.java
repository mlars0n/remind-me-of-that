package com.remindmeofthat.data.repository;

import com.remindmeofthat.data.model.ReminderRepeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepeatTypeRepository extends JpaRepository<ReminderRepeatType, Long> {

    ReminderRepeatType findReminderRepeatTypeByKey(String never);
}
