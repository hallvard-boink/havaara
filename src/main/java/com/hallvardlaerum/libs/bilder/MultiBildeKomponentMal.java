package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetMedBarnAktig;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.felter.Rekkefolgekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.LitRenderer;
import java.util.ArrayList;



/**
 * Denne komponenten brukes til å redigere pekere til bilder lagret i filsystemet på serveren.
 * Pekerne er lagret i en egen bildeentitet koblet til hovedentiteten med mange-til-en kobling.
 * Egentlig kan hver bildefil vises i mange hovedentiteter, siden filnavnene kan brukes flere steder
 *
 * Multibildekomponenten brukes for å pakke inn BilderedigeringsomraadeMal og vise frem gui-virkemidler for å
 * bytte mellom bilder, f.eks. PlassIndikatorKomponent.
 *
 *
 * @param <Bildeklasse>
 */
public class MultiBildeKomponentMal<Bildeklasse extends BildeentitetAktig, Forelderklasse extends EntitetAktig>
        extends VerticalLayout
        implements MultiBildeKomponentAktig<Bildeklasse, Forelderklasse>{

    private RedigeringsomraadeAktig<Forelderklasse> forelderRedigeringsomraade;

    private ArrayList<Bildeklasse> bilderArrayList;
    private Dialog forhaandsvisningDialog;
    private VisningEnum visningEnumDefault = VisningEnum.KARUSELL;
    private VisningEnum visningEnum;
    private TabSheet tabSheet;
    private VerticalLayout karusellTab;
    private VerticalLayout smaabilderTab;
    private VerticalLayout tabellTab;
    private PlassIndikatorKomponent plassIndikatorKomponent;

    private EntitetserviceAktig<Bildeklasse> bildeentitetservice;
    private BildeRedigeringsomraadeAktig<Bildeklasse, Forelderklasse> bildeRedigeringsomraade;
    private EntitetserviceAktig<Forelderklasse> forelderentitetservice;
    private Grid<Bildeklasse> gridSmaabilder;
    private ComboBox<Forelderklasse> forelderCombobox;
    private Boolean erFrittstaaende;

    public MultiBildeKomponentMal(){
        this.visningEnum = visningEnumDefault;
    }


    //TODO: Lag vindu som viser bildet i fullskjermvisning



    @Override
    public void init(VisningEnum visningEnum, EntitetserviceAktig<Bildeklasse> bildeentitetservice, BildeRedigeringsomraadeAktig<Bildeklasse, Forelderklasse> bildeRedigeringsomraade,
              EntitetserviceAktig<Forelderklasse> forelderentitetservice, RedigeringsomraadeAktig<Forelderklasse> forelderRedigeringsomraade
    ){

        this.bildeentitetservice = bildeentitetservice;
        this.bildeRedigeringsomraade = bildeRedigeringsomraade;
        this.forelderentitetservice = forelderentitetservice;
        this.forelderRedigeringsomraade = forelderRedigeringsomraade;


        if (visningEnum == null) {
            this.visningEnum = visningEnumDefault;
        } else {
            this.visningEnum = visningEnum;
        }
        bilderArrayList = new ArrayList<>();

        this.setPadding(false);
        this.setMargin(false);

        opprettLayout();
    }






    private void leggTilNyBildeinfo(){
        Bildeklasse bilde = bildeentitetservice.opprettEntitet();
        bildeRedigeringsomraade.setBildeentitet(bilde);

        if (bilde instanceof EntitetMedForelderAktig) {
            ((EntitetMedForelderAktig)bilde).setForelder(forelderRedigeringsomraade.getEntitet());
        }

        bildeentitetservice.lagre(bilde);
        bildeentitetservice.flush();
        bilderArrayList.add(bilde);
        plassIndikatorKomponent.oppdaterLayout(bilderArrayList.size(),bilderArrayList.size()-1);
        visBildeNr(bilderArrayList.size()-1);
    }

    private void visSlettBildeinfoConfirmDialog() {
        Bildeklasse bilde = (Bildeklasse) bildeRedigeringsomraade.getBildeentitet();
        if (bilde==null) {
            Notification.show("Ingen bilde å slette, avbryter");
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog("Slette bildeinfo?","Er du sikker på at du vil slette bildeinfoen med tittel " + bilde.getTittel() + " (" + bilde.getFilnavn() + ") "
                , "Ja, bare slett",e -> slettBildeinfo(),"Nei, avbryt", null);
        dialog.open();

    }


    private void slettBildeinfo() {
        Bildeklasse bilde = (Bildeklasse) bildeRedigeringsomraade.getBildeentitet();
        bildeentitetservice.slett(bilde);
        EntitetMedBarnAktig forelder = null;
        if (forelderRedigeringsomraade.getEntitet() instanceof EntitetMedBarnAktig) {
            forelder = (EntitetMedBarnAktig) forelderRedigeringsomraade.getEntitet();
            forelder = (EntitetMedBarnAktig) forelderentitetservice.finnEtterUUID(forelder.getUuid().toString());

            int bildeIndex = bilderArrayList.indexOf(bilde);
            if (bildeIndex>0){
                bildeIndex = bildeIndex-1;
            }

            plassIndikatorKomponent.oppdaterLayout(forelder.hentBarn().size(), bildeIndex);
        }

    }


    /**
     * Denne kommer fra plassindikatoren eller oppstart (bruker hovedbildets nummer, default bildenr)
     * @param plassIndeks
     */
    @Override
    public void visBildeNr(Integer plassIndeks) {

        if (!bilderArrayList.isEmpty()) {
            bildeRedigeringsomraade.setBildeentitet(bilderArrayList.get(plassIndeks));
            bildeRedigeringsomraade.aktiver(true);
        } else {
            bildeRedigeringsomraade.setBildeentitet(null);
        }
        bildeRedigeringsomraade.lesBean();
        bildeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();

    }



    @Override
    public void oppdaterForFremfunnetForelderentitet(ArrayList<Bildeklasse> bilderArrayList) {
        this.bilderArrayList = bilderArrayList;
        Rekkefolgekyklop.hent().sorterEtterRekkefoelgenummer(this.bilderArrayList);
        Bildeklasse bilde = null;
        Integer plassIndeks = hentPlassIndeksForHovedbilde();
        plassIndikatorKomponent.oppdaterLayout(bilderArrayList.size(), plassIndeks);
        if (!bilderArrayList.isEmpty()) {
            bilde = bilderArrayList.get(plassIndeks);
        } else {
            bildeRedigeringsomraade.aktiver(false);
        }
        bildeRedigeringsomraade.setBildeentitet(bilde);
        bildeRedigeringsomraade.lesBean();
        bildeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();

//        gridSmaabilder.setItems(bilderArrayList);
//        gridSmaabilder.select(bilde);
    }


    private Integer hentPlassIndeksForHovedbilde(){
        Integer plassIndeksStart = 0;
        if (bilderArrayList.isEmpty()) {
            return -1;
        }

        for (int i = 0; i<bilderArrayList.size(); i++) {
            Bildeklasse bilde = bilderArrayList.get(i);
            if (bilde.getErHovedbilde()!=null && bilde.getErHovedbilde()) {
                return i;
            }
        }
        // Hvis ingen av bildene er hovedbilde, velger vi første bilde
        bilderArrayList.getFirst().setErHovedbilde(true);
        return plassIndeksStart;
    }



    private void opprettLayout(){
        if (visningEnum == VisningEnum.KARUSELL) {
            add(opprettLayout_Karusell());
        } else if (visningEnum == VisningEnum.LISTE) {
            add(opprettLayout_Liste());
        } else if (visningEnum == VisningEnum.TABELL) {
            add(opprettLayout_Tabell());
        }

    }


    private VerticalLayout opprettLayout_Karusell(){
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);
        verticalLayout.setWidthFull();

        HorizontalLayout toppRadhorizontalLayout = new HorizontalLayout();
        plassIndikatorKomponent = new PlassIndikatorKomponent(this, bilderArrayList.size(),finnHovedbildetsIndeks());
        toppRadhorizontalLayout.add(plassIndikatorKomponent, opprettLayout_leggTilNyttBildeButton(), opprettLayout_slettBildeinfoButton());
        toppRadhorizontalLayout.setAlignItems(Alignment.CENTER);

        verticalLayout.add(toppRadhorizontalLayout, (com.vaadin.flow.component.Component) bildeRedigeringsomraade);
        return verticalLayout;
    }

    private Button opprettLayout_leggTilNyttBildeButton(){
        Button leggTilNyttBildeButton = new Button("Opprett ny bildeinfo");
        leggTilNyttBildeButton.addClickListener(e -> leggTilNyBildeinfo());
        return leggTilNyttBildeButton;
    }

    private Button opprettLayout_slettBildeinfoButton(){
        Button leggTilNyttBildeButton = new Button("Slett bildeinfo");
        leggTilNyttBildeButton.addClickListener(e -> visSlettBildeinfoConfirmDialog());
        return leggTilNyttBildeButton;
    }


    private Integer finnHovedbildetsIndeks(){
        if (bilderArrayList.size()>0) {
            Bildeklasse bilde = bilderArrayList.stream().filter(Bildeklasse::getErHovedbilde).toList().getFirst();
            return bilderArrayList.indexOf(bilde);
        } else {
            return 0;
        }
    }



    private VerticalLayout opprettLayout_Liste() {
        VerticalLayout verticalLayout =  new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);
        verticalLayout.setSizeFull();

        SplitLayout splitLayout = new SplitLayout();
        verticalLayout.add(splitLayout);
        splitLayout.setSplitterPosition(20d);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(opprettLayout_Smaabilder_Grid());
        splitLayout.addToSecondary((VerticalLayout)bildeRedigeringsomraade);

        return verticalLayout;

    }



    private Grid<Bildeklasse> opprettLayout_Smaabilder_Grid(){
        gridSmaabilder = new Grid();
        //gridSmaabilder.addColumn(b -> b.getTittel()).setHeader("Tittel");  ikke plass til denne

        String templatStreng =
                "<span>${item.tittel}</span>" +
                "<span style='overflow: hidden; display: flex; align-items: center; justify-content: center; width: 128px; height: 65px'>" +
                        "<img style='max-width: 100%' src=${item.bildedata} />" +
                        "</span>";

        LitRenderer<Bildeklasse> bildeDataRenderer = LitRenderer.<Bildeklasse>of(templatStreng)
            .withProperty("bildedata", item -> {
                if (item != null && item.getFilnavn() != null) {
                    return Bildekyklop.hent().hentBildedataSomStrengFraFilnavn(item.getFilnavn());
                } else {
                    return "";
                }
            })
            .withProperty("tittel", Bildeklasse::getTittel);

        gridSmaabilder.addColumn(bildeDataRenderer).setHeader("Bilde med tittel").setWidth("300px").setFlexGrow(0);

        //gridSmaabilder.setHeight("1000px");
        gridSmaabilder.setSizeFull();

        gridSmaabilder.addSelectionListener( e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                Bildeklasse bilde = e.getFirstSelectedItem().get();
                Integer plassInteger = bilderArrayList.indexOf(bilde);
                plassIndikatorKomponent.oppdaterPlass(plassInteger);
                //visBildeNr(plassInteger); trengs ikke, kalles fra plassindikatorkomponent
            }
        });


        return gridSmaabilder;

    }


    private VerticalLayout opprettLayout_Tabell(){
        VerticalLayout verticalLayout = new VerticalLayout();
        Span spanDummy = new Span("Her kommer det også mer");
        verticalLayout.add(spanDummy);
        return verticalLayout;
    }



    public enum VisningEnum {
        KARUSELL("Karusell","Ett og ett bilde vises"),
        LISTE("Småbilder","Alle bildene vises, i små utgaver. Klikk på et bilde for å få en forstørrelse."),
        TABELL("Tabell","Bilde med tittel vises i en tabell med thumbnail, filnavn, tittel og filstørrelse");

        private String tittel;
        private String beskrivelse;

        VisningEnum(String tittel, String beskrivelse) {
            this.tittel = tittel;
            this.beskrivelse = beskrivelse;
        }
    }
}
