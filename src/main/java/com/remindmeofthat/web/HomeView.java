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

@Route(value = "")
@PageTitle("Home")
public class HomeView extends VerticalLayout {

    public HomeView() {

        HorizontalLayout mainHorizontalLayout = new HorizontalLayout();

        H1 greetingText = new H1("Welcome to Remind Me Of That!");
        H3 instructionText = new H3("To get started, enter your email. We'll email you a link that you can use to set up your first reminder.");

        Image image = new Image("images/alarm_clock.jpeg", "Alarm clock banner");

        VerticalLayout textVerticalLayout = new VerticalLayout();
        textVerticalLayout.add(greetingText, instructionText);

        mainHorizontalLayout.add(image, textVerticalLayout);

       /* ArrayList<String> details = new ArrayList<>();
        details.add("Col A");

        Grid<String> grid = new Grid(String.class, false);
        grid.removeAllColumns();
        grid.addColumn(item -> "partition").setHeader("PARTITION ");
        grid.addColumn(item -> "offset").setHeader("OFFSET");
        grid.addComponentColumn(item -> new Html("<p>This message goes on and on and on and on and on<br/> alsdkjf laskdj laskjdf lksaj" +
                " dfljksad fljaks lksjd fwoieur m xz,cmvnowier owuasldjxvn sd.f, sdfmsdfs,dfmweiur<br/>" +
                " dflakjsd flajskd flaksjd flaksjd flaskjdf laskjdf laksjdf laskjdf  klsjad flkajsd flkajs dflkjas dflkjas dfljkas dflkj asdlfkj " +
                "asdf laskjd flkasjd flkajs dflkajsd flkjasd flakjsdf laksjdf</p>")).setHeader("MESSAGE");

        grid.setItems(details);

        //Label label = new Label();

        //label.set

        //grid.setWidthFull();

        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.getColumns().forEach( col -> {
            col.setAutoWidth(true);
        });*/

        //setWidthFull();

        add(mainHorizontalLayout);

    }
}
