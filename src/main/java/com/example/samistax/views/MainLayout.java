package com.example.samistax.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        var toggle = new DrawerToggle();

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        var sideBar = new VerticalLayout();
        var appName = new H1("DataStock");
        var scroller = new Scroller(createNavigation());

        appName.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);

        sideBar.add(appName, scroller);
        sideBar.expand(scroller);

        addToDrawer(sideBar);
    }

    private Component createNavigation() {
        var links = new VerticalLayout();
        links.setPadding(false);

        links.add(new RouterLink("Dashboard", DashboardView.class));
        links.add(new RouterLink("Stock Details", StockDetailsView.class));

        return links;
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        var title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
