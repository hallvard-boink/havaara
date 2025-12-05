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

public abstract class RedigeringsomraadeMal<T extends EntitetAktig>
        extends VerticalLayout implements RedigeringsomraademalAktig<T> {
    private T entitet;
    private Binder<T> binder;
    private Component fokusComponent;

    private DateTimePicker opprettetDatoTid;
    private DateTimePicker redigertDatoTid;
    private FormLayout formLayout;
    private VerticalLayout underFelterVerticalLayout;
    private VerticalLayout overFelterVerticalLayout;
    private ViewmalAktig<?> delAvView;
    private TabSheet tabSheet;
    private final String hovedtabnavnString="Hoved";

    public RedigeringsomraadeMal() {
        opprettHovedlayout();
        binder = new Binder<>();
    }

    private void opprettHovedlayout(){
        overFelterVerticalLayout = new VerticalLayout();
        overFelterVerticalLayout.setMargin(false);
        overFelterVerticalLayout.setPadding(false);
        overFelterVerticalLayout.setWidthFull();

        formLayout = new FormLayout();

        underFelterVerticalLayout = new VerticalLayout();
        underFelterVerticalLayout.setMargin(false);
        underFelterVerticalLayout.setPadding(false);
        underFelterVerticalLayout.setWidthFull();

        add(overFelterVerticalLayout, formLayout, underFelterVerticalLayout);

        this.setPadding(false);
        this.setMargin(false);
    }


    @Override
    public Tab opprettTab(String tittelString) {
        if (tabSheet == null) {
            VerticalLayout hovedTabVerticalLayout = new VerticalLayout();
            hovedTabVerticalLayout.setSizeFull();
            hovedTabVerticalLayout.add(formLayout);
            tabSheet = new TabSheet();
            //tabSheet.add("Hoved",hovedTabVerticalLayout);
            tabSheet.setSizeFull();
            removeAll();
            add(overFelterVerticalLayout, tabSheet, underFelterVerticalLayout);
        }

        VerticalLayout tabVerticalLayout = new VerticalLayout();
        tabVerticalLayout.setSizeFull();

        FormLayout formLayout = new FormLayout();
        formLayout.setAutoResponsive(true);
        formLayout.setColumnWidth("8em");
        formLayout.setExpandColumns(true);
        formLayout.setExpandFields(true);
        formLayout.setWidthFull();
        tabVerticalLayout.add(formLayout);
        return tabSheet.add(tittelString,tabVerticalLayout);

    }

    @Override
    public FormLayout hentFormLayoutFraTab(Integer tabIndex){
        if (tabSheet == null) {
            opprettTab(hovedtabnavnString);
        }
        return hentFormLayoutFraTab(tabSheet.getTabAt(tabIndex));
    }

    @Override
    public FormLayout hentFormLayoutFraTab(String tabTittelString){
        if(tabSheet==null) { // Ingen tab er opprettet, siden TabSheet uansett er null
            tabSheet = new TabSheet();
            hentFormLayoutFraTab(opprettTab(tabTittelString));
        }

        for (int i = 0; i<tabSheet.getTabCount(); i++) {
            Tab tab = tabSheet.getTabAt(i);
            if (tab.getLabel().equals(tabTittelString)) {
                return hentFormLayoutFraTab(tab);
            }
        }
        // Tab ikke funnet, oppretter ny
        return hentFormLayoutFraTab(opprettTab(tabTittelString));
    }

    private FormLayout hentFormLayoutFraTab(Tab tab){
        Component tabContent = tabSheet.getComponent(tab);
        if (tabContent instanceof VerticalLayout) {
            VerticalLayout verticalLayout = (VerticalLayout) tabContent;
            return verticalLayout.getChildren()
                    .filter(component -> component instanceof FormLayout)
                    .map(component -> (FormLayout) component)
                    .findFirst()
                    .orElseGet(() -> {
                        // If no FormLayout exists, create one and add it
                        FormLayout formLayout = new FormLayout();
                        verticalLayout.add(formLayout);
                        return formLayout;
                    });

        }


        return (FormLayout) tab.getChildren().filter(c -> c.getClass().getName().equals(FormLayout.class.getName())).toList().getFirst();
    }

    @Override
    public void settView(ViewmalAktig<?> delAvView){
        this.delAvView = delAvView;
    }

    public ViewmalAktig<?> hentView() {
        return delAvView;
    }

    @Override
    public void leggTilDatofeltTidOpprettetOgRedigert(){
        leggTilDatofeltTidOpprettetOgRedigert(hovedtabnavnString);
    }

    @Override
    public void leggTilDatofeltTidOpprettetOgRedigert(String tabString){
        FormLayout formLayout = hentFormLayoutFraTab(tabString);

        opprettetDatoTid = new DateTimePicker("Opprettet dato");
        redigertDatoTid = new DateTimePicker("Redigert dato");
        leggTilRedigeringsfelter(tabString, opprettetDatoTid, redigertDatoTid);

        binder.bind(opprettetDatoTid, T::getOpprettetDatoTid, T::setOpprettetDatoTid);
        binder.bind(redigertDatoTid, T::getRedigertDatoTid, T::setRedigertDatoTid);

        opprettetDatoTid.setEnabled(false);
        redigertDatoTid.setEnabled(false);
    }

    @Override
    public void settColspan(Component komponent, Integer intSpan) {
        formLayout.setColspan(komponent,intSpan);
    }


    /**
     * Legg til alle komponentene som skal i samme rad samtidig, og bruk setColspan(komponent, intSpan) etterpå.
     * @param components
     */
    public void leggTilRedigeringsfelter(String tabString, Component... components){
        FormLayout formLayout = hentFormLayoutFraTab(tabString);
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
        leggTilRedigeringsfelter(hovedtabnavnString, components);
    }


    private <C extends Component> C leggTilRedigeringsfelt_faktisk(FormLayout formLayout, C komponent)  {
        formLayout.add(komponent);
        if (komponent instanceof DatePicker) {
            Datokyklop.hent().fiksDatoformat((DatePicker) komponent);
        } else if (komponent instanceof  DateTimePicker) {
            Datokyklop.hent().fiksDatotidformat((DateTimePicker) komponent);
        }
        return komponent;
    }




    @Override
    public <C extends Component> C leggTilRedigeringsfelt(C komponent) {
        if (formLayout==null) {
            formLayout = hentFormLayoutFraTab(hovedtabnavnString);
        }
        return leggTilRedigeringsfelt_faktisk(formLayout, komponent);
    }

    @Override
    public <C extends Component> C leggTilRedigeringsfelt(Integer tabIndex, C komponent) {
        if (komponent==null || tabIndex==null) {return null;}

        FormLayout formLayout = hentFormLayoutFraTab(tabIndex);
        if (formLayout==null) {
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
    public Binder<T> hentBinder() {
        if (binder==null) {
            binder = new Binder<>();
        }
        return binder;
    }

    @Override
    public T getEntitet() {
        return entitet;
    }

    @Override
    public void setEntitet(T entitet) {
        this.entitet = entitet;
    }


    @Override
    public void setFokusComponent(Component fokusComponent) {
        if (fokusComponent instanceof Focusable<?>) {
            this.fokusComponent = fokusComponent;
            fokusComponent.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.PRIMARY);
        } else {
            Loggekyklop.hent().loggFEIL("Du kan ikke sette komponenter av klassen " + fokusComponent.getClass().getName() + " som fokusComponent, fordi de støtter ikke focus()");
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
