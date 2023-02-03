package com.remindmeofthat.web;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Arrays;

@Route(value = "", layout = ParentLayoutView.class)
@PageTitle("Home")
public class HomeView extends VerticalLayout {

    public HomeView() {

        HorizontalLayout mainHorizontalLayout = new HorizontalLayout();

        H1 greetingText = new H1("Welcome to Remind Me Of That!");
        H3 instructionText = new H3("To get started, enter your email. We'll email you a link that you can use to set up your first reminder.");
        TextField collectEmail = new TextField("Email");


        Image image = new Image("images/alarm_clock.jpeg", "Alarm clock banner");

        VerticalLayout textVerticalLayout = new VerticalLayout();
        textVerticalLayout.add(greetingText, instructionText, collectEmail);

        mainHorizontalLayout.add(image, textVerticalLayout);



        add(mainHorizontalLayout);

    }
}
