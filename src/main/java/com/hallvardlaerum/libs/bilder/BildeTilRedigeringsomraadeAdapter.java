package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.ViewmalAktig;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.Binder;


/**
 * Denne klarer foreløpig ikke å implementere getBinder(), men den trengs ikke her likevel.
 */
public class BildeTilRedigeringsomraadeAdapter<Bildeklasse extends BildeentitetAktig<Forelderklasse>,
        Forelderklasse extends EntitetAktig>
        implements RedigeringsomraadeAktig<EntitetAktig> {

    BildeRedigeringsomraadeAktig<Bildeklasse,Forelderklasse> bilderedigeringsomraade;


    public BildeTilRedigeringsomraadeAdapter(BildeRedigeringsomraadeAktig<Bildeklasse, Forelderklasse> bilderedigeringsomraade) {
        this.bilderedigeringsomraade = bilderedigeringsomraade;
    }

    public RedigeringsomraadeAktig<EntitetAktig> visSomRedigeringsomraade(){
        return this;
    }



    @Override
    public void setEntitet(EntitetAktig entitet) {
        if (entitet instanceof BildeentitetAktig<?>) {
            bilderedigeringsomraade.setEntitet((Bildeklasse) entitet);
        } else {
            Loggekyklop.hent().loggFEIL("Entiteten er ikke av type BildeentitetAktig, avbryter");
        }
    }

    @Override
    public ViewmalAktig<?> hentView() {
        return bilderedigeringsomraade.hentView();
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt(C komponent) {
        return bilderedigeringsomraade.leggTilRedigeringsfelt(komponent);
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt(C komponent, Integer tabIndex) {
        return bilderedigeringsomraade.leggTilRedigeringsfelt(komponent, tabIndex);
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt(C komponent, String tabTittelString) {
        return bilderedigeringsomraade.leggTilRedigeringsfelt(komponent, tabTittelString);
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt(C komponent, Tab tab) {
        return bilderedigeringsomraade.leggTilRedigeringsfelt(komponent, tab);
    }

    @Override
    public <C extends Component> C leggTilAndrefelterOver(C component) {
        return bilderedigeringsomraade.leggTilAndrefelterOver(component);
    }

    @Override
    public <C extends Component> C leggTilAndrefelterUnder(C komponent) {
        return bilderedigeringsomraade.leggTilAndrefelterUnder(komponent);
    }

    @Override
    public Tab opprettTab(String tittelString) {
        return bilderedigeringsomraade.opprettTab(tittelString);
    }

    @Override
    public void leggTilDatofeltTidOpprettetOgRedigert() {
        bilderedigeringsomraade.leggTilDatofeltTidOpprettetOgRedigert();
    }

    @Override
    public void settColspan(Component komponent, Integer intSpan) {
        bilderedigeringsomraade.settColspan(komponent, intSpan);
    }



    @Override
    public Binder<EntitetAktig> hentBinder() {
        Loggekyklop.hent().loggFEIL("hentBinder er ikke implementert i RedigeringsomraadeAdapter ennå, avbryter");
        return null;
    }

    @Override
    public Bildeklasse getEntitet() {
        return bilderedigeringsomraade.getEntitet();
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        bilderedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
    }

    @Override
    public void instansOpprettFelter() {
        bilderedigeringsomraade.instansOpprettFelter();
    }

    @Override
    public void instansByggOppBinder() {
        bilderedigeringsomraade.instansByggOppBinder();
    }

    @Override
    public void lesBean() {
        bilderedigeringsomraade.lesBean();
    }

    @Override
    public void skrivBean() {
        bilderedigeringsomraade.skrivBean();
    }

    @Override
    public void setFokusComponent(Component fokusComponent) {
        bilderedigeringsomraade.setFokusComponent(fokusComponent);
    }

    @Override
    public void fokuser() {
        bilderedigeringsomraade.fokuser();
    }

    @Override
    public void aktiver(Boolean skalAktiveres) {
        bilderedigeringsomraade.aktiver(skalAktiveres);
    }

    @Override
    public void settView(ViewmalAktig<?> view) {
        //
    }
}


