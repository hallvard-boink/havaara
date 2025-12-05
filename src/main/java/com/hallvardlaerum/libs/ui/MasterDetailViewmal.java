package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.*;
import com.hallvardlaerum.libs.eksportimport.CSVEksportkyklop;
import com.hallvardlaerum.libs.eksportimport.CSVImportkyklop;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.button.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dette er en generisk klasse som nye Views i Vaadin skal ekstendere for å få en lik layout og
 * mye felles prosedyrer gratis.
 *
 * Malen inneholder to deler: søkedel og redigeringssdel
 * Søkedelene har grid og filterfelter, samt en knapperad for eksport/import og testdata
 * Redigeringsdelen har knapperad med ny/lagre/slett-knapper og et entitetspesifikt redigeringsområde.
 *
 * @param <Entitet>
 */

public abstract class MasterDetailViewmal<Entitet extends AbstraktEntitet> extends HorizontalLayout implements ViewmalAktig<Entitet> {
    private EntitetserviceAktig<Entitet> entitetserviceAktig;
    private Grid<Entitet> grid;
    private HorizontalLayout layoutRedigeringsknapper;
    private HorizontalLayout layoutSoekeknapper;

    private HeaderRow headerRowFilterfelter;
    private ViewCRUDStatusEnum viewCRUDStatus = ViewCRUDStatusEnum.UROERT;
    private RedigeringsomraadeAktig<Entitet> redigeringsomraade;
    private VerticalLayout redigeringsomraadeSomVerticalLayout;
    private SplitLayout.Orientation orientering = SplitLayout.Orientation.HORIZONTAL;
    private ConfigurableFilterDataProvider<Entitet,Void,String> filterProvider;
    private SplitLayout splitLayout;

    private Button nyButton = null;
    private Button lagreButton = null;
    private Button slettButton = null;

    private MenuBar verktoeyMenuBar;
    private MenuItem verktoeyMenuItem;
    private SubMenu verktoeySubMenu;

    private H2 vinduTittel;

    public MasterDetailViewmal() {
    }

    @Override
    public ViewCRUDStatusEnum getViewCRUDStatus() {
        return viewCRUDStatus;
    }

    @Override
    public void setViewCRUDStatus(ViewCRUDStatusEnum viewCRUDStatus) {
        this.viewCRUDStatus = viewCRUDStatus;
    }

    @Override
    public void instansTilpassNyopprettetEntitet() {
        //ingenting
    }

    @Override
    public void instansAktiverKnapperadRedigeringsfelt(Boolean aktiverBoolean) {
        lagreButton.setEnabled(aktiverBoolean);
        slettButton.setEnabled(aktiverBoolean);
    }

    public Entitet hentEntitet(){
        return redigeringsomraade.getEntitet();
    }

    @Override
    public HorizontalLayout hentKnapperadSoekefelt() {
        return layoutSoekeknapper;
    }

    @Override
    public HorizontalLayout hentKnapperadRedigeringsfelt() { return layoutRedigeringsknapper;   }


    public void opprettLayout(EntitetserviceAktig<Entitet> entitetserviceAktig, RedigeringsomraadeAktig<Entitet> redigeringsomraade) {
        opprettLayout(entitetserviceAktig,redigeringsomraade,SplitLayout.Orientation.VERTICAL);
    }

    public void opprettLayout(EntitetserviceAktig<Entitet> entitetserviceAktig, RedigeringsomraadeAktig<Entitet> redigeringsomraade, SplitLayout.Orientation orientering){
        opprettLayout(entitetserviceAktig,redigeringsomraade,SplitLayout.Orientation.VERTICAL, 50D);
    }

    @Override
    public void opprettLayout(EntitetserviceAktig<Entitet> entitetserviceAktig,
                              RedigeringsomraadeAktig<Entitet> redigeringsomraade,
                              SplitLayout.Orientation orientering,
                              Double splitPositionDouble)
    {
        this.entitetserviceAktig = entitetserviceAktig;
        this.redigeringsomraade = redigeringsomraade;
        this.orientering = orientering;

        if (redigeringsomraade instanceof VerticalLayout) {
            this.redigeringsomraadeSomVerticalLayout = (VerticalLayout) redigeringsomraade;
        } else {
            Loggekyklop.hent().loggFEIL("Redigeringsomraade må baseres på VerticalLayout, slik Redigeringsomraademal gjør det.");
        }

        splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(splitPositionDouble);
        splitLayout.setOrientation(orientering);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(opprettSoekeomraade());
        splitLayout.addToSecondary(opprettRedigeringsomraadeMedKnapper());
        this.add(splitLayout);
        setSizeFull();

        instansOpprettGrid();
        tilpassKolonnerOgOpprettFilteradIGrid();
        instansOpprettFilterFelter();
        tilpassFilterfelterIGrid();

        //VaadinSession.getCurrent().setErrorHandler(new HallvardsErrorHandler());
        oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        redigeringsomraade.aktiver(false);
    }


    public Grid<Entitet> hentGrid(){
        return grid;
    }



    private void tilpassKolonnerOgOpprettFilteradIGrid(){
        List<Grid.Column<Entitet>> kolonner = grid.getColumns();
        for (Grid.Column<Entitet> kol:kolonner){
            kol.setResizable(true);
            kol.setSortable(true);
        }
        grid.setItems(entitetserviceAktig.finnAlle());
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSizeFull();

        grid.getHeaderRows().clear();
        headerRowFilterfelter = grid.appendHeaderRow();

    }

    @Override
    public <C extends Component> C leggTilFilterfelt(Integer cellIndex, C component, String feltnavn) {
        if (component instanceof HasPlaceholder) {
            ((HasPlaceholder) component).setPlaceholder(feltnavn);
        }

        if (component instanceof DatePicker) {
            Datokyklop.hent().fiksDatoformat((DatePicker)component);
        } else if (component instanceof DateTimePicker) {
            Datokyklop.hent().fiksDatotidformat((DateTimePicker)component);
        }

        headerRowFilterfelter.getCells().get(cellIndex).setComponent(component);

        return component;
    }

    @Override
    public HeaderRow getHeaderRowFilterfelter(){
        return headerRowFilterfelter;
    }


    private void tilpassFilterfelterIGrid() {
        List<HeaderRow> headerRows = grid.getHeaderRows();
        List<HeaderRow.HeaderCell> celler = headerRows.getLast().getCells();
        for (HeaderRow.HeaderCell cell : celler) {
            if (cell.getComponent() instanceof TextField) {
                TextField textFieldFilter = (TextField) cell.getComponent();
                textFieldFilter.setValueChangeMode(ValueChangeMode.LAZY);
                textFieldFilter.setWidthFull();
                textFieldFilter.setClearButtonVisible(true);
                textFieldFilter.addValueChangeListener(e -> settFilter());

            } else if (cell.getComponent() instanceof ComboBox) {
                ComboBox<Entitet> comboBox = (ComboBox<Entitet>) cell.getComponent();
                comboBox.setWidthFull();
                comboBox.setClearButtonVisible(true);
                comboBox.addValueChangeListener(e -> settFilter());

            } else if (cell.getComponent() instanceof DatePicker) {
                DatePicker datePicker = (DatePicker)cell.getComponent();
                datePicker.setWidthFull();
                datePicker.setLocale(Locale.forLanguageTag("no"));
//                datePicker.setLocale(Locale.of("no"));
                datePicker.setClearButtonVisible(true);
                datePicker.addValueChangeListener(e -> settFilter());

            } else if (cell.getComponent() instanceof Checkbox) {
                BooleanCombobox booleanCombobox = new BooleanCombobox();
                booleanCombobox.setWidthFull();
                booleanCombobox.addValueChangeListener(e -> settFilter());
                booleanCombobox.setClearButtonVisible(true);
                cell.setComponent(booleanCombobox);


            } else if (cell.getComponent() instanceof IntegerField) {
                IntegerField integerField = (IntegerField) cell.getComponent();
                integerField.setValueChangeMode(ValueChangeMode.LAZY);
                integerField.setWidthFull();
                integerField.setClearButtonVisible(true);
                integerField.addValueChangeListener(e -> settFilter());

            } else if (cell.getComponent() instanceof NumberField) {
                NumberField numberField = (NumberField) cell.getComponent();
                numberField.setValueChangeMode(ValueChangeMode.LAZY);
                numberField.setWidthFull();
                numberField.setClearButtonVisible(true);
                numberField.addValueChangeListener(e -> settFilter());
            }
        }
    }

    @Override
    public RedigeringsomraadeAktig<Entitet> hentRedigeringsomraadeAktig(){
        return redigeringsomraade;
    }


    private VerticalLayout opprettSoekeomraade(){
        VerticalLayout verticalLayout = new VerticalLayout();
        layoutSoekeknapper = new HorizontalLayout();
        opprettSoekeomraade_leggTilKnapperOgTittel();
        layoutSoekeknapper.setWidthFull();


        grid = new Grid<>();
        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                redigeringsomraade.setEntitet(e.getFirstSelectedItem().get());
                viewCRUDStatus = ViewCRUDStatusEnum.POST_KAN_REDIGERES;
                oppdaterRedigeringsomraade();
            }
        });
        grid.setSizeFull();

        verticalLayout.add(layoutSoekeknapper, grid);
        verticalLayout.setSizeFull();
        return verticalLayout;


    }

    @Override
    public H2 hentVindutittel() {
        return vinduTittel;
    }

    @Override
    public SubMenu hentVerktoeySubMeny(){
        return verktoeySubMenu;
    }

    private void opprettSoekeomraade_leggTilKnapperOgTittel(){
        vinduTittel = new H2(entitetserviceAktig.hentEntitetsnavn());

        verktoeyMenuBar = new MenuBar();
        verktoeyMenuBar.addThemeVariants(MenuBarVariant.LUMO_DROPDOWN_INDICATORS);
        verktoeyMenuItem = verktoeyMenuBar.addItem("Verktøy");
        verktoeySubMenu = verktoeyMenuItem.getSubMenu();

        MenuItem opprettTestdataMenuItem = verktoeySubMenu.addItem("Opprett testdata");
        opprettTestdataMenuItem.addClickListener(e -> {
            entitetserviceAktig.opprettTestdata();
            grid.setItems(entitetserviceAktig.finnAlle());
            oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        });

        MenuItem slettAlleMenuItem = verktoeySubMenu.addItem("Slett alle");
        slettAlleMenuItem.addClickListener(e -> {
            entitetserviceAktig.slettAlle();
            grid.setItems(entitetserviceAktig.finnAlle());
            oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        });

        MenuItem eksporterTilCSVMenuItem = verktoeySubMenu.addItem("Eksporter til CSV");
        eksporterTilCSVMenuItem.addClickListener(e -> {
            String strEntitetsnavn = TekstKyklop.hent().hentSisteIStrengMedDelimiter(entitetserviceAktig.opprettEntitet().getClass().getName());
            CSVEksportkyklop.hent().eksporterArrayListTilTekst(strEntitetsnavn,entitetserviceAktig.finnAlle());
        });

        MenuItem importerFraCSVMenuItem = verktoeySubMenu.addItem("Importer fra CSV");
        importerFraCSVMenuItem.addClickListener(e -> {
            CSVImportkyklop.hent().velgImportfilOgKjoerImport(entitetserviceAktig);
            grid.setItems(entitetserviceAktig.finnAlle());
            oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        });

        verktoeySubMenu.addSeparator();

        MenuItem byttOrienteringAvSplitLayoutMenuItem = verktoeySubMenu.addItem("Bytt orientering av layout");
        byttOrienteringAvSplitLayoutMenuItem.addClickListener(e -> {
            if (orientering==null || orientering == SplitLayout.Orientation.HORIZONTAL) {
                orientering = SplitLayout.Orientation.VERTICAL;
            } else {
                orientering = SplitLayout.Orientation.HORIZONTAL;
            }
            splitLayout.setOrientation(orientering);
        });


        /** For å teste HallvardsErrorHandler
        Button buttonError = new Button("Click me", event -> {
            throw new IllegalArgumentException("No clicking! BAD BOY!!");
        });
         */

        layoutSoekeknapper.addToStart(vinduTittel);
        layoutSoekeknapper.addToEnd(verktoeyMenuBar);
    }

    public void leggTilButtonISoekeomraadet(Button button) {
        layoutSoekeknapper.add(button);
    }

    private VerticalLayout opprettRedigeringsomraadeMedKnapper(){
        VerticalLayout verticalLayout = new VerticalLayout();
        layoutRedigeringsknapper = new HorizontalLayout();
        opprettRedigeringsknapper();


        redigeringsomraadeSomVerticalLayout.setSizeFull();
        verticalLayout.add(layoutRedigeringsknapper,redigeringsomraadeSomVerticalLayout);


        verticalLayout.setSizeFull();
        return verticalLayout;
    }



    private void opprettRedigeringsknapper(){

        nyButton = new Button("Ny");
        nyButton.addClickListener(e -> {
            opprettNyEntitet();
        });
        nyButton.addClickShortcut(Key.KEY_N, KeyModifier.CONTROL, KeyModifier.SHIFT);
        nyButton.setTooltipText("SHIFT-CTRL-N");

        lagreButton = new Button("Lagre");
        lagreButton.addClickShortcut(Key.KEY_L, KeyModifier.CONTROL, KeyModifier.SHIFT);
        lagreButton.addClickListener(e -> {
            lagreEntitet();
        });
        lagreButton.addClickShortcut(Key.KEY_L, KeyModifier.CONTROL, KeyModifier.SHIFT);
        lagreButton.setTooltipText("SHIFT-CTRL-L");
        lagreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        slettButton = new Button("Slett");
        slettButton.addClickListener(e -> {
            slettEntitet();
        });

        layoutRedigeringsknapper.addToEnd(nyButton, lagreButton, slettButton);
    }

    @Override
    public Button hentSlettButton() {
        return slettButton;
    }

    @Override
    public Button hentLagreButton() {
        return lagreButton;
    }

    @Override
    public Button hentNyButton() {
        return nyButton;
    }

    private void opprettNyEntitet(){
        viewCRUDStatus = ViewCRUDStatusEnum.NY;
        Entitet entitet = entitetserviceAktig.opprettEntitet();
        redigeringsomraade.setEntitet(entitet);
        instansTilpassNyopprettetEntitet();
        oppdaterRedigeringsomraade(); // må komme før select(entitet), fordi select fyrer selectionEvent, som setter viewCRUSTATUS.KAN_REDIGERES

        if (grid.getGenericDataView() instanceof ListDataView<?,?>) {
            grid.getListDataView().addItem(entitet);
            grid.select(entitet); // fyrer SelectionEvent, som oppdaterer innholdet i Redigeringsområdet

        } else {  //LazyDataView
            //kan ikke legge til direkte, har ikke lyst til å lagre entitet og fyre setFilter heller
        }
    }

    public void lagreEntitet(){
        redigeringsomraade.skrivBean();
        entitetserviceAktig.lagre(redigeringsomraade.getEntitet());
        redigeringsomraade.lesBean();  // for å få frem oppdatert endret klokkeslett, men det kommer først senere...

//        Entitet entitetMedOppdatertDatoTid  = entitetserviceAktig.finnEtterUUID(redigeringsomraade.getEntitet().getUuid().toString());
//        if (entitetMedOppdatertDatoTid !=null) {
//            redigeringsomraade.getEntitet().setRedigertDatoTid(entitetMedOppdatertDatoTid.getRedigertDatoTid());  //Litt grisete men la gå.
//            if (redigeringsomraade.getEntitet().getOpprettetDatoTid()==null) {
//                redigeringsomraade.getEntitet().setOpprettetDatoTid(entitetMedOppdatertDatoTid.getOpprettetDatoTid());
//            }
//        }

        oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        viewCRUDStatus = ViewCRUDStatusEnum.POST_KAN_REDIGERES;
        //grid.select(redigeringsomraade.getEntitet()); // fyrer SelectionEvent, som oppdaterer innholdet i Redigeringsområdet

    }


    private void slettEntitet(){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Slette " + entitetserviceAktig.hentEntitetsnavn().toLowerCase() + "?");
        dialog.setText("Er du sikker på at du vil slette " + entitetserviceAktig.hentVisningsnavn(redigeringsomraade.getEntitet()) + "?");
        dialog.addConfirmListener(e -> {
            if (grid.getGenericDataView() instanceof ListDataView<?,?>) {
                grid.getListDataView().removeItem(redigeringsomraade.getEntitet());
            }
            entitetserviceAktig.slett(redigeringsomraade.getEntitet());
            redigeringsomraade.setEntitet(null);
            oppdaterRedigeringsomraade();
            viewCRUDStatus = ViewCRUDStatusEnum.ER_SLETTET;
            oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        });
        dialog.open();
    }




    @Override
    public void oppdaterRedigeringsomraade(){

        if (redigeringsomraade.getEntitet()==null) {
            redigeringsomraade.lesBean();
            redigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
            redigeringsomraade.aktiver(false);
            instansAktiverKnapperadRedigeringsfelt(false);

        } else {
            redigeringsomraade.lesBean();
            redigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
            redigeringsomraade.aktiver(true);
            instansAktiverKnapperadRedigeringsfelt(true);
            redigeringsomraade.fokuser();

        }

    }


    /**
     * Denne aktiverer at angitte søkmetode kjøres hver gang en av filterfeltene er endret
     */
    @Override
    public void initierCallbackDataProviderIGrid (
            @NotNull com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback<Entitet, String> fetchCallback,
            @NotNull com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback<Entitet, String> countCallback
    ){
        final CallbackDataProvider<Entitet, String> dataProvider = DataProvider.fromFilteringCallbacks(fetchCallback, countCallback);
        filterProvider = dataProvider.withConfigurableFilter();
        grid.setItems(filterProvider);
        settFilter();
    }

    public void brukFiltreIDataprovider(ArrayList<SearchCriteria> filtre) {
        brukFiltreIDataprovider(filtre, EntityFilterSpecification.OperatorEnum.AND);
    }

    public void brukFiltreIDataprovider(ArrayList<SearchCriteria> filtre, EntityFilterSpecification.OperatorEnum operatorEnum) {
        entitetserviceAktig.setEntityFilterSpecification(new EntityFilterSpecification<>(filtre, operatorEnum));
        filterProvider.setFilter(""); // for å applisere filteret. Det finnes antagelig mer elegante måter å gjøre det på
        if (this.grid.getDataProvider() instanceof ListDataView) {
            this.grid.getListDataView().refreshAll();
        } else {
            this.grid.getDataProvider().refreshAll();
        }
    }



    /**
     * Oppdatere grid når entiteten er oppdatert, f.eks. en av feltene som vises i grid
     */
    public void oppdaterSoekeomraadeEtterRedigeringAvEntitet(){

        oppdaterAntallRaderNederstIGrid();

        if (redigeringsomraade.getEntitet() != null) {
            if (grid.getDataProvider() instanceof ListDataView) {
                if (viewCRUDStatus.equals(ViewCRUDStatusEnum.POST_KAN_REDIGERES)) {
                    grid.getListDataView().refreshItem(redigeringsomraade.getEntitet());
                } else if (viewCRUDStatus == ViewCRUDStatusEnum.NY) {
                    grid.getListDataView().refreshAll();
                }
            } else {
                if (viewCRUDStatus.equals(ViewCRUDStatusEnum.POST_KAN_REDIGERES)) {
                    grid.getDataProvider().refreshItem(redigeringsomraade.getEntitet());
                } else if (viewCRUDStatus == ViewCRUDStatusEnum.NY) {
                    settFilter();

                    grid.select(redigeringsomraade.getEntitet());

                }
            }
            //scrollTilValgtRad();  // virker ikke ennå

        } else if (viewCRUDStatus==ViewCRUDStatusEnum.ER_SLETTET) {
            settFilter();
        }

    }

    @Override
    public ViewCRUDStatusEnum getCRUDStatus() {
        return viewCRUDStatus;
    }

    @Override
    public void oppdaterSoekeomraade(){
        if (grid.getDataProvider() instanceof ListDataView) {
            grid.setItems(entitetserviceAktig.finnAlle());
        } else {
            settFilter();
        }

        if (redigeringsomraade.getEntitet()!=null) {
            grid.select(redigeringsomraade.getEntitet());
        }
    }

    private void scrollTilValgtRad(){
        if (!grid.isDetailsVisible(redigeringsomraade.getEntitet())){
            if (grid.getDataProvider() instanceof GridListDataView<?>) {
                for (int i = 0; i < grid.getListDataView().getItemCount(); i++) {
                    if (redigeringsomraade.getEntitet().getUuid().equals(grid.getListDataView().getItem(i).getUuid())) {
                        grid.scrollToIndex(i);
                    }
                }
            } else if (grid.getDataProvider()!=null) {
//                int antallRader = entitetserviceAktig.tellAntallMedSpecification();  // VIRKER IKKE ENNÅ
//                for (int i = 0; i < antallRader; i++) {
//                    if (redigeringsomraade.getEntitet().getUuid().equals(grid.getDataProvider().)) {
//                        grid.scrollToIndex(i);
//                    }
//                }
            }
        }
    }

    private void oppdaterAntallRaderNederstIGrid(){
        Grid.Column<Entitet> column = grid.getColumns().getFirst();
        if (grid.getDataProvider() instanceof GridListDataView<?>) {
            column.setFooter("Antall: " + grid.getListDataView().getItemCount());
        } else {
            int antallRader = entitetserviceAktig.tellAntallMedSpecification();
            column.setFooter("Antall: " + antallRader);
        }
    }

    /**
     * Brukes etter at entiteter er lagt til fra andre steder, f.eks. Dialogbokser
     * Erstatt med oppdaterSoekeomraade
     * @param entitet
     */
    @Deprecated
    @Override
    public void oppdaterSoekeomraade_finnAlle(Entitet entitet){
        oppdaterSoekeomraadeOgRedigeringsomraadeMedNyEntitet(entitet);
    }

    @Override
    public void oppdaterSoekeomraadeOgRedigeringsomraadeMedNyEntitet(Entitet entitet) {
        redigeringsomraade.setEntitet(entitet);
        if (grid.getGenericDataView() instanceof ListDataView<?,?>) {
            grid.setItems(entitetserviceAktig.finnAlle());
            grid.getDataProvider().refreshAll();
        } else {
            settFilter();
        }
        grid.select(redigeringsomraade.getEntitet());

        oppdaterAntallRaderNederstIGrid();
    }


}
