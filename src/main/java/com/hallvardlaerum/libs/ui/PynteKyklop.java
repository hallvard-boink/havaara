package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Litt usikker på om jeg skal bruke denne eller legge funksjonaliteten i maler for andre klasser
 * @deprecated Bruk heller spesialiserte klasser av komponenter, ferdig pyntet.
 *
 */
@Deprecated(forRemoval = true, since = "1.8.2")
public class PynteKyklop {
    private static PynteKyklop pynteKyklop;


    public void storeBlaaBokstaver(Component component) {
        component.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.PRIMARY);
    }


    private PynteKyklop() {
    }

    public static PynteKyklop hent(){
        if (pynteKyklop==null) {
            pynteKyklop = new PynteKyklop();
        }
        return pynteKyklop;
    }
}
