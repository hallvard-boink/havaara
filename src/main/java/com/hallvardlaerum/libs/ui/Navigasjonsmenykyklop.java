package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class Navigasjonsmenykyklop {
    private static Navigasjonsmenykyklop navigasjonsmenykyklop;


    /**
     * Oppretter menyvalg på
     * @param tittelString
     * @param navString
     * @return SideNavItem dvs. menyvalget
     */
    public SideNavItem leggMenyvalgTilRot(SideNav sideNav, String tittelString, String navString) {
        SideNavItem sideNavItem;
        if (navString==null) {
            sideNavItem = new SideNavItem(tittelString);
        } else {
            sideNavItem = new SideNavItem(tittelString, "/" + navString);
        }

        sideNav.addItem(sideNavItem);
        return sideNavItem;
    }

    public SideNavItem leggMenyvalgforelderTilRot(SideNav sideNav, String tittelString) {
        return leggMenyvalgTilRot(sideNav,tittelString,null);
    }


    /**
     * Legger til menyvalg på et eksisterende menyvalg
     * @param forelderSideNavItem
     * @param tittelString
     * @param navString
     * @return SideNavItem dvs menyvalget
     */
    public SideNavItem leggMenyvalgTilForelder(SideNavItem forelderSideNavItem, String tittelString, String navString) {
        SideNavItem barnSideNavItem;
        if (navString == null) {
            barnSideNavItem = new SideNavItem(tittelString);
        } else {
            barnSideNavItem = new SideNavItem(tittelString, "/" + navString);
        }
        forelderSideNavItem.addItem(barnSideNavItem);
        return barnSideNavItem;

    }

    public SideNavItem leggMenyvalgforelderTilForelder(SideNavItem forelderSideNavItem, String tittelString) {
        return leggMenyvalgTilForelder(forelderSideNavItem, tittelString,null);

    }

    public SideNav opprettSideNavAutomatisk() {
        var nav = new SideNav();
        nav.addClassNames(LumoUtility.Margin.Horizontal.MEDIUM);
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

    public static Navigasjonsmenykyklop hent(){
        if (navigasjonsmenykyklop ==null) {
            navigasjonsmenykyklop = new Navigasjonsmenykyklop();
        }
        return navigasjonsmenykyklop;
    }



    private Navigasjonsmenykyklop() {
    }


}
