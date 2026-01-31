package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.*;
import com.hallvardlaerum.libs.eksportimport.CSVEksportkyklop;
import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * <p>Dette er en generisk klasse som nye Views i Vaadin skal ekstendere for å få en lik layout og
 * protectedmye felles prosedyrer gratis.</p>
 * Malen inneholder to deler: søkedel og redigeringssdel
 * Søkedelene har grid og filterfelter, samt en knapperad for eksport/import og testdata
 * Redigeringsdelen har knapperad med ny/lagre/slett-knapper og et entitetspesifikt redigeringsområde.
 *
 * @param <Entitet>
 */

public abstract class MasterDetailViewmal<Entitet extends AbstraktEntitet,
        Repo extends JpaRepository<Entitet, UUID> & JpaSpecificationExecutor<Entitet> & RepositoryTillegg<Entitet>>
        extends HorizontalLayout implements ViewmalAktig<Entitet, Repo> {
    private EntitetserviceAktig<Entitet, ?> entitetserviceAktig;
    protected Grid<Entitet> soekeGrid;
    protected HorizontalLayout layoutRedigeringsknapper;
    protected HorizontalLayout layoutSoekeknapper;

    protected HeaderRow headerRowFilterfelter;
    protected ViewCRUDStatusEnum viewCRUDStatus = ViewCRUDStatusEnum.UROERT;
    private RedigeringsomraadeAktig<Entitet> redigeringsomraade;
    protected VerticalLayout redigeringsomraadeSomVerticalLayout;
    private SplitLayout.Orientation orientering = SplitLayout.Orientation.HORIZONTAL;
    private ConfigurableFilterDataProvider<Entitet,Void,String> filterProvider;
    protected SplitLayout splitLayout;

    private Button nyButton = null;
    private Button lagreButton = null;
    private Button slettButton = null;

    protected MenuBar verktoeyMenuBar;
    protected MenuItem verktoeyMenuItem;
    protected SubMenu verktoeySubMenu;

    protected MenuItem opprettTestdataMenuItem;
    protected MenuItem slettAlleMenuItem;
    protected MenuItem eksporterTilCSVMenuItem;
    protected MenuItem importerFraCSVMenuItem;
    protected MenuItem byttOrienteringAvSplitLayoutMenuItem;

    private H2 vinduTittel;
    protected GridInnholdsTypeEnum gridInnholdsTypeEnum = GridInnholdsTypeEnum.ALLE_RADER_SAMTIDIG;

// endregion


// ===========================
// region 1. Init og Constructor
// ===========================

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

    @Override
    public void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig, RedigeringsomraadeAktig<Entitet> redigeringsomraade) {
        opprettLayout(entitetserviceAktig,redigeringsomraade,SplitLayout.Orientation.VERTICAL);
    }

    @Override
    public void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig, RedigeringsomraadeAktig<Entitet> redigeringsomraade, SplitLayout.Orientation orientering){
        opprettLayout(entitetserviceAktig,redigeringsomraade,SplitLayout.Orientation.VERTICAL, 50D);
    }

    @Override
    public void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig,
                              RedigeringsomraadeAktig<Entitet> redigeringsomraade,
                              SplitLayout.Orientation orientering,
                              Double splitPositionDouble) {
        opprettLayout(entitetserviceAktig,redigeringsomraade,SplitLayout.Orientation.VERTICAL, splitPositionDouble, GridInnholdsTypeEnum.ALLE_RADER_SAMTIDIG);
    }

    @Override
    public void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig,
                              RedigeringsomraadeAktig<Entitet> redigeringsomraade,
                              SplitLayout.Orientation orientering,
                              Double splitPositionDouble,
                              GridInnholdsTypeEnum gridInnholdsTypeEnum)
    {
        this.entitetserviceAktig = entitetserviceAktig;
        this.redigeringsomraade = redigeringsomraade;
        this.orientering = orientering;

        if (redigeringsomraade instanceof VerticalLayout) {
            this.redigeringsomraadeSomVerticalLayout = (VerticalLayout) redigeringsomraade;
        } else {
            Loggekyklop.bruk().loggFEIL("Redigeringsomraade må baseres på VerticalLayout, slik Redigeringsomraademal gjør det.");
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

        if (gridInnholdsTypeEnum == GridInnholdsTypeEnum.ALLE_RADER_SAMTIDIG) {
            headerRowFilterfelter = Gridkyklop.hent().alleRaderTilpassKolonnerOgOpprettFilteradIGrid(soekeGrid);
        } else if (gridInnholdsTypeEnum == GridInnholdsTypeEnum.PORSJONSVIS) {
            headerRowFilterfelter = Gridkyklop.hent().porsjonsviseRaderTilpassKolonnerOgOpprettFilteradIGrid(soekeGrid);
        } else {
            headerRowFilterfelter = Gridkyklop.hent().alleRaderTilpassKolonnerOgOpprettFilteradIGrid(soekeGrid);
        }

        instansOpprettFilterFelter();
        tilpassFilterfelterIGrid();

        //VaadinSession.getCurrent().setErrorHandler(new HallvardsErrorHandler());
        oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        redigeringsomraade.aktiver(false);
    }


    public Grid<Entitet> hentGrid(){
        return soekeGrid;
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


    protected void tilpassFilterfelterIGrid() {
        List<HeaderRow> headerRows = soekeGrid.getHeaderRows();
        List<HeaderRow.HeaderCell> celler = headerRows.getLast().getCells();
        for (HeaderRow.HeaderCell cell : celler) {
            if (cell.getComponent() instanceof TextField textFieldFilter) {
                textFieldFilter.setValueChangeMode(ValueChangeMode.LAZY);
                textFieldFilter.setWidthFull();
                textFieldFilter.setClearButtonVisible(true);
                textFieldFilter.addValueChangeListener(e -> settFilter());

            } else if (cell.getComponent() instanceof ComboBox) {
                ComboBox<Entitet> comboBox = (ComboBox<Entitet>) cell.getComponent();
                comboBox.setWidthFull();
                comboBox.setClearButtonVisible(true);
                comboBox.addValueChangeListener(e -> settFilter());

            } else if (cell.getComponent() instanceof DatePicker datePicker) {
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


            } else if (cell.getComponent() instanceof IntegerField integerField) {
                integerField.setValueChangeMode(ValueChangeMode.LAZY);
                integerField.setWidthFull();
                integerField.setClearButtonVisible(true);
                integerField.addValueChangeListener(e -> settFilter());

            } else if (cell.getComponent() instanceof NumberField numberField) {
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


    protected VerticalLayout opprettSoekeomraade(){
        opprettSoekeomraade_leggTilTittel();
        opprettSoekeomraade_leggTilVerktoyMeny();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettTestDataMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettEksporterTilCSVMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterFraCSVMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator();
        opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem();
        opprettSoekeomraade_leggTilSoekeGrid();
        return opprettSoeomraade_settSammenDetHele();
    }

    protected VerticalLayout opprettSoeomraade_settSammenDetHele(){
        layoutSoekeknapper = new HorizontalLayout();
        layoutSoekeknapper.setWidthFull();
        layoutSoekeknapper.addToStart(vinduTittel);
        layoutSoekeknapper.addToEnd(verktoeyMenuBar);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(layoutSoekeknapper, soekeGrid);
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    protected void opprettSoekeomraade_leggTilTittel(){
        vinduTittel = new H2(entitetserviceAktig.hentEntitetsnavn());
    }

    protected void opprettSoekeomraade_leggTilSoekeGrid(){
        soekeGrid = new Grid<>();
        soekeGrid.addItemClickListener(e -> {
            redigeringsomraade.setEntitet(e.getItem());
            viewCRUDStatus = ViewCRUDStatusEnum.POST_KAN_REDIGERES;
            oppdaterRedigeringsomraade();
        });
        soekeGrid.setSizeFull();
    }

    protected void opprettSoekeomraade_leggTilVerktoyMeny(){
        verktoeyMenuBar = new MenuBar();
        verktoeyMenuBar.addThemeVariants(MenuBarVariant.LUMO_DROPDOWN_INDICATORS);
        verktoeyMenuItem = verktoeyMenuBar.addItem("Verktøy");
        verktoeySubMenu = verktoeyMenuItem.getSubMenu();
    }

    protected void opprettSoekeomraade_leggTilVerktoyMeny_opprettTestDataMenuItem(){
        opprettTestdataMenuItem = verktoeySubMenu.addItem("Opprett testdata");
        opprettTestdataMenuItem.addClickListener(e -> {
            entitetserviceAktig.opprettTestdata();
            soekeGrid.setItems(entitetserviceAktig.finnAlle());
            oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        });
    }

    protected void opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMenuItem(){
        slettAlleMenuItem = verktoeySubMenu.addItem("Slett alle");
        slettAlleMenuItem.addClickListener(e -> {
            entitetserviceAktig.slettAlle();
            soekeGrid.setItems(entitetserviceAktig.finnAlle());
            oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        });
    }

    protected void opprettSoekeomraade_leggTilVerktoyMeny_opprettEksporterTilCSVMenuItem(){
        eksporterTilCSVMenuItem = verktoeySubMenu.addItem("Eksporter til CSV");
        eksporterTilCSVMenuItem.addClickListener(e -> {
            String strEntitetsnavn = TekstKyklop.hent().hentSisteIStrengMedDelimiter(entitetserviceAktig.opprettEntitet().getClass().getName());
            CSVEksportkyklop.hent().eksporterArrayListTilTekst(strEntitetsnavn,entitetserviceAktig.finnAlle());
        });
    }

    protected void opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterFraCSVMenuItem(){
        importerFraCSVMenuItem = verktoeySubMenu.addItem("Importer fra CSV");
        importerFraCSVMenuItem.addClickListener(e -> {
            new CSVImportmester().velgImportfilOgKjoerImport(entitetserviceAktig);
            soekeGrid.setItems(entitetserviceAktig.finnAlle());
            oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        });
    }

    protected void opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator(){
        verktoeySubMenu.addSeparator();
    }

    protected void opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem(){
        byttOrienteringAvSplitLayoutMenuItem = verktoeySubMenu.addItem("Bytt orientering av layout");
        byttOrienteringAvSplitLayoutMenuItem.addClickListener(e -> {
            if (orientering==null || orientering == SplitLayout.Orientation.HORIZONTAL) {
                orientering = SplitLayout.Orientation.VERTICAL;
            } else {
                orientering = SplitLayout.Orientation.HORIZONTAL;
            }
            splitLayout.setOrientation(orientering);
        });
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

        if (soekeGrid.getGenericDataView() instanceof ListDataView<?,?>) {
            soekeGrid.getListDataView().addItem(entitet);
            soekeGrid.select(entitet); // fyrer SelectionEvent, som oppdaterer innholdet i Redigeringsområdet

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
            if (soekeGrid.getGenericDataView() instanceof ListDataView<?,?>) {
                soekeGrid.getListDataView().removeItem(redigeringsomraade.getEntitet());
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
        soekeGrid.setItems(filterProvider);
        settFilter();
    }

    public void brukFiltreIDataprovider(ArrayList<SearchCriteria> filtre) {
        brukFiltreIDataprovider(filtre, EntityFilterSpecification.OperatorEnum.AND);
    }

    public void brukFiltreIDataprovider(ArrayList<SearchCriteria> filtre, EntityFilterSpecification.OperatorEnum operatorEnum) {
        entitetserviceAktig.setEntityFilterSpecification(new EntityFilterSpecification<>(filtre, operatorEnum));
        filterProvider.setFilter(""); // for å applisere filteret. Det finnes antagelig mer elegante måter å gjøre det på
        if (this.soekeGrid.getDataProvider() instanceof ListDataView) {
            this.soekeGrid.getListDataView().refreshAll();
        } else {
            this.soekeGrid.getDataProvider().refreshAll();
        }
    }











}
