package com.remindmeofthat.web;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

/**
 * Establish the top menu
 */
/*@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")*/
public class ParentLayoutView extends AppLayout {

    public ParentLayoutView() {
        //Whether the navbar or drawer is primary
        setPrimarySection(Section.NAVBAR);

        addToNavbar(new DrawerToggle(), new Span("RemindMeOfThat Admin"));

        RouterLink apiDataRouterLink = new RouterLink("Home", HomeView.class);

        Tab apiDataViewTab = new Tab(apiDataRouterLink);

        Tabs tabs = new Tabs(apiDataViewTab);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);
    }
}
