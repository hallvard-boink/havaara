package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.Binder;

public interface  RedigeringsomraademalAktig<Entitet extends EntitetAktig>  {
    // ---------------------------
    // --- Data og databinding ---

    void initRedigeringsomraadeMal();

    Binder<Entitet> hentBinder();
    Entitet getEntitet();

    void setEntitet(Entitet entitet);
    void lesBean();
    void skrivBean();

    void setFokusComponent(Component fokusComponent);

    FormLayout hentFormLayoutFraTab(Integer tabIndex);
    FormLayout hentFormLayoutFraTab(String tabTittelString);

    // ---------------------------
    // --- Layout og struktur ----

    void settView(ViewmalAktig<?,?> delAvView); //Overflødig?
    ViewmalAktig<?,?> hentView(); //Overflødig?

    void leggTilRedigeringsfelter(Component... components);

    <C extends Component> C leggTilRedigeringsfelt(C komponent);
    <C extends Component> C leggTilRedigeringsfelt(Integer tabIndex, C komponent);
    <C extends Component> C leggTilRedigeringsfelt(String tabTittelString, C komponent);
    <C extends Component> C leggTilRedigeringsfelt(Tab tab, C komponent);
    <C extends Component> C leggTilAndrefelterOver(C component);
    <C extends Component> C leggTilAndrefelterUnder(C komponent);

    Tab opprettTabOgEvtTabSheet(String tittelString);
    void leggTilDatofeltTidOpprettetOgRedigert();
    void leggTilDatofeltTidOpprettetOgRedigert(String tabString);
    void settColspan(Component komponent, Integer intSpan);

    // ----------------------
    // --- Funksjonalitet ---

    void fokuser();
    void aktiver(Boolean skalAktiveres);

    VerticalLayout hentOverFelterVerticalLayout();
    VerticalLayout hentUnderFelterVerticalLatout();

}
