package com.remindmeofthat.service;

import com.remindmeofthat.data.model.Reminder;
import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.model.ReminderRepeatType;
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

        //Calculate the ZoneOffset
        ZoneOffset zoneOffset = ZoneOffset.ofHours(timeZoneOffset);

        //If this is a "Never" reminder, then make the end date the same as the start date, because the end date will come in not set
        if (reminderConfig.getReminderRepeatType().getKey().equalsIgnoreCase(ReminderRepeatType.RepeatTypeKey.NEVER.getKey())) {
            reminderConfig.setEndDate(reminderConfig.getStartDate());
        }

        //Make sure this is default enabled
        reminderConfig.setEnabled(true);

        //Create reminders (single or recurring)
        createReminders(reminderConfig, zoneOffset);

        //Create a recurring reminder if one is called for
/*        if (!reminderConfig.getReminderRepeatType().getKey().equalsIgnoreCase(ReminderRepeatType.RepeatTypeKey.NEVER.getKey())) {
            createRepeatingReminder(reminderConfig, zoneOffset);
        } else { //Else just create a single reminder
            Reminder reminder = createSingleReminder(reminderConfig, reminderConfig.getStartDate(), zoneOffset);

            Set<Reminder> reminderSet = new HashSet<>(Arrays.asList(reminder));

            saveReminderConfig(reminderConfig, zoneOffset, reminderSet);
        }*/
    }

    public void createReminders(ReminderConfig reminderConfig, ZoneOffset zoneOffset) {
        //Create repeating reminders according to the period defined in the reminderRepeatType

        //Create a set to hold the reminders
        Set<Reminder> reminderSet = new HashSet<>();

        //Algorithm for creating the reminders. This works for all cases because the "Never" case is
        //handled by setting the end date to the start date in the code and then adding 0 days to the start date the
        //first time through the loop. It is also important that the reminder time (what hour/min it will be sent out)
        // is added only after it has been determined that the reminder is within the start and end date range.
        //The end date is inclusive (i.e. we should have a reminder sent out on the end date if one would normally be scheduled for that
        //date, as in the case of daily reminders
        boolean finished = false;
        for (int i = 0; !finished; i++) {

            //Calculate this reminder's date/time based on the start date and the period
            OffsetDateTime reminderDateTime = null;

            //NEVER needs special handling to create it on the first time through the loop
            if (reminderConfig.getReminderRepeatType().getKey().equals(ReminderRepeatType.RepeatTypeKey.NEVER.getKey())) {

                //Use plus days so we always get a date in the future the second time around when i > 0
                reminderDateTime = reminderConfig.getStartDate().plusDays(i);

            } else if (reminderConfig.getReminderRepeatType().getKey().equals(ReminderRepeatType.RepeatTypeKey.DAILY.getKey())) {
                reminderDateTime = reminderConfig.getStartDate().plusDays(i);
            } else if (reminderConfig.getReminderRepeatType().getKey().equals(ReminderRepeatType.RepeatTypeKey.WEEKLY.getKey())) {
                reminderDateTime = reminderConfig.getStartDate().plusWeeks(i);
            } else if (reminderConfig.getReminderRepeatType().getKey().equals(ReminderRepeatType.RepeatTypeKey.MONTHLY.getKey())) {
                reminderDateTime = reminderConfig.getStartDate().plusMonths(i);
            } else if (reminderConfig.getReminderRepeatType().getKey().equals(ReminderRepeatType.RepeatTypeKey.YEARLY.getKey())) {
                reminderDateTime = reminderConfig.getStartDate().plusYears(i);
            }  else { //Log an error here and bail out of this loop
                logger.error("Reminder repeat type [{}] is not supported", reminderConfig.getReminderRepeatType().getKey());
                finished = true;
            }

            logger.debug("Checking whether to create reminder for reminder config [{}] with reminder date/time of [{}]", reminderConfig, reminderDateTime);

            //Check whether we should create this reminder or not
            if (reminderDateTime.isAfter(reminderConfig.getEndDate())) {
                finished = true;
            }

            //Create the reminder if we are not done
            if (!finished) {
                Reminder reminder = createSingleReminder(reminderConfig, reminderDateTime, zoneOffset);

                //Add the reminder to the set
                reminderSet.add(reminder);
            }
        }

        //Save it all into the DB
        saveReminderConfig(reminderConfig, zoneOffset, reminderSet);
    }

    private Reminder createSingleReminder(ReminderConfig reminderConfig, OffsetDateTime reminderDateTime, ZoneOffset zoneOffset) {
        //Create a single reminder
        Reminder reminder = new Reminder();
        reminder.setSent(false);
        reminder.setReminderConfig(reminderConfig);

        //Add two hours to OffsetDateTime to get to 2:00am
        //TODO randomize the time as a time before 00:00 and 04:00
        OffsetDateTime offsetDateTimeForReminder = reminderDateTime.plusHours(2);

        //Set the reminder date/time and created/modified times
        reminder.setReminderTime(offsetDateTimeForReminder);
        reminder.setCreatedDate(OffsetDateTime.now(zoneOffset));
        reminder.setLastModifiedDate(OffsetDateTime.now(zoneOffset));

        logger.debug("Saving reminder config [{}] with reminder date/time of [{}]", reminderConfig, offsetDateTimeForReminder);logger.debug("Saving reminder config [{}] with reminder date/time of [{}]", reminderConfig, offsetDateTimeForReminder);

        return reminder;
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