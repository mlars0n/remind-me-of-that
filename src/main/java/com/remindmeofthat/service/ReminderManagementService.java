package com.remindmeofthat.service;

import com.remindmeofthat.data.model.Reminder;
import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.repository.ReminderConfigRepository;
import com.remindmeofthat.data.repository.ReminderRepository;
import com.remindmeofthat.data.repository.ReminderUserRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to handle reminder creation and other reminder services
 */
@Service
public class ReminderManagementService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderManagementService.class);

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
    @Transactional
    public void createNewRemindersWithRandomTime(ReminderConfig reminderConfig, int timeZoneOffset) {

        ZoneOffset zoneOffset = ZoneOffset.ofHours(timeZoneOffset);

        //Create a recurring reminder if one is called for
        if (!reminderConfig.getReminderRepeatType().getKey().equalsIgnoreCase("NEVER")) {
            createRepeatingReminder(reminderConfig, zoneOffset);
        } else { //Else just create a single reminder
            Reminder reminder = new Reminder();
            reminder.setSent(false);
            reminder.setReminderConfig(reminderConfig);

            //Add two hours to OffsetDateTime to get to 2:00am
            //TODO randomize the time as a time before 00:00 and 04:00
            OffsetDateTime offsetDateTimeForReminder = reminderConfig.getStartDate().plusHours(2);

            //Set the reminder date/time and created/modified times
            reminder.setReminderTime(offsetDateTimeForReminder);
            reminder.setCreatedDate(OffsetDateTime.now(zoneOffset));
            reminder.setLastModifiedDate(OffsetDateTime.now(zoneOffset));

            Set<Reminder> reminderSet = new HashSet<>(Arrays.asList(reminder));

            logger.debug("Saving reminder config [{}] with reminder date/time of [{}]", reminderConfig, offsetDateTimeForReminder);

            saveReminderConfig(reminderConfig, zoneOffset, reminderSet);

        }
    }

    public void createRepeatingReminder(ReminderConfig reminderConfig, ZoneOffset zoneOffset) {
        //TODO create repeating reminders according to the reminderRepeatType

        //Save it all into the DB
        saveReminderConfig(reminderConfig, zoneOffset, new HashSet<>());
    }

    private void saveReminderConfig(@NotNull ReminderConfig reminderConfig, ZoneOffset zoneOffset, Set<Reminder> reminderSet) {

        //Set the right audit dates into the reminder config
        reminderConfig.setCreatedDate(OffsetDateTime.now(zoneOffset));
        reminderConfig.setLastModifiedDate(OffsetDateTime.now(zoneOffset));

        //Set the new reminder(s) into the set
        reminderConfig.setReminders(reminderSet);

        //Save it all into the DB
        reminderConfigRepository.save(reminderConfig);
    }

    /**
     * Method to encompass the logic that updates reminders by deleting the unsent reminders and recreating them
     * @param reminderConfig
     * @param timeZoneOffset
     */
    @Transactional
    public void updateRemindersWithRandomTime(ReminderConfig reminderConfig, int timeZoneOffset) {
        //Delete all unsent reminders for this reminder config, because we are going to recreate them
        reminderRepository.deleteAllNotSentByReminderConfig(reminderConfig);

        //Now recreate the reminders
        createNewRemindersWithRandomTime(reminderConfig, timeZoneOffset);
    }
}