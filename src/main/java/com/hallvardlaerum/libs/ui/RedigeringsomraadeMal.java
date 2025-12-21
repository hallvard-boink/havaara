package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.theme.lumo.LumoUtility;

public abstract class RedigeringsomraadeMal<Entitet extends EntitetAktig>
        extends VerticalLayout
        implements RedigeringsomraademalAktig<Entitet> {
    private Entitet entitet;
    private Binder<Entitet> binder;
    private Component fokusComponent;

    private DateTimePicker opprettetDatoTid;
    private DateTimePicker redigertDatoTid;
    private VerticalLayout kjerneVerticalLayout;
    private TabSheet tabSheet;
    private FormLayout formLayoutKjerne;
    private VerticalLayout underFelterVerticalLayout;
    private VerticalLayout overFelterVerticalLayout;
    private ViewmalAktig<?,?> delAvView;
    private final String hovedtabnavnString="Hoved";


    public RedigeringsomraadeMal() {

    }

    @Override
    public void initRedigeringsomraadeMal(){
        opprettHovedlayout();
        binder = new Binder<>();
    }

    private void opprettHovedlayout(){
        overFelterVerticalLayout = new VerticalLayout();
        overFelterVerticalLayout.setMargin(false);
        overFelterVerticalLayout.setPadding(false);
        overFelterVerticalLayout.setWidthFull();

        kjerneVerticalLayout = new VerticalLayout();
        kjerneVerticalLayout.setSizeFull();
        formLayoutKjerne = new FormLayout();
        formLayoutKjerne.setWidthFull();
        kjerneVerticalLayout.add(formLayoutKjerne);

        underFelterVerticalLayout = new VerticalLayout();
        underFelterVerticalLayout.setMargin(false);
        underFelterVerticalLayout.setPadding(false);
        underFelterVerticalLayout.setWidthFull();

        add(overFelterVerticalLayout, kjerneVerticalLayout, underFelterVerticalLayout);

        this.setPadding(false);
        this.setMargin(false);
    }


    @Override
    public Tab opprettTabOgEvtTabSheet(String tittelString) {

        if (tabSheet == null) {
            opprettTabSheet();
        }

        FormLayout formLayout = opprettFormLayoutMedPentUtseende();
        Tab tab = tabSheet.add(tittelString,formLayout);
        tab.setLabel(tittelString);
        return tab;
    }

    private void opprettTabSheet(){
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        kjerneVerticalLayout.add(tabSheet);
    }

    private FormLayout opprettFormLayoutMedPentUtseende(){
        FormLayout formLayout = new FormLayout();
        formLayout.setAutoResponsive(true);
        formLayout.setColumnWidth("8em");
        formLayout.setExpandColumns(true);
        formLayout.setExpandFields(true);
        formLayout.setWidthFull();
        return formLayout;
    }

    @Override
    public FormLayout hentFormLayoutFraTab(Integer tabIndex){
        if (tabSheet == null) {
            opprettTabOgEvtTabSheet(hovedtabnavnString);
        }
        Tab tab = tabSheet.getTabAt(tabIndex);
        if (tab == null) {
            Loggekyklop.bruk().loggFEIL("Fant ingen tab med index " + tabIndex + ", avbryter");
            return null;
        } else {
            return hentFormLayoutFraTab(tab);
        }
    }

    @Override
    public FormLayout hentFormLayoutFraTab(String tabTittelString){
        if(tabSheet==null) {
            opprettTabOgEvtTabSheet(hovedtabnavnString);
        }

        for (int i = 0; i<tabSheet.getTabCount(); i++) {
            Tab tab = tabSheet.getTabAt(i);
            if (tab.getLabel().equals(tabTittelString)) {
                return hentFormLayoutFraTab(tab);
            }
        }
        // Tab ikke funnet, oppretter ny
        return hentFormLayoutFraTab(opprettTabOgEvtTabSheet(tabTittelString));
    }

    private FormLayout hentFormLayoutFraTab(Tab tab){
        Component tabContent = tabSheet.getComponent(tab);

        if (tabContent instanceof FormLayout) {
            return (FormLayout) tabContent;
        } else {
            Loggekyklop.bruk().loggFEIL("Komponenten på øverste nivå for tab " + tab.getLabel() + " er " + tabContent.getClassName() + ", ikke Formlayout");
            return null;
        }

    }

    @Override
    public void settView(ViewmalAktig<?,?> delAvView){
        this.delAvView = delAvView;
    }

    public ViewmalAktig<?,?> hentView() {
        return delAvView;
    }

    @Override
    public void leggTilDatofeltTidOpprettetOgRedigert(){
        leggTilDatofeltTidOpprettetOgRedigert(null);
    }

    @Override
    public void leggTilDatofeltTidOpprettetOgRedigert(String tabString){
        FormLayout formLayout;
        if (tabString==null)  {
            formLayout = formLayoutKjerne;
        } else {
            formLayout = hentFormLayoutFraTab(tabString);
        }

        opprettetDatoTid = new DateTimePicker("Opprettet dato");
        redigertDatoTid = new DateTimePicker("Redigert dato");
        leggTilRedigeringsfelter_faktisk(formLayout, opprettetDatoTid, redigertDatoTid);

        binder.bind(opprettetDatoTid, Entitet::getOpprettetDatoTid, Entitet::setOpprettetDatoTid);
        binder.bind(redigertDatoTid, Entitet::getRedigertDatoTid, Entitet::setRedigertDatoTid);

        opprettetDatoTid.setEnabled(false);
        redigertDatoTid.setEnabled(false);
    }

    @Override
    public void settColspan(Component komponent, Integer intSpan) {
        FormLayout formLayout = finnFormlayout(komponent);
        if (formLayout!=null) {
            formLayout.setColspan(komponent,intSpan);
        }
    }

    private FormLayout finnFormlayout(Component komponent) {
        if (komponentenFinnesIFormlayout(komponent, formLayoutKjerne)) {
            return formLayoutKjerne;
        }

        for (int i = 0; i<tabSheet.getTabCount(); i++) {
            FormLayout formLayoutITab = hentFormLayoutFraTab(i);
            if (komponentenFinnesIFormlayout(komponent, formLayoutITab)) {
                return formLayoutITab;
            }
        }

        Loggekyklop.bruk().loggFEIL("Komponenten du prøver å finne er ikke lagt til noe sted");
        return null;

    }

    private boolean komponentenFinnesIFormlayout(Component component, FormLayout formLayout) {
        return formLayout.getChildren().anyMatch(c -> c.getId().equals(component.getId()));
    }


    /**
     * Legg til alle komponentene som skal i samme rad samtidig, og bruk setColspan(komponent, intSpan) etterpå.
     * @param components
     */
    public void leggTilRedigeringsfelter(String tabString, Component... components){
        FormLayout formLayout = hentFormLayoutFraTab(tabString);
        leggTilRedigeringsfelter_faktisk(formLayout,components);
    }

    private void leggTilRedigeringsfelter_faktisk(FormLayout formLayout, Component... components) {
        formLayout.addFormRow(components);

        for (Component component:components) {
            if (component instanceof DatePicker) {
                Datokyklop.hent().fiksDatoformat((DatePicker) component);
            } else if (component instanceof  DateTimePicker) {
                Datokyklop.hent().fiksDatotidformat((DateTimePicker) component);
            }
        }
    }


    @Override
    public void leggTilRedigeringsfelter(Component... components) {
        leggTilRedigeringsfelter_faktisk(formLayoutKjerne, components);
    }


    private <C extends Component> C leggTilRedigeringsfelt_faktisk(FormLayout formLayout, C komponent)  {
        formLayout.addFormRow(komponent);

        if (komponent instanceof DatePicker) {
            Datokyklop.hent().fiksDatoformat((DatePicker) komponent);
        } else if (komponent instanceof  DateTimePicker) {
            Datokyklop.hent().fiksDatotidformat((DateTimePicker) komponent);
        }
        return komponent;
    }




    @Override
    public <C extends Component> C leggTilRedigeringsfelt(C komponent) {
        return leggTilRedigeringsfelt_faktisk(formLayoutKjerne, komponent);
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt(Integer tabIndex, C komponent) {
        if (komponent==null || tabIndex==null) {return null;}

        FormLayout formLayout = hentFormLayoutFraTab(tabIndex);
        if (formLayout==null) {
            Loggekyklop.bruk().loggFEIL("Fant ingen tab på tabIndex " + tabIndex + ", avbryter");
            return null;
        } else {
            return leggTilRedigeringsfelt_faktisk(formLayout, komponent);
        }
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt(String tabTittelString, C komponent) {
        if (komponent==null || tabTittelString==null) {return null;}
        FormLayout formLayout = hentFormLayoutFraTab(tabTittelString);
        if (formLayout==null) {
            return null;
        } else {
            return leggTilRedigeringsfelt_faktisk(formLayout, komponent);
        }
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt( Tab tab, C komponent) {
        if (komponent==null || tab==null) {return null;}
        FormLayout formLayout = hentFormLayoutFraTab(tab);
        if (formLayout==null) {
            return null;
        } else {
            return leggTilRedigeringsfelt_faktisk(formLayout, komponent);
        }
    }


    @Override
    public <C extends Component> C leggTilAndrefelterUnder(C komponent) {
        underFelterVerticalLayout.add(komponent);
        return komponent;
    }

    @Override
    public <C extends Component> C leggTilAndrefelterOver(C komponent) {
        overFelterVerticalLayout.add(komponent);
        return komponent;
    }



    @Override
    public void lesBean() {
        binder.readBean(entitet);
    }

    @Override
    public void skrivBean() {
        try {
            binder.writeBean(entitet);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Binder<Entitet> hentBinder() {
        if (binder==null) {
            Loggekyklop.bruk().loggFEIL("Binder er tom, men skulle vært initiert");
        }
        return binder;
    }

    public void settBinder(Binder<Entitet> binder) {
        this.binder = binder;
    }

    @Deprecated
    @Override
    public Entitet getEntitet() {
        return entitet;
    }

    @Deprecated
    @Override
    public void setEntitet(Entitet entitet) {
        this.entitet = entitet;
    }

    @Override
    public Entitet hentEntitet(){
        return entitet;
    }

    @Override
    public void settEntitet(Entitet entitet) {
        this.entitet = entitet;
    }


    @Override
    public void settFokusKomponent(Component fokusComponent){
        if (fokusComponent instanceof Focusable<?>) {
            this.fokusComponent = fokusComponent;
        } else {
            Loggekyklop.bruk().loggFEIL("Du kan ikke sette komponenter av klassen " + fokusComponent.getClass().getName() + " som fokusComponent, fordi de støtter ikke focus()");
        }
    }

    @Deprecated
    @Override
    public void setFokusComponent(Component fokusComponent) {
        if (fokusComponent instanceof Focusable<?>) {
            this.fokusComponent = fokusComponent;
            //fokusComponent.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.PRIMARY);
        } else {
            Loggekyklop.bruk().loggFEIL("Du kan ikke sette komponenter av klassen " + fokusComponent.getClass().getName() + " som fokusComponent, fordi de støtter ikke focus()");
        }
    }

    @Override
    public void fokuser(){
        if(fokusComponent !=null) {
            ((Focusable<?>) fokusComponent).focus();
        }
    }

    @Override
    public void aktiver(Boolean blnAktiver){
        for (Component c : getChildren().toList()){
            if (c instanceof HasEnabled) {
                ((HasEnabled) c).setEnabled(blnAktiver);
            }
            if (c instanceof Paragraph) {
                if (blnAktiver){
                    c.setClassName(LumoUtility.TextColor.BODY);
                } else {
                    c.setClassName(LumoUtility.TextColor.DISABLED);
                }
            }
        }

        delAvView.hentLagreButton().setEnabled(blnAktiver);
        delAvView.hentSlettButton().setEnabled(blnAktiver);
    }



    @Override
    public VerticalLayout hentOverFelterVerticalLayout() {
        return overFelterVerticalLayout;
    }

    @Override
    public VerticalLayout hentUnderFelterVerticalLatout() {
        return underFelterVerticalLayout;
    }
}
