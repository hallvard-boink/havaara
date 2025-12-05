package com.hallvardlaerum.libs.feiloglogging;

public class HavaaraVersjonskyklop extends Versjonskyklopmal{
    private static HavaaraVersjonskyklop havaaraVersjonskyklop;

    public void printVersjon(){
        System.out.println(hentSisteVersjonSomStreng());
    }

    //TODO: Forenkle enda mer, f.eks. å opprette filterfelter og kolonner i grid ut fra entity


    @Override
    public void byggOppVersjoner() {
        super.leggTilVersjon("1.7.2","2025-05-12","Allvitekyklop","Har tatt i bruk allvitekyklop for mer kontrollert initiering, " +
                "men før endret constructor til alle maler i havaara.");
        super.leggTilVersjon("1.7.1", "2025-11-17","CSVImportMester",
                "Tilpasningsmuligheten for CSVImportkyklop hang igjen mellom ulike bruksscenarier, som medførte " +
                        "at importdataene havnet i feil databasetabell. Gikk vekk fra singletonmønsteret og brukte vanlig objekt " +
                        "istedet. Har @Deprecated i overgangen, for å unngå å bryte alt samtidig.");

        super.leggTilVersjon("1.7.0", "2025-11-26","Havaara i helt eget prosjekt",
                "Det viste seg å være upraktisk å ha havaara som modul i et multimodulprosjekt sammen med testkode. " +
                        "Mye arbeid med å oppdatere testkoden, som var kopi av blaahvalen. Hadde ebslading som testprosjekt før, men da måtte " +
                        "jeg vedlikeholde mye testkode for å teste det som var relevant, og jeg måtte uansett kjøre install og recompile " +
                        "for å ta i bruk den nye koden i havvara.");

        super.leggTilVersjon("1.6.4", "2025-11-18","Bedre formlayout m.m.",
                "Må kunne lage tabeller");

        super.leggTilVersjon("1.6.3", "2025-11-11","Bugfiks, bl.a. MasterDetailView",
                "Fikset feil i generering av Tabs, og gjorde flere komponenter tilgjengelig for andre klasser.");

        super.leggTilVersjon("1.6.2", "2025-11-07","Paged search, Importlogg til fil",
                "Paged search tillater store datamengder. Ingen av importrutinene gir kræsj, skriver til en tekstfil i stedet i mappen 'logs'. ");

        super.leggTilVersjon("1.6.0", "2025-10-23","Backup på tomcat virker",
                "Har funnet ut av rettigheter, path og sånn-");

        super.leggTilVersjon("1.4.0", "2025-10-09","Restore fra backup.zip og mer robust import ",
                "Viser nå alle felter i 'Om...'");

        super.leggTilVersjon("1.3.2", "2025-10-08","Fikse krøll med avhengigheter",
                "Etter mye tull med java generics virker det endelig, også med oppstart med 0 poster og feil dato i versjonskyklop.");

        super.leggTilVersjon("1.3.1", "2025-10-04","Rydde bildefunksjonalitet",
                "Nå er det mulig å støtte bilder på fire måter. 1) Visningskomponent (bare visning fra filnavn), " +
                        "2) Opplastingskomponent (visning og opplasting), 3) MultiBildekomponent (egen Bildeentitet, vises i " +
                        " samme redigeringsområde som forelderEntiteten) og 4) BildeRedigeringsomraadeFrittstaaende (bildene har egen View)"
        );

        super.leggTilVersjon("1.3.0.0", "2025-09-18","Redusere boilerplate-kode",
                "EntitetserviceMal reduserer hvor mye 'boilerplate'-kode brukeren må legge inn i hver Entitetservice");
        super.leggTilVersjon("1.2.3.0", "2025-08-23","Opprettet generisk MultiBildeKomponent med AbstraktBildeentitet",
                "Multibildekomponent lar utvikleren koble til entitet som håndterer opplasting av bildefiler til server. Komponenten håndterer like filnavn og kan forminske bilder");
        super.leggTilVersjon("1.2.2.0", "2025-07-16","Opprettet generisk BarneGridMal og ryddet i bean injection. Nå er det MainView som sørger for initiering av entitetservicer og dermed sikrer bean injection.",
                "Barnegridmal skal gjøre det mulig å legge til en redigerbar liste over barna på alle entiteter som har slik kobling.");
        super.leggTilVersjon("1.2.1.2", "2025-07-15","Omgjorde SlettEntitetDialog og RedigerEntitetDialog til vanlige klasser",
                "Rotet en del med mange abstraheringslag, men landet på dette.");
        super.leggTilVersjon("1.2.1.1", "2025-07-14","Bugfix",
                "Fikset noen feil, bl.a. oppdatering etter redigering fra barnegrid.");
        super.leggTilVersjon("1.2.1.0", "2025-07-08","Utskillelse av Redigeringsområde fra View",
                "Måtte skille ut gjenbrukbare elementer fra View.");
        super.leggTilVersjon("1.2.0.0", "2025-07-05","Automatisk backup til mappe på serveren (forberedelse)",
                "En time etter siste endring i databasen skal det lastes opp en zipfil med full klartekst backup til jottacloud. Id er flyttet til AbstraktEntitet.");
        super.leggTilVersjon("1.1.0.9", "2025-07-04","Versjoner, zip og backup",
                "Gjør det mulig å laste ned en zippet fil til brukerens filsystem. La til Versjonskyklopmal til bruk i hver applikasjon. Dette gjør det enkelt å vise versjonshistorikk i brukergrensesnittet.");
        super.leggTilVersjon("1.1.0.8", "2025-07-02", "Ny Clipboardkyklop",
                "Kyklop som kan kopiere tekst til clipboard ved hjelp av et litt hacka javascript");
        super.leggTilVersjon("1.1.0.0","2025-06-31","Endret struktur av AbstraktEntitet",
                "Id ble flyttet tilbake til hver entitet. Fikset en vanskelig bug hvor det ikke lot seg gjøre å opprette relasjoner");
        super.leggTilVersjon("1.0.0", "2025-06-30","Første utgave av Havaara med versjonskyklop",
                "MasterDetailView gjør det mulig å lage nye vinduer med CRUD for utvalgt entitet veldig raskt");
    }


    public static VersjonskyklopAktig hent(){
        if (havaaraVersjonskyklop == null) {
            havaaraVersjonskyklop = new HavaaraVersjonskyklop();
        }
        return havaaraVersjonskyklop;
    }


    private HavaaraVersjonskyklop() {
        super.setApplikasjonsNavnString("Havaara");
        byggOppVersjoner();
    }

}
