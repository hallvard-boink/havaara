# Havaara v1.2

Rammeverk for rask utvikling av pene og praktiske Vaadin-applikasjoner
Utviklet av Hallvard Lærum Juni 2025 - d.d.

## Overordnet struktur
Havaara bruker noen få sentrale abstrakte klasser til å gi standardfunksjonalitet som navigering,
søkbare lister, eksport/import til tekstfiler (CSV), CRUD (altså create, read, update, delete) og 
en infoside om applikasjonen din. Alle klasser, metoder og egenskaper har norske navn, slik at de skilles lett
fra annen javakode.

### Klasseinnsprøytning (Bean injection)
Hver applikasjon må ha et View som utvider MainViewmal og har merkelappen @Route uten parameter. Ved oppstart er det 
meningen at denne klassen er startpunktet for klasseinnsprøytning, og sørger for at andre entitetservicene blir opprettet, 
og får den innsprøytningen de skal ha. 

Entitetservice skal motta alle klasser som har med entiteten å gjøre, ved hjelp av klasseinnsprøytning. Deretter skal
de kunne tilgjengeliggjøre dette videre. Dette vil bety at alle klasser går til EntitetService for å hente
RedigeringsområdeAktige eller Viewmalaktige klasser. Hver entitetservice ber også om innsprøytning av andre entitetservicer de trenger.


### Entiteter
En entitet er her en enkel javaklasse ("bean") har merkelappen @Entity og som utvider AbstraktEntitet. Den har egen tabell 
i databasen, og lagres her. Hvert entitetredigeringsområde tar vare på sin egen entitet. Dette betyr at vi går til 
redigeringsområdet for å finne entiteten som er funnet frem eller opprettet, og at det er mulig å lage Views som kombinerer
ulike redigeringsområder med hver sin entitet.


### AbstraktEntitet (abstrakt klasse)
Alle entiteter må ekstendere denne. Bruker UUID som id, slik at importer via CSV kan få med seg relasjonskoblinger. 
Har felles utgave av toString, som er viktig i eksport/import.
Bruker AbstraktEntitetAktig, som også krever hentBeskrivendeNavn(). Denne sørger for at hver instans av en entitet kan 
vise et navn eller en tittel som angir entiteten entydig, og gir mening for brukeren. F.eks. registringsnummeret til entiteten Bil. 
 
### MainViewMal (abstrakt klasse)
Ekstenderes av MainView. Gir en felles start, og sørger for felles funksjonalitet under "Om..." - vinduet 

### Hvordan legge til nye entiteter
1. Opprett entitet som vanlig javabean merket @Entity og som utvider AbstraktEntitet. Navnet på entiteten blir heretter referert til med teksten "|Entitet|"
2. Opprett interface kalt |Entitet|Repository som utvider JpaRepository<|Entitet|, UUID>, JpaSpecificationExecutor<|Entitet|> og RepositoryTillegg<|Entitet|>
3. Opprett klasse kalt |Entitet|Service merket @Service som ekstenderer EntitetserviceMal<|Entitet|,|Entitet|Repository>. Implementer metodene.
4. Opprett klasse kalt |Entitet|Redigeringsomraade som ekstenderer RedigeringsomraadeMal og implementerer implements RedigeringsomraadeAktig<|Entitet|>. Implementer metodene og initier |Entitet|Service med den. Legg til redigeringsfelter og bygg opp binder.
5. Opprett klasse kalt |Entitet|View (eller lignende) som ekstenderer MasterDetailView



### Ordliste
Jeg har prøvd å være konsistent i oversettelsen fra engelske til norske termer, og har brukt følgende
oversettelser:

| **Engelsk term** | **Norsk oversettelse** |
|:-----------------|------------------------|
| Entity           | Entitet                |
| -able            | -aktig                 |
| Singleton        | Kyklop                 |
| Template         | Mal                    |
| Extend           | Forlenge               |
| Implement        | Implementere           |    
| Annotation       | Merkelapp              |
| Parameter        | Parameter              |
| Bean injection   | Klasseinnsprøytning    |
| File             | fil                    |
| Fil path         | filsti                 |




# Fremgangsmåte

## Forberedelser
Opprett en ny Vaadin-app, og fjern overflødige klasser. La innholdet under mappen "base" bli værende. 
Lag en ny mappestruktur som følger, under com.hallvardlaerum:

| Mappe         | Forventet innhold   |
|:--------------|:--------------------|
| data          | entiteter og enums  |
| services      | serviceentiteter    |
| uikomponenter | redigeringsområder  |
| views         | vinduer             |



## Tilpass MainView.class
Gå inn i mappen base.ui, og slett constructor i klassen MainView. La klassen utvide MainViewMal, <br/>
og legg til følgende annotering:
````
@Route
@PermitAll // When security is enabled, allow all authenticated users
@Menu(title = "Om..." ,order = 99, icon="vaadin:clipboard-text")
````
Velg et passende ikon fra https://vaadin.com/docs/latest/components/icons/default-icons

Lag en ny constructor for MainView som tar i mot alle serviceklassene i applikasjonen med bean injection.<br/> 
Legg til og tilpass følgende kode (laget for Gjenstand):

````
   
    public MainView(GjenstandService gjenstandService) {
        super();
        
        UI.getCurrent().access(() -> {  // Ensure we're attaching components in a UI thread
            Loggekyklop.hent().settNivaaFEIL();  //Kan også settes til DEBUG eller INFO 
            
            gjenstandService.initier();
           
            Backupkyklop.hent().leggTilEntitetservice(gjenstandService);
        
            opprettLayout(Versjonskyklop.hent());
            
        });
    }

 

    public static void showMainView() { // Også for å unngå "not in context"-feilmeldingene
        UI current = UI.getCurrent();
        if (current != null) {
            current.access(() -> current.navigate(MainView.class));
        }
    }
````
Det oppretter innholdet i MainView ("Om...")<br/>
Initier deretter alle serviceklassene. Hovedhensikten er å legge til serviceklassene for foreldre eller <br/> 
barne-entiteter.


## Legg til entiteter
Legg til entitetene du trenger, under mappen "data". Legg til @Entity foran klassen, og legg til alle feltene. Marker
feltene som skal være med under en eksport med @SkalEksporteres. Legg til getters og setters.

#### Når entiteten er Forelder
Implementer EntitetMedBarnAktig<T>, og bytt ut T med klassen til barnet
Lag et felt som er en liste, og annoter den med 
````
@OneToMany(fetch = FetchType.EAGER, mappedBy = "<forelderklassens felt for barnet>", cascade = CascadeType.REMOVE)
````

#### Når entiteter er Barn
Implementer EntitetMedForelderAktig<T>, og bytt ut T med klassen Forelder <br/>
Lag et felt som er klassen til Forelder, og annoter den med 
````
@ManyToOne(targetEntity = Forelder.class)
````
Husk: Utvid også serviceklassen med EntitetserviceMedForelderAktig<Entitet, Forelder>


## Lag repository-interface
Lag en interface med entitetsnavnet og postfix "Repository". Utvid RepositoryMal<Entitet>  <br/>
Legg til eventuelle automatiske sammensatte søk etter behov.  <br/>
Se JPA-spesifikasjon for å finne ut hvordan navnene skal være i så fall.

## Lag serviceklasse
Lag en serviceklasse for hver entitet, og utvid med EntitetserviceMal<Entitet>. Annoter klassen med @Service .
Opprett constructor og sett inn entitetens repository med bean injection.
Implementer abstrakte metoder ved hjelp av repository-interfacen. Her er et eksempel:

````
public class GjenstandService  extends EntitetserviceMal<Gjenstand, GjenstandRepository> implements EntitetserviceAktig<Gjenstand>{

    public GjenstandService(GjenstandRepository gjenstandRepository) {
        super(gjenstandRepository);
    }

    @Override
    public Gjenstand opprettEntitet() {
        return leggTilUUID(new Gjenstand());                 
    }

    // Denne er ikke strengt nødvendig
    @Override
    public void opprettTestdata() {
        bilRepository.save(new Bil(UUID.randomUUID(), "EL74354"));
        bilRepository.save(new Bil(UUID.randomUUID(), "EL74355"));
        bilRepository.save(new Bil(UUID.randomUUID(), "EE12345"));
        bilRepository.save(new Bil(UUID.randomUUID(), "EP54321"));
        bilRepository.flush();

    }

}
````


## Lag redigeringsområde
Lag et redigeringsområde per entitet. Utvid med RedigeringsomraadeMal<Entitet>. Annoter med @Component
Implementer de abstrakte metodene.
Implementer også en init-metode som tar imot nødvendige serviceklasser, f.eks. med liste over barneentiteter

Eksempel med Medlem:

````
    public void initier(MedlemService medlemService, BilService bilService) {
        this.bilService = bilService;
        this.medlemService = medlemService;
        if (navnTextField==null) {
            opprettFelter();
            byggOppBinder();
        }
    }

    @Override
    public void byggOppBinder(){
        super.hentBinder().bind(navnTextField, Medlem::getNavn, Medlem::setNavn);
        super.hentBinder().bind(adresseTextField, Medlem::getAdresse, Medlem::setAdresse);
        super.hentBinder().bind(mobilTextField, Medlem::getMobiltelefon, Medlem::setMobiltelefon);
        super.hentBinder().bind(epostEmailField, Medlem::getEpostadresse, Medlem::setEpostadresse);
        super.hentBinder().bind(statusComboBox, Medlem::getMedlemsstatus, Medlem::setMedlemsstatus);
        super.hentBinder().bind(kanVisesEnumComboBox, Medlem::getKanVisesEnum, Medlem::setKanVisesEnum);
        super.hentBinder().bind(notaterTextArea, Medlem::getNotater, Medlem::setNotater);
        super.hentBinder().bind(startdatoDatePicker, Medlem::getStartdato, Medlem::setStartdato);
        super.hentBinder().bind(sluttdatoDatePicker, Medlem::getSluttdato, Medlem::setSluttdato);
        super.hentBinder().bind(filstitilbildeTextField, Medlem::getFilstiTilBilde, Medlem::setFilstiTilBilde);
    }

    @Override
    public void opprettFelter() {
        navnTextField = super.leggTilRedigeringsfelt(new TextField("Navn"));
        mobilTextField = super.leggTilRedigeringsfelt(new TextField("Mobiltelefon"));
        adresseTextField = super.leggTilRedigeringsfelt(new TextField("Adresse"));
        epostEmailField = super.leggTilRedigeringsfelt(new EmailField("Epost"));
        statusComboBox = super.leggTilRedigeringsfelt(new ComboBox<>("Status"));
        statusComboBox.setItems(MedlemsstatusEnum.values());
        statusComboBox.setItemLabelGenerator(MedlemsstatusEnum::getTittel);

        kanVisesEnumComboBox = super.leggTilRedigeringsfelt(new ComboBox<>("Kan vises?"));
        kanVisesEnumComboBox.setItems(KanVisesEnum.values());
        kanVisesEnumComboBox.setItemLabelGenerator(KanVisesEnum::getTittel);
        notaterTextArea = super.leggTilRedigeringsfelt(new TextArea("Notater"));
        super.settColspan(notaterTextArea, 2);
        notaterTextArea.setWidthFull();

        startdatoDatePicker = super.leggTilRedigeringsfelt(new DatePicker("Start dato"));
        sluttdatoDatePicker = super.leggTilRedigeringsfelt(new DatePicker("Slutt dato"));

        super.leggTilDatofeltTidOpprettetOgRedigert();


        filstitilbildeTextField = super.leggTilRedigeringsfelt(new TextField("Opplastet bilde"));
        filstitilbildeTextField.setVisible(false);


        bildeOpplastingsKomponent = new BildeOpplastingsKomponent((RedigeringsomraadeAktig) this, this);
        super.leggTilAndrefelterUnder(bildeOpplastingsKomponent);


        ArrayList<ValueProvider<Bil,?>> kolonneArrayList = new ArrayList<>();
        kolonneArrayList.add(Bil::getRegistreringsnummer);
        bilGrid = new BarneGridMal<>(bilService, medlemService,kolonneArrayList);
        super.leggTilAndrefelterUnder(bilGrid);

        super.setFokusComponent(navnTextField);
    }

````






## Lag Views
Opprett en View per entitet, og utvid f.eks. MasterDetailView. Annoter med følgende:
````
@Route("entitet")
@Menu(title="Entitet", order=2, icon = "vaadin:car")
````
Endre ikonet som du vil. Legg til serviceklasser og redigeringsområder i constructor (bean injection)

Eksempel på constructor med bil:
````
public BilView(BilService bilService, MedlemService medlemService, BilRedigeringsomraade bilredigeringsomraade, BildeAvBilenService bildeAvBilenService) {
    this.bilService = bilService;
    this.medlemService = medlemService;
    this.bilredigeringsomraade = bilredigeringsomraade;

    super.opprettLayout(bilService, bilredigeringsomraade);
    
    this.bilredigeringsomraade.setDelAvView(this);
    this.bilredigeringsomraade.initier(medlemService, bildeAvBilenService, bilService);
}

````

Implementer abstrakte metoder, f.eks. som dette eksempelet med Bil:
````
  @Override
    public void instansOpprettGrid() {
        grid = super.hentGrid();

        grid.addColumn(Bil::getRegistreringsnummer);
        grid.addColumn(Bil::getMedlemsNavn).setKey("medlem");
        grid.addColumn(Bil::getKommentar);
        grid.getListDataView().addItems(bilService.finnAlle());
    }

    @Override
    public void instansOpprettFelter() {
        //redigeringsomraade.opprettFelter();  //er allerede gjort i MedlemsView
    }

    @Override
    public void instansByggOppBinder() {
        // redigeringsomraade.byggOppBinder();  // er allerede gjort i MedlemsView
    }

    @Override
    public void instansOpprettFilterFelter() {
        registreringsnummerTextFieldFilter = super.leggTilFilterfelt(0, new TextField(), "Reg.nr.");
        medlemTextFieldFilter = super.leggTilFilterfelt(1, new TextField(), "Medlem");
        kommentarTextFieldFilter = super.leggTilFilterfelt(2, new TextField(),"Kommentar");
    }

    @Override
    public void settFilter(){
        GridListDataView<Bil> listDataView = grid.getListDataView();
        listDataView.removeFilters();

        if (!registreringsnummerTextFieldFilter.getValue().isEmpty()) {
            listDataView.addFilter(b -> super.inneholderTekst(b.getRegistreringsnummer(), registreringsnummerTextFieldFilter.getValue()));
        }

        if (!medlemTextFieldFilter.getValue().isEmpty()) {
            listDataView.addFilter(b -> super.inneholderTekst(b.getMedlemsNavn(), medlemTextFieldFilter.getValue()));
        }

        if (!kommentarTextFieldFilter.getValue().isEmpty()) {
            listDataView.addFilter(b -> super.inneholderTekst(b.getKommentar(), kommentarTextFieldFilter.getValue()));
        }
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        // trenger som regel ikke gjøre noe
    }
````


## Opprett Versjonskyklop

Opprett en klasse som utvider Versjonskyklopmal.Legg til følgende prosedyrer:

````
    public static VersjonskyklopAktig hent(){
        if (versjonskyklop == null) {
            versjonskyklop = new Versjonskyklop();
            versjonskyklop.setApplikasjonsNavn("HavaaraTest");
            versjonskyklop.byggOppVersjoner();
        }
        return versjonskyklop;
    }
    
    @Override
    public void byggOppVersjoner(){
        super.leggTilVersjon("0.5","2025-07-08","Bugfix","Ymse bugfix");
        super.leggTilVersjon("0.4","2025-07-08","Komponentbasert oppsett","Redigeringsområdet ble skilt ut fra View inn egen komponent, da fungerte det å gjenbruke innholdet");
        super.leggTilVersjon("0.3","2025-07-05","Versjoner, backup og zip","La til funksjonalitet for å eksportere alle data som semikolonseparert tekst pakket i en zip-fil som brukeren kan laste ned lokalt.");
        super.leggTilVersjon("0.1","2025-06-20","Første utgave","Testapplikasjon for å kunne gjøre tester som også håndterte databasen.");
    }

````



## Stil på TextField og den slags


1. Bruk setClassName og LumoUtility
2. Bruk addThemeVariants() og f.eks.TextAreaVariant.LUMO_ALIGN_CENTER 

