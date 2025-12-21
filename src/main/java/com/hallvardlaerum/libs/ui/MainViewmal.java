package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.feiloglogging.Infobit;
import com.hallvardlaerum.libs.feiloglogging.HavaaraVersjonskyklop;
import com.hallvardlaerum.libs.feiloglogging.Versjon;
import com.hallvardlaerum.libs.feiloglogging.VersjonskyklopAktig;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.Inspeksjonskyklop;
import com.hallvardlaerum.libs.verktoy.Backupkyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;

/**
 * Denne lager et standard "Om.."-vindu. Her kjøres initiering av alle entitetservicer, så den er viktig.
 * I tillegg får man følgende funksjoner gratis:
 *      - hvilken funksjonalitet som er lagt til i appen til ulike versjoner
 *      - hvilken versjon av havaara som brukes
 *      - antall poster i hver tabell, og når tabellen sist ble endret
 *      - mulighet til å starte og stoppe backup (zippede csv-filer), kjøre backup nå og evt. slette gamle backups. Restore gjøres manuelt, en utpakket csv-fil per entitet
 *
 */



public abstract class MainViewmal extends Main implements MainViewAktig{
    private VersjonskyklopAktig versjonskyklop;
    private VerticalLayout bakgrunnLayout;
    private TabSheet detaljerTabsheet;
    private TekstTabell backupsTekstTabell;
    private TekstTabell felterTekstTabell;


    public MainViewmal() {

    }
    

    @Override
    public void opprettLayout(VersjonskyklopAktig versjonskyklop){
        addClassName(LumoUtility.Padding.MEDIUM);
        setSizeFull();
        this.versjonskyklop = versjonskyklop;
        bakgrunnLayout = new VerticalLayout();
        add(bakgrunnLayout);

        // Felles del i toppen
        bakgrunnLayout.add(new H2(versjonskyklop.getApplikasjonsNavnString()));
        bakgrunnLayout.add(new Div(versjonskyklop.getApplikasjonsBeskrivelseString()));

        //Tabs
        detaljerTabsheet = new TabSheet();
        bakgrunnLayout.add(detaljerTabsheet);

        opprettLayout_generellTab();
        opprettLayout_versjonerTab();
        opprettLayout_backupsTab();
        opprettLayout_felterTab();

        detaljerTabsheet.setSizeFull();
        bakgrunnLayout.setSizeFull();

    }

    public void opprettLayout(VersjonskyklopAktig versjonskyklop, boolean byggOppMenyenManuelt) {

    }

    private HorizontalLayout opprettLayout_knapperad() {
        HorizontalLayout knapperHorizontalLayout = new HorizontalLayout();
        knapperHorizontalLayout.add(Backupkyklop.hent().hentBackupLenkeButton(versjonskyklop.getApplikasjonsNavnString()));
        knapperHorizontalLayout.add(Backupkyklop.hent().hentStartBakcupDemonButton());
        knapperHorizontalLayout.add(Backupkyklop.hent().hentStoppBackupDemonButton());
        knapperHorizontalLayout.add(Backupkyklop.hent().hentBackupBadge());
        return(knapperHorizontalLayout);
    }

    private void opprettLayout_backupsTab(){
        VerticalLayout ark = new VerticalLayout();

        ark.add(opprettLayout_knapperad());
        ark.add(opprettBackupsTekstTabell());

        ark.setSizeFull();

        Tab backupTab = detaljerTabsheet.add("Backups",ark);
        Backupkyklop.hent().setBackupsTab(backupTab);
        Backupkyklop.hent().oppdaterBackupTab();
    }

    private void opprettLayout_versjonerTab(){
        VerticalLayout ark = new VerticalLayout();

        ark.add(opprettVersjonerTeksttabell());

        ark.setSizeFull();
        detaljerTabsheet.add("Versjoner",ark);
    }

    private void opprettLayout_generellTab(){
        VerticalLayout ark = new VerticalLayout();

        ark.add(opprettApplikasjonTekstTabell());
        ark.setSpacing(true);
        ark.add(opprettEntitetTeksttabell());

        ark.setSizeFull();
        detaljerTabsheet.add("Generelt",ark);
    }

    private void opprettLayout_felterTab(){
        VerticalLayout ark = new VerticalLayout();

        String[] kolonneTitler = {"Klasse", "Forelderklasse","Felt"};
        felterTekstTabell = new TekstTabell("Felter",kolonneTitler);
        ArrayList<Inspeksjonskyklop.UtvidetFelt> alleFelterArrayList = Backupkyklop.hent().byggOppAlleFelterArrayList();
        if (alleFelterArrayList.isEmpty()) {
            felterTekstTabell.leggTilRad("","(ingen entiteter)","(ingen felter)");
        } else {
            for (Inspeksjonskyklop.UtvidetFelt utvidetFelt:alleFelterArrayList) {

                String superklasseString = "(ingen)";
                if (utvidetFelt.getSuperklasse()!=null) {
                    superklasseString = utvidetFelt.getSuperklasse().getSimpleName();
                }

                felterTekstTabell.leggTilRad(
                        utvidetFelt.getKlasse().getSimpleName(),
                        superklasseString,
                        utvidetFelt.getFelt().getName()
                );
            }
        }

        felterTekstTabell.oppdaterRadene();
        felterTekstTabell.setSizeFull();
        ark.add(felterTekstTabell);
        ark.setSizeFull();
        detaljerTabsheet.add("Felter",ark);
    }



    private TekstTabell opprettApplikasjonTekstTabell(){

        ArrayList<Infobit> infobiter = new ArrayList<>();
        infobiter.add(new Infobit("Versjon:",versjonskyklop.hentSisteVersjonSomStreng()));
        infobiter.add(new Infobit("Nytt i versjonen:", versjonskyklop.hentSisteVersjon().getBeskrivelse()));
        infobiter.add(new Infobit("Bruk av rammeverk:", HavaaraVersjonskyklop.hent().hentSisteVersjonSomStreng()));

        TekstTabell tekstTabell = new TekstTabell("Info om applikasjonen",infobiter);

        return tekstTabell;
    }


    private TekstTabell opprettBackupsTekstTabell(){
        Backupkyklop.hent().setMainViewMalInstans(this);

        String[] kolonneTitler = {"Fil","Størrelse (kb)"};
        backupsTekstTabell = new TekstTabell("Backups",kolonneTitler);

        // Legge til knappekolonner
        Grid<TekstTabell.Rad> grid = backupsTekstTabell.getGrid();
        opprettBackupteksttabell_leggTilLastNedButton(grid);
        opprettBackupteksttabell_leggTilRestoreButton(grid);
        opprettBackupteksttabell_leggTilSlettButton(grid);

        //oppdaterBackupteksttabell();
        ArrayList<Infobit> backuper = Backupkyklop.hent().getBackupInfobiter();

        if (backuper.isEmpty()) {
            backupsTekstTabell.leggTilRad("","(ingen backups er kjørt ennå)");

        } else {
            for (Infobit infobit:backuper) {
                backupsTekstTabell.leggTilRad(infobit.getTittel(), infobit.getBeskrivelse());
            }

        }
        backupsTekstTabell.oppdaterRadene();

        setSizeFull();
        return backupsTekstTabell;
    }

    public void oppdaterBackupteksttabell() {
        opprettBackupsTekstTabell();

//        backupsTekstTabell.toemInnholdIGrid();
//        ArrayList<Infobit> backuper = Backupkyklop.hent().getBackupInfobiter();
//
//        if (backuper.isEmpty()) {
//            backupsTekstTabell.leggTilRad("","(ingen backups er kjørt ennå)");
//
//        } else {
//            for (Infobit infobit:backuper) {
//                backupsTekstTabell.leggTilRad(infobit.getTittel(), infobit.getBeskrivelse());
//            }
//        }
//        backupsTekstTabell.oppdaterRadene();


    }

    private void opprettBackupteksttabell_leggTilSlettButton(Grid<TekstTabell.Rad> grid) {
        // Button: Slett backup på server
        grid.addColumn(new ComponentRenderer<Span, TekstTabell.Rad>(rad -> {
            Span span = new Span();
            Button slettButton = new Button("Slett");
            slettButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            slettButton.addClickListener(e -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Slette backup");
                dialog.setText("Vil du slette backupfilen " + rad.getCelle0() + " på serveren?");
                dialog.addConfirmListener(event -> {
                    Backupkyklop.hent().slettBackupfil(rad.getCelle0());
                });
                dialog.open();
            });
            span.add(slettButton);
            return span;
        }));
    }

    private void opprettBackupteksttabell_leggTilRestoreButton(Grid<TekstTabell.Rad> grid) {
        // Anchor/Button: Restore backup
        grid.addColumn(new ComponentRenderer<Span, TekstTabell.Rad>(rad -> {
            Span span = new Span();
            Button button = new Button("Restore");
            button.addClickListener(e -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Restore fra backup");
                dialog.setText("Vil du kjøre restore fra backupfilen " + rad.getCelle0() + " på serveren?");
                dialog.addConfirmListener(event -> {
                    Backupkyklop.hent().restoreBackupfilButtonMethod(rad.getCelle0());
                });
                dialog.open();
            });
            span.add(button);
            return span;
        }));
    }

    private void opprettBackupteksttabell_leggTilLastNedButton(Grid<TekstTabell.Rad> grid) {
        // Anchor/Button: Last ned backup fra server
        grid.addColumn(new ComponentRenderer<Span, TekstTabell.Rad>(rad -> {
            Span span = new Span();
            Anchor anchor = Backupkyklop.hent().hentBackupLenkeButtonTilGrid(rad.getCelle0());
            Button buttonLastNedDummyButton =new Button("Last ned");
            anchor.add(buttonLastNedDummyButton);
            span.add(anchor);
            return span;
        }));
    }


    private TekstTabell opprettEntitetTeksttabell(){
        String[] kolonneTitler = {"Navn","Antall poster","Sist endret"};
        TekstTabell tabell = new TekstTabell("Entiteter",kolonneTitler);

        ArrayList<ArrayList<String>> strengMatrise = Backupkyklop.hent().hentEntiteterAntallposterSistendret();
        for (ArrayList<String> rad:strengMatrise) {
            tabell.leggTilRad(rad);
        }

        tabell.oppdaterRadene();
        return tabell;
    }


    private TekstTabell opprettVersjonerTeksttabell(){
        String[] kolonnetitler = {"Versjon","Dato","Tittel","Beskrivelse"};
        TekstTabell tekstTabell = new TekstTabell("Versjoner av denne applikasjonen", kolonnetitler);
        for (Versjon versjon : versjonskyklop.getVersjonerArrayList()) {
            tekstTabell.leggTilRad(
                    versjon.getVersjonsNr(),
                    Datokyklop.hent().formaterDato(versjon.getDato()),
                    versjon.getTittel(),
                    versjon.getBeskrivelse()
            );
        }
        tekstTabell.oppdaterRadene();
        return tekstTabell;
    }


}
