package com.remindmeofthat.web;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Class to help ensure UI elements are the same throughout
 */
public class VaadinConstants {

    public static Button saveButton() {
        //Button saveButton = new Button("Save", new Icon(VaadinIcon.DISC));
        Button saveButton = new Button("Save", new Icon("lumo", "checkmark"));
        return saveButton;
    }

    /*public static Button editButton() {
        return new Button(new Icon(VaadinIcon.ELLIPSIS_DOTS_V));
    }*/

    public static Button editButton() {
        return new Button(new Icon("lumo", "cog"));
    }

    public static Button backButton() {
        return new Button("Back", new Icon(VaadinIcon.ENTER_ARROW));
    }

    public static Button cancelButton() {
        return new Button("Cancel", new Icon("lumo", "cross"));
    }

    public static Button disableButton() {
        return new Button(new Icon("lumo", "eye-disabled"));
    }

    public static Button enableButton() {
        return new Button(new Icon("lumo", "eye"));
    }

    public static Button deleteButton() {
        return new Button(new Icon(VaadinIcon.TRASH));
    }
}
