package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.Binder;

public interface  RedigeringsomraademalAktig<T extends EntitetAktig>  {
    // --- Data og databinding ---
    Binder<T> hentBinder();
    T getEntitet();

    void setEntitet(T entitet);
    void lesBean();
    void skrivBean();

    void setFokusComponent(Component fokusComponent);


    FormLayout hentFormLayoutFraTab(Integer tabIndex);
    FormLayout hentFormLayoutFraTab(String tabTittelString);

    // --- Layout og struktur ----
    void settView(ViewmalAktig<?> delAvView); //Overflødig?
    ViewmalAktig<?> hentView(); //Overflødig?

    <C extends Component> C leggTilRedigeringsfelt(C komponent);
    <C extends Component> C leggTilRedigeringsfelt(C komponent, Integer tabIndex);
    <C extends Component> C leggTilRedigeringsfelt(C komponent, String tabTittelString);
    <C extends Component> C leggTilRedigeringsfelt(C komponent, Tab tab);
    <C extends Component> C leggTilAndrefelterOver(C component);
    <C extends Component> C leggTilAndrefelterUnder(C komponent);

    Tab opprettTab(String tittelString);
    void leggTilDatofeltTidOpprettetOgRedigert();
    void leggTilDatofeltTidOpprettetOgRedigert(String tabString);
    void settColspan(Component komponent, Integer intSpan);


    // --- Funksjonalitet ---
    void fokuser();
    void aktiver(Boolean skalAktiveres);

    VerticalLayout hentOverFelterVerticalLayout();
    VerticalLayout hentUnderFelterVerticalLatout();

}
