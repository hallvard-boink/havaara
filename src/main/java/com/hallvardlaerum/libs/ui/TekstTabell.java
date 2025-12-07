package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.feiloglogging.Infobit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.Arrays;

/// # TekstTabell
/// Denne klassen lager en VerticalLayout med grid på en enkel måte ut fra tekstbiter. Den generelle fremgangsmåten er
/// å bygge opp innholdet først, deretter lage tabellen.
///
/// ## Fremgangsmåte 1: Strenger
/// 1. Opprett TekstTabell med tabelltittel og kolonnetitler som strenger, f.eks. new TekstTabell("Tabelltittel","Kolonnetittel1","Kolonnetittel2","Kolonnetittel3")
/// 2. Legg til rader med metodene leggTilRad("streng1","streng2","streng3") eller leggTilRad(raderArraylist)
/// 3. Endre evt. instillinger av utseende
/// 4. Bygg layout
///
/// ## Fremgangsmåte 2: Infobiter
/// Klassen Infobit er en enkel POJO med feltene tittel og beskrivelse. Teksttabeller som bygges opp med denne får
/// automatisk to kolonner.
///  1. Bygg opp en ArrayList<Infobit> med radene du ønsker på formen new Infobit("tittel","beskrivelse");
///  2. Opprett TekstTabell med tittel som streng og en Arraylist<Infobit>, f.eks. new TekstTabell("Tabelltittel",infobiterArrayList);
///
/// Denne måtene er enklere, og har ikke behov for å bygge opp layout separat.
///
///

public class TekstTabell extends VerticalLayout {
    private HorizontalLayout tittelStripe;
    private Span tittel;
    private String strTittel;
    private Grid<Rad> grid;
    private Boolean blnBrukTittelStripe = true;
    private ArrayList<Rad> radene;
    private String[] kolonnetitlene;
    private Integer antallKolonner;
    private final Integer intPixlerPerRad = 30;
    private final Integer intPixlerTilHeader = 160;


    /**
     * Her bygges hele tabellen opp på en gang
     *
     * @param strTittel Tabellens tittel
     * @param kolonnetitlene Array med strenger
     * @param tekstradene Array med Array med strenger
     */
    public TekstTabell(String strTittel, String[] kolonnetitlene, ArrayList<ArrayList<String>> tekstradene) {
        oppdaterInfo(strTittel, kolonnetitlene);
        opprettLayout();
        oppdaterRadene();
    }

    /**
     * Har opprettes layout, men radene legges til etterpå
     *
     * @param strTittel Tabellens tittel
     * @param kolonnetitlene så mange strenger man vil, eller en String[]
     */
    public TekstTabell(String strTittel, String... kolonnetitlene){
        oppdaterInfo(strTittel, kolonnetitlene);
        opprettLayout();
    }



    /**
     * Her brukes infobiter i stedet, og tabellen bygges med en gang
     * @param strTittel
     * @param infobiter
     */
    public TekstTabell(String strTittel, ArrayList<Infobit> infobiter) {
        String[] kolonnetitlene = {"Tittel","Beskrivelse"};
        oppdaterInfo(strTittel, kolonnetitlene);
        for (Infobit infobit:infobiter) {
            leggTilRad(new String[]{infobit.getTittel(), infobit.getBeskrivelse()});
        }
        opprettLayout();
        oppdaterRadene();
    }



    public void toemInnholdIGrid() {
        grid.setItems(new ArrayList<>());
        grid.getListDataView().refreshAll();
    }

    public Grid<Rad> getGrid(){
        return grid;
    }

    private void oppdaterInfo(String strTittel, String[] kolonnetitlene){
        if (kolonnetitlene==null) {
            return;
        }
        antallKolonner = kolonnetitlene.length;
        if (strTittel==null || strTittel.isEmpty()) {
            blnBrukTittelStripe = false;
        } else {
            this.strTittel = strTittel;
            blnBrukTittelStripe = true;
        }

        radene = new ArrayList<>();
        this.kolonnetitlene=kolonnetitlene;
    }

    public void opprettLayout() {
        setMargin(false);
        setSpacing(false);
        setPadding(false);

        opprettLayout_tittelStripe();
        opprettLayout_grid();
    }

    public void opprettLayoutMedRader(){
        opprettLayout();
        oppdaterRadene();
    }

    private void opprettLayout_grid(){
        grid = new Grid<>();
        opprettLayout_grid_kolonner();
        add(grid);
        setWidthFull();
        Integer height = (intPixlerPerRad*radene.size()) + intPixlerTilHeader;
        setHeight(height + "px");
    }

    private void opprettLayout_grid_kolonner(){
        if (antallKolonner>=1) grid.addColumn(Rad::getCelle0).setHeader(kolonnetitlene[0]).setAutoWidth(true);
        if (antallKolonner>=2) grid.addColumn(Rad::getCelle1).setHeader(kolonnetitlene[1]).setAutoWidth(true);
        if (antallKolonner>=3) grid.addColumn(Rad::getCelle2).setHeader(kolonnetitlene[2]).setAutoWidth(true);
        if (antallKolonner>=4) grid.addColumn(Rad::getCelle3).setHeader(kolonnetitlene[3]).setAutoWidth(true);
        if (antallKolonner==5) grid.addColumn(Rad::getCelle4).setHeader(kolonnetitlene[4]).setAutoWidth(true);
    }

    private void opprettLayout_tittelStripe(){
        if (blnBrukTittelStripe) {
            tittelStripe = new HorizontalLayout();
            tittel = new Span(strTittel);
//            tittel.addClassName(LumoUtility.Background.ERROR_50);  //Bare for testingens skyld
            tittel.addClassName(LumoUtility.FontWeight.BOLD);

            //tittel.setMaxHeight("14px");
            tittelStripe.add(tittel);

            tittelStripe.addClassName(LumoUtility.Background.PRIMARY_10);
            tittelStripe.addClassName(LumoUtility.Border.ALL);
            tittelStripe.setMargin(false);
            tittelStripe.setPadding(true);
            // tittelStripe.getThemeList().add("padding-xs");  // virket ikke
            tittelStripe.setWidthFull();
            //tittelStripe.setHeight("30px");  //trengs ikke, styres av padding

            add(tittelStripe);
        }
    }

    public void oppdaterRadene(){
        grid.setItems(radene);
        grid.setSizeFull();
        setSizeFull();
    }

    public void erstattRadene(ArrayList<ArrayList<String>> radArrayArrayList) {
        radene = new ArrayList<>();
        for (ArrayList<String> rad:radArrayArrayList) {
            leggTilRad(rad);
        }
        oppdaterRadene();
    }

    private void setItems(ArrayList<Infobit> infobiter){
        ArrayList<Rad> radene = new ArrayList<>();
        for (Infobit infobit:infobiter) {
            radene.add(new Rad(infobit.getTittel(), infobit.getBeskrivelse()));
        }
        grid.setItems(radene);
    }

    public void leggTilRad(String... cellerArray){
        radene.add(new Rad(cellerArray));
    }

    public void leggTilRad(ArrayList<String> celler) {
        radene.add(new Rad(celler));
    }

    public class Rad {
        ArrayList<String> celler;

        public Rad(ArrayList<String> celler) {
            this.celler = celler;
            antallKolonner = Math.max(celler.size(),antallKolonner);
        }


        public Rad(String... cellerArray) {
            this.celler = new ArrayList<>(Arrays.asList(cellerArray));
            antallKolonner = Math.max(celler.size(),antallKolonner);
        }

        public String getCelleTekst(Integer kolNr) {
            if (kolNr<antallKolonner) {
                return celler.get(kolNr);
            } else {
                return "";
            }
        }

        public String getCelle0(){ return celler.getFirst();}
        public String getCelle1(){ return celler.get(1);}
        public String getCelle2(){ return celler.get(2);}
        public String getCelle3(){ return celler.get(3);}
        public String getCelle4(){ return celler.get(4);}

    }

}
