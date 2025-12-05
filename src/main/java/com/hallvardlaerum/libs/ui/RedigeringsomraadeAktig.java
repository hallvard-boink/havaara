package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.Binder;


// TODO: Flytt prosedyrene som bare implementeres i redigeringsomraademal inn i egen interface. RedigeringsomraademalAktig
public interface RedigeringsomraadeAktig<T extends EntitetAktig> {
    // ______________________________________________________________
    // ---- PROSEDYRER SOM IMPLEMENTERES I REDIGERINGSOMRAADEMAL ----
    // For å beholde fleksibiliteten og åpne for andre maler enn redigeringsomraademal, må interfacen være stor


        T getEntitet();
        void setEntitet(T entitet);


    // --- Data og databinding ---
        Binder<T> hentBinder();

        void lesBean();
        void skrivBean();

        void setFokusComponent(Component fokusComponent);


    // --- Layout og struktur ----

        ViewmalAktig<?> hentView();
        void settView(ViewmalAktig<?> view);
        <C extends Component> C leggTilRedigeringsfelt(C komponent);
        <C extends Component> C leggTilRedigeringsfelt(Integer tabIndex, C komponent);
        <C extends Component> C leggTilRedigeringsfelt(String tabTittelString, C komponent);
        <C extends Component> C leggTilRedigeringsfelt(Tab tab, C komponent);
        <C extends Component> C leggTilAndrefelterOver(C component);
        <C extends Component> C leggTilAndrefelterUnder(C komponent);
        Tab opprettTab(String tittelString);
        void leggTilDatofeltTidOpprettetOgRedigert();
        void settColspan(Component komponent, Integer intSpan);




    // --- Funksjonalitet ---
        void fokuser();
        void aktiver(Boolean skalAktiveres);






    // ______________________________________________________________
    // ------ PROSEDYRER SOM SKAL IMPLEMENTERES I HVER INSTANS -----

    /**
     * Obs! Legg også til redigeringsområdets eget entitetservice, men også evt. barn og foreldres entitetservicer
     */

    /**
     * Oppdatere innhold i felter som ikke oppdateres av binder
     */
    void instansOppdaterEkstraRedigeringsfelter();

    /**
     * Opprett redigeringsfelter som skal bindes til entiteten med binder
     */
    void instansOpprettFelter();

    /**
     * Knytt redigeringsfelter til binder - kjøres i constructormetoden til instansen
     */
    void instansByggOppBinder();
}
