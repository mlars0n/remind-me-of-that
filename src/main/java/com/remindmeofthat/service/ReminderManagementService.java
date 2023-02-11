package com.remindmeofthat.service;

import com.remindmeofthat.data.model.Reminder;
import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.model.ReminderUser;
import com.remindmeofthat.data.repository.ReminderConfigRepository;
import com.remindmeofthat.data.repository.ReminderRepository;
import com.remindmeofthat.data.repository.ReminderUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Service to handle reminder creation and other reminder services
 */
@Service
public class ReminderManagementService {

    private ReminderUserRepository reminderUserRepository;
    private ReminderConfigRepository reminderConfigRepository;
    private ReminderRepository reminderRepository;

    public ReminderManagementService(@Autowired ReminderConfigRepository reminderConfigRepository,
                                     @Autowired ReminderUserRepository reminderUserRepository,
                                     @Autowired ReminderRepository reminderRepository) {
        this.reminderConfigRepository = reminderConfigRepository;
        this.reminderUserRepository = reminderUserRepository;
        this.reminderRepository = reminderRepository;
    }

    /**
     * Method to encompass the logic that creates reservations
     * @param reminderConfig
     */
    public void createReminders(ReminderConfig reminderConfig, OffsetDateTime offsetDateTime) {

        //Create a recurring reminder if one is called for
        if (reminderConfig.getRecurring()) {
            //TODO this might need to be
            createRepeatingReminder(reminderConfig);
        } else { //Else just create a single reminder
            Reminder reminder = new Reminder();
            reminder.setSent(false);
            reminder.setReminderConfig(reminderConfig);
            reminder.setReminderTime(offsetDateTime);
            reminderRepository.save(reminder);
        }
    }

    public void createRepeatingReminder(ReminderConfig reminderConfig) {
        //TODO create repeating reminders
    }
}