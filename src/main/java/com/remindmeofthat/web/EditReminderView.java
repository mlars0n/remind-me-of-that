package com.remindmeofthat.web;

import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.model.ReminderRepeatType;
import com.remindmeofthat.data.model.ReminderUser;
import com.remindmeofthat.data.repository.ReminderConfigRepository;
import com.remindmeofthat.data.repository.ReminderRepeatTypeRepository;
import com.remindmeofthat.data.repository.ReminderUserRepository;
import com.remindmeofthat.service.ReminderManagementService;
import com.remindmeofthat.service.ReminderUserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalQueries;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Route(value = "edit/:linkId", layout = ParentLayoutView.class)
@PageTitle("Home")
public class EditReminderView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(EditReminderView.class);

    private final ReminderUserService reminderService;
    private final ReminderUserRepository reminderUserRepository;

    private final ReminderConfigRepository reminderConfigRepository;

    private final ReminderManagementService reminderManagementService;

    private final ReminderRepeatTypeRepository reminderRepeatTypeRepository;

    private String linkId;
    private ReminderUser reminderUser;

    private int timeZoneOffset;

    private ReminderConfig reminderConfig; // The current reminder config being worked on

    //Form fields that we want to bind and validate
    private BeanValidationBinder<ReminderConfig> binder = new BeanValidationBinder<>(ReminderConfig.class);

    Dialog editReminderConfigDialog = new Dialog();

    Paragraph dialogueTimeZoneNotification = new Paragraph();
    
    TextField subject = new TextField("Headline");
    TextArea body = new TextArea("Body");

    Select<ReminderRepeatType> reminderRepeatType = new Select<>();

    DatePicker startDate = new DatePicker();// Value of label is set below

    DatePicker endDate = new DatePicker("Reminder End Date");

    Button save = VaadinConstants.saveButton();
    Button cancel = VaadinConstants.cancelButton();
    Button delete = VaadinConstants.deleteButton();

    //Grid setup
    Grid<ReminderConfig> remindersGrid = new Grid<>();

    public EditReminderView(@Autowired ReminderUserService reminderService, @Autowired ReminderUserRepository reminderUserRepository,
                            @Autowired ReminderConfigRepository reminderConfigRepository, @Autowired ReminderManagementService reminderManagementService,
                            @Autowired ReminderRepeatTypeRepository reminderRepeatTypeRepository) {
        this.reminderService = reminderService;
        this.reminderUserRepository = reminderUserRepository;
        this.reminderConfigRepository = reminderConfigRepository;
        this.reminderManagementService = reminderManagementService;
        this.reminderRepeatTypeRepository = reminderRepeatTypeRepository;

        //Get the extended client side details
        UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
            timeZoneOffset = details.getTimezoneOffset() / 60 / 60 / 1000;

            ZoneOffset offset = ZoneOffset.ofHours(timeZoneOffset);
            ZoneId zoneId = offset.getRules().getOffset(Instant.now()).query(TemporalQueries.zone());
            String displayName = zoneId.getDisplayName(TextStyle.FULL, Locale.getDefault());

            logger.debug("Your time zone offset: " + offset.getId());

            //dialogueTimeZoneNotification.setText("Your time zone is [" + displayName + "]");

        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //TODO need all kinds of error checking here (format of the linkId, what it looks like, etc. to
        // prevent brute force type attacks)
        Optional<String> linkIdOptional = event.getRouteParameters().get("linkId");
        if (linkIdOptional.isPresent()) {
            linkId = linkIdOptional.get();
        }

        logger.debug("Edit ID parameter is [{}]", linkId);

        //TODO check on link ID expiration and disallow access if it is too old

        //Find the user record for this ID
        var reminderUserOptional = reminderUserRepository.findReminderUserByLinkId(linkId);

        //This user is found so display the edit page with any current reminders on it
        if (reminderUserOptional.isPresent()) {
            logger.debug("Reminder user is [{}]", reminderUserOptional);

            reminderUser = reminderUserOptional.get();

            completeSetup();
        } else {
            logger.debug("Reminder user could not be found");

            //TODO return a 404 page here long term
        }

    }

    private void completeSetup() {

        //Create the popup dialog (and set up the binder for it)
        createDialog();

        //Create the reminders grid
        createGrid();

        //Add the add reminder button
        var addReminderButton = new Button("Add Reminder", new Icon(VaadinIcon.PLUS));
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.setHorizontalComponentAlignment(Alignment.CENTER, addReminderButton);
        addReminderButton.addClickListener(event -> {
            //TODO check whether we are editing or adding but for now just add a new one
            ReminderRepeatType neverRepeat = reminderRepeatTypeRepository.findReminderRepeatTypeByKey("NEVER");
            this.reminderConfig = new ReminderConfig();
            reminderConfig.setReminderRepeatType(neverRepeat);
            binder.setBean(reminderConfig);
            delete.setVisible(false);
            editReminderConfigDialog.open();
        });

        setSizeFull();
        add(addReminderButton, new H4("Your Reminders"), remindersGrid, editReminderConfigDialog);
    }
    private void createGrid() {
        //remindersGrid.addColumn(createToggleDetailsRenderer()).setWidth("7em").setFlexGrow(0);
        remindersGrid.addColumn(item -> item.getSubject()).setHeader("Headline").setKey("headline");
        remindersGrid.setItems(reminderConfigRepository.findReminderConfigByReminderUser(reminderUser));

        /*remindersGrid.addSelectionListener(item -> {
            //logger.debug("Using ReminderConfig item [{}]", item);

            if (item.getFirstSelectedItem().isPresent()) {
                binder.setBean(item.getFirstSelectedItem().get());
            }
            editReminderConfigDialog.open();
        });*/

        remindersGrid.addColumn(reminderConfig -> reminderConfig.getStartDate()).setHeader("Start Date").setKey("startDate");
        remindersGrid.addColumn(reminderConfig -> reminderConfig.getCreatedDate()).setHeader("Created Date").setKey("createdDate");

        //Add the edit button
        remindersGrid.addComponentColumn(reminderConfig -> {
            Button editRowButton = VaadinConstants.editButton();

            editRowButton.addClickListener(event -> {
                binder.setBean(reminderConfig);
                editReminderConfigDialog.open();
            });

            return editRowButton;
        }).setHeader("Edit");

        //remindersGrid.setWidthFull();
        remindersGrid.setSizeFull();

        //Set up the details renderer
        /*remindersGrid.setItemDetailsRenderer(
                new ComponentRenderer<>(reminderConfig -> {
                    //VerticalLayout reminderDetailsLayout = new VerticalLayout();
                    Paragraph paragraph = new Paragraph();
                    paragraph.add(reminderConfig.getBody());

                    return paragraph;
                }));*/

        //Turn off the selection mode
        remindersGrid.setSelectionMode(Grid.SelectionMode.NONE);
    }

    private TemplateRenderer<ReminderConfig> createToggleDetailsRenderer() {
        return TemplateRenderer.<ReminderConfig>of("<vaadin-button theme=\"secondary\" on-click=\"handleClick\">+</vaadin-button>")
                .withEventHandler("handleClick",
                        reminderConfig -> remindersGrid.setDetailsVisible(reminderConfig, !remindersGrid.isDetailsVisible(reminderConfig)));
    }

    private void createDialog() {
        //Capture the form in a vertical layout
        VerticalLayout dialogLayout = new VerticalLayout();

        //Make sure you can resize the dialog
        editReminderConfigDialog.setResizable(true);

        //Make it non modal
        editReminderConfigDialog.setCloseOnOutsideClick(false);

        //Create the component sizes
        subject.setWidthFull();
        body.setWidthFull();
        body.setHeight("250px");

        //Make this field resizable
        body.getStyle().set("resize", "both");
        body.getStyle().set("overflow", "auto");

        //Set the date formats allowed
        DatePicker.DatePickerI18n multiFormatI18n = new DatePicker.DatePickerI18n();
        multiFormatI18n.setDateFormats("yyyy-MM-dd", "MM/dd/yyyy","dd.MM.yyyy");
        startDate.setI18n(multiFormatI18n);
        endDate.setI18n(multiFormatI18n);

        //Size the date fields
        startDate.setWidth("13em");
        endDate.setWidth("13em");

        //Date picker to stop sending the reminder
        startDate.setRequired(true);
        endDate.setEnabled(false);

        //Make a horizontal layout for the recurring part
        HorizontalLayout recurringLayout = new HorizontalLayout();

        //Set up the recurring dropdown list
        reminderRepeatType.setLabel("Repeat Reminder");
        reminderRepeatType.setWidth("13em");
        List<ReminderRepeatType> reminderRepeatTypes = reminderRepeatTypeRepository.findAll();
        ReminderRepeatType neverRepeat = reminderRepeatTypeRepository.findReminderRepeatTypeByKey("NEVER");
        reminderRepeatType.setItemLabelGenerator(ReminderRepeatType::getName);
        reminderRepeatType.setItems(reminderRepeatTypes);

        //If we change the reminder repeat type, change up the other values on this page
        reminderRepeatType.addValueChangeListener(event -> {
            if (event.getValue() != null && event.getValue().equals(neverRepeat)) {
                startDate.setLabel("Reminder Date");
                endDate.setEnabled(false);
                endDate.setRequired(false);
                endDate.setValue(null);
            } else {
                startDate.setLabel("Reminder Start Date");
                endDate.setEnabled(true);
                endDate.setRequired(true);
            }
        });

        recurringLayout.add(startDate, reminderRepeatType, endDate);
        recurringLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        //Set tooltip text on the Date picker
        Tooltip.forComponent(dialogueTimeZoneNotification)
                .withText("Reminders will be sent out between 00:00 and 04:00 hours in your local timezone")
                .withPosition(Tooltip.TooltipPosition.TOP_START);

        //Create the binder for the start date field, which requires a converter. The first lambda converts the UI value
        //to the model value. The second lambda converts the model value to the UI value. Note that this has to be done in a way
        //that takes into account the time zone offset and allows that to be passed in as a parameter, i.e. the converter needs
        //access to that parameter

        String dateFormatErrorMessage = "Invalid date. Formats allowed: yyyy-MM-dd, MM/dd/yyyy,dd.MM.yyyy";
        binder.forField(startDate)
                .withConverter(this::convertLocalDateToOffsetDateTime, this::convertOffsetDateTimeToLocalDate,
                        dateFormatErrorMessage)
                .withValidator(userOffsetDateTime -> {
                    //Get today's time in the user's time zone at 00:00:00
                    ZoneOffset zoneOffset = ZoneOffset.ofHours(timeZoneOffset);
                    OffsetDateTime offsetDateTimePlusOneDay = OffsetDateTime.of(LocalDateTime.now(), zoneOffset)
                            .withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);

                    //If the date isn't at least tomorrow's date, return false
                    if (userOffsetDateTime == null || userOffsetDateTime.isBefore(offsetDateTimePlusOneDay)) {
                        return false;
                    }

                    return true;
                }, "Date needs to be tomorrow or later")
                .bind(ReminderConfig::getStartDate, ReminderConfig::setStartDate);

        binder.forField(endDate)
                .withConverter(localDateFromUi -> {
                            //If the end date is not enabled, return null
                            if (!endDate.isEnabled()) {
                                return null;
                            } else {
                                return convertLocalDateToOffsetDateTime(localDateFromUi);
                            }
                        },
                        this::convertOffsetDateTimeToLocalDate,
                        dateFormatErrorMessage)
                .withValidator(userOffsetDateTime -> {

                    //Always return true if the end date is not enabled
                    if (!endDate.isEnabled() || startDate.getValue() == null) {
                        return true;
                    }

                    OffsetDateTime convertedStartDate = convertLocalDateToOffsetDateTime(startDate.getValue());

                    //If the date isn't at least tomorrow's date, return false
                    if (userOffsetDateTime == null || userOffsetDateTime.isBefore(convertedStartDate)) {
                        return false;
                    }

                    return true;
                }, "End date must be later than start date")
                .bind(ReminderConfig::getEndDate, ReminderConfig::setEndDate);;

        //Complete the bean validation binder setup
        binder.bindInstanceFields(this);

        dialogLayout.add(subject, body, recurringLayout, createDialogButtons());
        editReminderConfigDialog.add(dialogLayout);
    }

    private HorizontalLayout createDialogButtons() {
        HorizontalLayout buttonActions = new HorizontalLayout();

        //Put delete over on the right side
        buttonActions.setSizeFull();
        delete.getStyle().set("margin-left", "auto");

        //Cancel action
        cancel.addClickListener(event -> {
            editReminderConfigDialog.close();
        });

        //Save action
        save.addClickListener(event -> {
            try {

                //Write this back to the binder object
                binder.writeBean(reminderConfig);

                //Set the user that you got from the ID
                reminderConfig.setReminderUser(reminderUser);

                // Get the local date and time from the DateTimePicker
                LocalDate localDate = startDate.getValue();

                //Save this all to the database with the correct logic
                reminderManagementService.createRemindersWithRandomTime(reminderConfig, timeZoneOffset);

                //Clear out the ReminderConfig object for clarity
                reminderConfig = new ReminderConfig();

                //Update the grid showing the list of reminders
                remindersGrid.setItems(reminderConfigRepository.findReminderConfigByReminderUser(reminderUser));

                //Close the dialog and update the items in the list
                editReminderConfigDialog.close();

            } catch (ValidationException e) {

                //TODO make sure the validation errors are handled sensibly for clients
                logger.warn("Validation error saving environment form");
            }
        });

        buttonActions.add(save, cancel, delete);

        return buttonActions;
    }

    /**
     * Convert the date from the UI to an OffsetDateTime object with the correct time zone offset and a time of 00:00:00
     * @param localDateFromUi
     * @return new offsetDateTime
     */
    private OffsetDateTime convertLocalDateToOffsetDateTime(LocalDate localDateFromUi) {
        logger.debug("Creating OffsetDateTime from local date [{}] with offset [{}]", localDateFromUi, timeZoneOffset);

        //Otherwise check for errors and return the value if there are no errors
        LocalTime localTime = LocalTime.of(0, 0, 0);
        LocalDateTime localDateTime = LocalDateTime.of(localDateFromUi, localTime);

        // Convert the local date and time to an OffsetDateTime object with the correct time zone offset
        ZoneOffset zoneOffset = ZoneOffset.ofHours(timeZoneOffset);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);
        return offsetDateTime;
    }

    private LocalDate convertOffsetDateTimeToLocalDate(OffsetDateTime modelOffsetDateToUi) {

        //Convert the OffsetDateTime object to a local date and time if it is not null
        //If it is null, it's because it's a new reminder and we don't have a date yet
        if (modelOffsetDateToUi == null) {
            return null;
        }

        LocalDateTime localDateTime = modelOffsetDateToUi.toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }
}
