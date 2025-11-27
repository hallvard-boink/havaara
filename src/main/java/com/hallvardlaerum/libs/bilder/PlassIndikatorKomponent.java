package com.hallvardlaerum.libs.bilder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

//TODO Sørg for callback til bildevisningskomponent for å oppdatere når bruker endrer plass
//TODO Sjekk at antall plasser oppdateres når en ny hovedentitet finnes frem
//TODO Lag adferd ved 0 bilder (grå indikator)
//TODO Skift til listevisning med flere enn x bilder

public class PlassIndikatorKomponent extends HorizontalLayout implements PlassIndikatorKomponentAktig {

    private Integer plassIndeks;
    private Indikator[] indikatorArray;

    private Button bakoverButton;
    private Button fremoverButton;
    private MultiBildeKomponentAktig multiBildeKomponent;



    public PlassIndikatorKomponent(MultiBildeKomponentAktig multiBildeKomponent, Integer lengde, Integer startplassIndeks) {
        indikatorArray = new Indikator[lengde];
        plassIndeks = startplassIndeks;
        this.multiBildeKomponent = multiBildeKomponent;
        opprettLayout(lengde);
        oppdaterPlass(plassIndeks);
    }

    @Override
    public void oppdaterLayout(Integer lengde, Integer startplassIndeks) {
        slettGamleIndikatorer();
        remove(fremoverButton);
        if (!fremoverButton.isEnabled() && lengde>1) {
            fremoverButton.setEnabled(true);
            bakoverButton.setEnabled(true);
        }

        inaktiverEllerleggTilIndikatorer(lengde);

        add(fremoverButton);

        plassIndeks = startplassIndeks;
        oppdaterPlass(plassIndeks);
    }

    private void inaktiverEllerleggTilIndikatorer(Integer lengde){
        if (lengde == null || lengde == 0) {
            inaktiverPlassIndikatorer(false);
        } else if (lengde == 1) {
            inaktiverPlassIndikatorer(true);
        } else {
            leggTilIndikatorer(lengde);
        }
    }

    private void inaktiverPlassIndikatorer(boolean fylltIndikator){
        fremoverButton.setEnabled(false);
        bakoverButton.setEnabled(false);
        leggTilIndikatorer(1);
        if (fylltIndikator) {
            indikatorArray[0].setIcon(VaadinIcon.CIRCLE);
        } else {
            indikatorArray[0].setIcon(VaadinIcon.CIRCLE_THIN);
        }
    }

    private void leggTilIndikatorer(Integer lengde) {
        indikatorArray = new Indikator[lengde];

        for (int i = 0; i< indikatorArray.length; i++) {
            Indikator indikator = new Indikator();
            indikator.setIcon(VaadinIcon.CIRCLE_THIN);
            indikator.setPlassNr(i);
            indikator.addClickListener(e -> oppdaterPlass(((Indikator)e.getSource()).getPlassNr()));
            indikatorArray[i]=indikator;
            add(indikator);
        }
    }

    private void slettGamleIndikatorer(){
        for (int i = 0; i<indikatorArray.length; i++) {
            remove(indikatorArray[i]);
        }
    }

    public void visNesteBilde(){
        if (plassIndeks< indikatorArray.length-1){
            oppdaterPlass(plassIndeks+1);
        }
    }

    public void visForrigeBilde(){
        if (plassIndeks>0) {
            oppdaterPlass(plassIndeks-1);
        }
    }

    public void oppdaterPlass(Integer nyPlass){
        if (nyPlass>= indikatorArray.length || nyPlass<0){
            return;
        }

        indikatorArray[plassIndeks].setIcon(VaadinIcon.CIRCLE_THIN);
        indikatorArray[nyPlass].setIcon(VaadinIcon.CIRCLE);

        plassIndeks = nyPlass;
        multiBildeKomponent.visBildeNr(plassIndeks);
    }


    private void opprettLayout(Integer lengde){
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        bakoverButton = new Button(LumoIcon.ARROW_LEFT.create());
        bakoverButton.addClickListener(e -> visForrigeBilde());
        fremoverButton = new Button(LumoIcon.ARROW_RIGHT.create());
        fremoverButton.addClickListener(e -> visNesteBilde());

        add(bakoverButton);
        inaktiverEllerleggTilIndikatorer(lengde);
        add(fremoverButton);

    }



    private class Indikator extends Icon {
        private Integer plassNr;

        public Indikator() {

        }

        public Indikator(Integer plassNr) {
            this.plassNr = plassNr;
        }

        public Integer getPlassNr() {
            return plassNr;
        }

        public void setPlassNr(Integer plassNr) {
            this.plassNr = plassNr;
        }
    }

}
