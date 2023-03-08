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
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
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
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataView;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Route(value = "edit/:linkId", layout = ParentLayoutView.class)
@PageTitle("Edit Reminder | Remind Me Of That")
//@Theme(value = "app-theme")
//@CssImport(value = "./styles/vaadin-grid-styles.css", themeFor = "vaadin-grid")
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
    
    TextField subject = new TextField("Reminder Topic");
    TextArea body = new TextArea("Body");

    Select<ReminderRepeatType> reminderRepeatType = new Select<>();

    DatePicker startDate = new DatePicker();// Value of label is set below

    DatePicker endDate = new DatePicker("Reminder End Date");

    Button saveButton = VaadinConstants.saveButton();
    Button cancelButton = VaadinConstants.cancelButton();

    //Grid setup
    Grid<ReminderConfig> remindersGrid = new Grid<>();

    //Whether we are editing within the dialog or not (if not, we are adding a reminder)
    boolean editing = false;

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

            logger.debug("Your time zone offset is [{}], and your timezone ID is [{}]", offset.getId(), details.getTimeZoneId());

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
            //logger.debug("Reminder user is [{}]", reminderUserOptional);

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

        //Create the main layout for the page
        createMainLayout();
    }

    private void createMainLayout() {
        //Add the add reminder button
        var addReminderButton = new Button("Add Reminder", new Icon(VaadinIcon.PLUS));
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.setHorizontalComponentAlignment(Alignment.CENTER, addReminderButton);
        addReminderButton.addClickListener(event -> {
            ReminderRepeatType neverRepeat = reminderRepeatTypeRepository.findReminderRepeatTypeByKey(ReminderRepeatType.RepeatTypeKey.NEVER.getKey());
            this.reminderConfig = new ReminderConfig();
            reminderConfig.setReminderRepeatType(neverRepeat);
            binder.setBean(reminderConfig);
            editing = false;
            editReminderConfigDialog.open();
        });

        setSizeFull();

        //Add all the components
        add(addReminderButton, new H4("Your Reminders"), remindersGrid, editReminderConfigDialog);
    }

    private void createGrid() {
        //The date format to use in the grid display
        DateTimeFormatter gridDisplayDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //Set up the columns
        //remindersGrid.addColumn(createToggleDetailsRenderer()).setWidth("7em").setFlexGrow(0);
        remindersGrid.addColumn(item -> item.getSubject()).setHeader("Topic").setKey("reminderTopic").setSortable(true);
        remindersGrid.addColumn(item -> item.getReminderRepeatType().getName()).setHeader("Repeat").setKey("repeat").setSortable(true);
        Grid.Column<ReminderConfig> startDateColumn = remindersGrid.addColumn(reminderConfig -> reminderConfig.getStartDate().format(gridDisplayDateFormatter))
                .setHeader("Start Date").setKey("startDate").setSortable(true);
        remindersGrid.addColumn(reminderConfig -> {
           /* if (!reminderConfig.getReminderRepeatType().getKey().equals(ReminderRepeatType.RepeatTypeKey.NEVER.getKey())) {
                return reminderConfig.getEndDate().format(gridDisplayDateFormatter);
            } else {
                return "n/a";
            }*/
            return reminderConfig.getEndDate().format(gridDisplayDateFormatter);
        }).setHeader("End Date").setKey("endDate").setSortable(true);

        //Add enabled/disabled column using lumo icons
/*        Grid.Column<ReminderConfig> enabledColumn = remindersGrid.addComponentColumn(reminderConfig -> {
            if (reminderConfig.getEnabled()) {
                return new Icon("lumo", "checkmark");
            } else {
                return new Icon("lumo", "cross");
            }
        }).setHeader("Enabled").setKey("enabled").setSortable(false);*/

        remindersGrid.addColumn(createManageReminderRenderer()).setHeader("Manage Reminder").setKey("manageReminder").setSortable(false);

        remindersGrid.setSizeFull();

        //Set up the details renderer
       /* remindersGrid.setItemDetailsRenderer(
                new ComponentRenderer<>(reminderConfig -> {
                    //VerticalLayout reminderDetailsLayout = new VerticalLayout();
                    Paragraph paragraph = new Paragraph();
                    //paragraph.add(reminderConfig.getBody());
                    paragraph.add("Explanatory text here");

                    return paragraph;
                }));*/

        //Set the items
        ListDataView reminderGridItems = remindersGrid.setItems(reminderConfigRepository.findReminderConfigByReminderUserOrderByCreatedDateDesc(reminderUser));

        //Set the default sort order
        List<GridSortOrder<ReminderConfig>> sortOrderList = new ArrayList<>();
        sortOrderList.add(new GridSortOrder<>(startDateColumn, SortDirection.DESCENDING));
        remindersGrid.sort(sortOrderList);

        //Turn off the selection mode
        remindersGrid.setSelectionMode(Grid.SelectionMode.NONE);
    }

    //Render the manage reminders button


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
        reminderRepeatType.setWidth("15em");
        List<ReminderRepeatType> reminderRepeatTypes = reminderRepeatTypeRepository.findAll();
        ReminderRepeatType neverRepeat = reminderRepeatTypeRepository.findReminderRepeatTypeByKey(ReminderRepeatType.RepeatTypeKey.NEVER.getKey());
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
                            //If the end date is not enabled, or this is a "NEVER" type reminder, return null
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
        buttonActions.setSizeFull();

        //Cancel action
        cancelButton.addClickListener(event -> {
            //binder.
            editReminderConfigDialog.close();
        });

        //Save or update action
        saveButton.addClickListener(event -> {
            try {

                //Write this back to the reminder config object
                binder.writeBean(reminderConfig);

                //Set the user that you got from the ID
                reminderConfig.setReminderUser(reminderUser);

                //Save this all to the database with the correct logic for updating or creating
                if (editing) { //We are updating an existing reminder
                    reminderManagementService.updateRemindersWithRandomTime(reminderConfig, timeZoneOffset);
                } else { //We are saving a new reminder
                    reminderManagementService.createNewRemindersWithRandomTime(reminderConfig, timeZoneOffset);
                }

                //Clear out the ReminderConfig object for clarity
                reminderConfig = new ReminderConfig();

                //Update the grid showing the list of reminders
                remindersGrid.setItems(reminderConfigRepository.findReminderConfigByReminderUserOrderByCreatedDateDesc(reminderUser));

                //Close the dialog and update the items in the list
                editReminderConfigDialog.close();

            } catch (ValidationException e) {

                //TODO make sure any validation errors are handled sensibly for clients
                logger.warn("Validation error saving environment form");
            }
        });

        buttonActions.add(saveButton, cancelButton);

        return buttonActions;
    }

    private ComponentRenderer<HorizontalLayout, ReminderConfig> createManageReminderRenderer() {
        return new ComponentRenderer<>(localReminderConfig -> {
            HorizontalLayout reminderRowButtonLayout = new HorizontalLayout();

            Button editRowButton = VaadinConstants.editButton();
            editRowButton.setTooltipText("Edit reminder");

            //Set the correct button text depending on whether the reminder is enabled or not
            Button disableRowButton = null;
            ConfirmDialog disableRowConfirmDialog = new ConfirmDialog();
            disableRowConfirmDialog.setHeader("Disable Reminder");
            disableRowConfirmDialog.setText("Disabling this reminder will stop any reminders from being sent out until it is re-enabled. " +
                    "Are you sure you want to disable this reminder?");
            disableRowConfirmDialog.setCancelable(true);
            disableRowConfirmDialog.setConfirmText("Disable Reminder");
            disableRowConfirmDialog.addConfirmListener(event -> {
                localReminderConfig.setEnabled(!localReminderConfig.getEnabled());
                reminderConfigRepository.save(localReminderConfig);
                remindersGrid.setItems(reminderConfigRepository.findReminderConfigByReminderUserOrderByCreatedDateDesc(reminderUser));
            });
            if (localReminderConfig.getEnabled()) {
                disableRowButton = VaadinConstants.enableButton();
                disableRowButton.setTooltipText("Disable reminder");

                disableRowButton.addClickListener(event -> {
                    disableRowConfirmDialog.open();
                });
            } else {
                disableRowButton = VaadinConstants.disableButton();
                disableRowButton.setTooltipText("Enable reminder");
                disableRowButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

                //Set up the actions that will lead the reminder to be enabled or disabled
                disableRowButton.addClickListener(event -> {
                    localReminderConfig.setEnabled(!localReminderConfig.getEnabled());
                    reminderConfigRepository.save(localReminderConfig);
                    remindersGrid.setItems(reminderConfigRepository.findReminderConfigByReminderUserOrderByCreatedDateDesc(reminderUser));
                });
            }



            Button deleteRowButton = VaadinConstants.deleteButton();
            deleteRowButton.setTooltipText("Delete reminder");

            //Configure the edit button
            editRowButton.addClickListener(event -> {
                //Refresh the reminderConfig
                Optional<ReminderConfig> reminderConfigOptional = reminderConfigRepository.findById(localReminderConfig.getId());
                if (reminderConfigOptional.isPresent()) {
                    logger.debug("Refreshing reminder config with ID [{}]", localReminderConfig.getId());
                    this.reminderConfig = reminderConfigOptional.get();
                } else {
                    logger.error("Could not find reminder config with ID [{}]", localReminderConfig.getId());
                    this.reminderConfig = localReminderConfig; //Just use the one we have if we can't refresh the other one
                }

                editing = true;

                //Check if end date should be enabled, and also clear out the value if NEVER is used
                if (this.reminderConfig.getReminderRepeatType().getKey().equals(ReminderRepeatType.RepeatTypeKey.NEVER.getKey())) {
                    endDate.setEnabled(false);
                    this.reminderConfig.setEndDate(null);
                } else {
                    endDate.setEnabled(true);
                }

                binder.setBean(this.reminderConfig);

                editReminderConfigDialog.open();
            });

            //Configure the delete button using a confirm dialog
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Delete Reminder?");
            dialog.setText("Are you sure you want to delete this reminder? This removes all reminder " +
                    " configuration and history. If you just want to stop reminders, you can also disable this reminder.");
            dialog.setCancelable(true);
            dialog.setConfirmText("Delete Reminder");
            dialog.setConfirmButtonTheme("error primary");
            dialog.addConfirmListener(event -> {
                reminderConfigRepository.delete(localReminderConfig);
                remindersGrid.setItems(reminderConfigRepository.findReminderConfigByReminderUserOrderByCreatedDateDesc(reminderUser));
            });

            deleteRowButton.addClickListener(event -> {
                dialog.open();
            });

            reminderRowButtonLayout.add(editRowButton, disableRowButton, deleteRowButton);
            return reminderRowButtonLayout;
        });
    }

    /**
     * Convert the date from the UI to an OffsetDateTime object with the correct time zone offset and a time of 00:00:00
     * @param localDateFromUi
     * @return new offsetDateTime
     */
    private OffsetDateTime convertLocalDateToOffsetDateTime(LocalDate localDateFromUi) {
        //logger.debug("Creating OffsetDateTime from local date [{}] with offset [{}]", localDateFromUi, timeZoneOffset);

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
