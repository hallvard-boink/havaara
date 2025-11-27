package com.hallvardlaerum.libs.filerogopplasting;

import com.hallvardlaerum.libs.bilder.BildeVisningsKomponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VelgFilDialog extends Dialog{
    private File valgtFil = null;
    private Filkategori filkategori = Filkategori.ANNET;
    private Grid<File> filGrid;
    private BildeVisningsKomponent bildeVisningsKomponent;
    private FilopplastingsEgnet velgFilDialogEgnet;
    private TextField filnavnTextFieldFilter;
    private TextField stoerrelseTextFieldFilter;
    private HeaderRow filterHeaderRow;


    //TODO: Lag standardkomponent der en entitet har relasjonskoblinger til en bildeEntitet: liste + bilde preview + velg hovedbilde

    public VelgFilDialog(Filkategori filkategori, FilopplastingsEgnet velgFilDialogEgnet, String strTittel){
        this.filkategori = filkategori;
        this.velgFilDialogEgnet = velgFilDialogEgnet;

        setHeaderTitle(strTittel);
        VerticalLayout verticalLayout = new VerticalLayout();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.addToPrimary(opprettGrid());
        splitLayout.addToSecondary(opprettBildevisningskomponent());
        splitLayout.setSizeFull();
        verticalLayout.add(splitLayout);

        verticalLayout.add(opprettKnapperad());

        verticalLayout.setSizeFull();
        add(verticalLayout);

        setSizeFull();
    }

    private VerticalLayout opprettBildevisningskomponent() {
        bildeVisningsKomponent = new BildeVisningsKomponent();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(bildeVisningsKomponent);
        return verticalLayout;
    }

    private HorizontalLayout opprettKnapperad() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button velgButton = new Button("Velg");
        velgButton.addClickListener(e -> dialog_velgFil(filGrid.getSelectedItems().stream().findFirst().get()));
        velgButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button avBrytButton = new Button("Avbryt");
        avBrytButton.addClickListener(e -> close());

        horizontalLayout.add(velgButton,avBrytButton);
        horizontalLayout.setWidthFull();
        return horizontalLayout;
    }

    private Grid<File> opprettGrid(){
        filGrid = new Grid<>();
        filGrid.addColumn(File::getName).setHeader("Filnavn");
        filGrid.addColumn(File::length).setHeader("Størrelse");
        filGrid.setItems(byggFilerArrayList());
        filGrid.addSelectionListener(rad -> {
           File valgtFil = rad.getFirstSelectedItem().get();
           bildeVisningsKomponent.hentBildeFraFilnavn(valgtFil.getName());
        });
        filGrid.setSizeFull();

        filnavnTextFieldFilter = new TextField();
        filnavnTextFieldFilter.setPlaceholder("filnavn...");
        filnavnTextFieldFilter.setClearButtonVisible(true);
        filnavnTextFieldFilter.setValueChangeMode(ValueChangeMode.LAZY);
        filnavnTextFieldFilter.addValueChangeListener(e -> settFilter());

        stoerrelseTextFieldFilter = new TextField();
        stoerrelseTextFieldFilter.setPlaceholder("størrelse...");
        stoerrelseTextFieldFilter.setClearButtonVisible(true);
        stoerrelseTextFieldFilter.setValueChangeMode(ValueChangeMode.LAZY);
        stoerrelseTextFieldFilter.addValueChangeListener(e -> settFilter());

        filterHeaderRow = filGrid.appendHeaderRow();
        filterHeaderRow.getCells().get(0).setComponent(filnavnTextFieldFilter);
        filterHeaderRow.getCells().get(1).setComponent(stoerrelseTextFieldFilter);

        return filGrid;
    }

    private void settFilter() {
        GridListDataView<File> listDataView = filGrid.getListDataView();
        listDataView.removeFilters();

        if (!filnavnTextFieldFilter.getValue().isEmpty()) {
            listDataView.addFilter(f -> f.getName().contains(filnavnTextFieldFilter.getValue()));
        }

        if (!stoerrelseTextFieldFilter.getValue().isEmpty()) {
            listDataView.addFilter(f -> String.valueOf(f.length()).contains(stoerrelseTextFieldFilter.getValue()));
        }
        listDataView.refreshAll(); //Usikkert om denne virker..
    }

    private void dialog_velgFil(File valgtFil) {
        if (valgtFil != null){
            this.valgtFil = valgtFil;
        }
        close();
        velgFilDialogEgnet.lagringEtterOpplastingEllerValgAvFil(hentValgtFilnavn());
    }

    public String hentValgtFilnavn(){
        if (valgtFil!=null) {
            return valgtFil.getName();
        } else {
            return "";
        }
    }

    private ArrayList<File> byggFilerArrayList(){
        File mappe = new File("./" + filkategori.getMappeNavn() + "/");
        if (mappe.exists()){
            File[] filer = mappe.listFiles();
            if (filer!=null) {
                return new ArrayList<>(List.of(filer));
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }



}
