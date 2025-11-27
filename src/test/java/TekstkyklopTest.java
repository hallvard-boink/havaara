import com.hallvardlaerum.libs.felter.TekstKyklop;
import org.junit.jupiter.api.Test;

public class TekstkyklopTest {


    @Test
    public void fjernEscapecharacterFraStreng() {
        String tekst = "\\.";
        String tekst_forventet = ".";
        String tekst_funnet = TekstKyklop.hent().fjernEscapecharacterFraStreng(tekst);
        printTestresultat(tekst,tekst_forventet,tekst_funnet);
        assert tekst_funnet.equals(tekst_forventet);

        String tekst2 = "\\.\\.";
        String tekst2_forventet = "..";
        String tekst2_funnet = TekstKyklop.hent().fjernEscapecharacterFraStreng(tekst2);
        printTestresultat(tekst2,tekst2_forventet,tekst2_funnet);
        assert tekst2_funnet.equals(tekst2_forventet);

    }

    @Test
    public void hentKlassenavn(){
        String strTekst = "medlem@abc-534-222";
        String strForventet = "medlem";

        String strResultat = TekstKyklop.hent().hentFoersteDelAvStrengMedDelimiter(strTekst,"@");
        assert strResultat.equals(strForventet);
    }

    @Test
    public void finnSistIRekkenTest() {

        String strTekst = "medlem";
        String strResultat = "";
        String strForventet = "medlem";

        strResultat = TekstKyklop.hent().hentSisteIStrengMedDelimiter(strTekst);
        assert strResultat.equals(strForventet);


        strTekst = "første.andre.tredje.medlem";
        strResultat = TekstKyklop.hent().hentSisteIStrengMedDelimiter(strTekst);
        assert strResultat.equals(strForventet);

    }

    @Test
    public void fjernSisteDelAvStrengMedDelimiterTest() {
        String tekst = "første.andre.tredje.jpg";
        String tekst_forventet = "første.andre.tredje";
        String tekst_funnet = TekstKyklop.hent().fjernSisteDelAvStrengMedDelimiter(tekst,"\\.");
        printTestresultat(tekst,tekst_forventet,tekst_funnet);
        assert tekst_funnet.equals(tekst_forventet);

    }

    @Test
    public void konverterFilnavnTilTittelTest() {
        String tekstOriginal = "vw-id7-blaa-forfra.jpeg";
        TekstKnippe tekstKnippe1 = new TekstKnippe(tekstOriginal, "Vw id7 blaa forfra", TekstKyklop.hent().konverterFilnavnTilTittel(tekstOriginal));
        tekstKnippe1.printTestresultat();
        tekstKnippe1.assertEqual();

        String tekstOriginal2 = "IMG_7450.JPG";
        TekstKnippe tekstKnippe2 = new TekstKnippe(tekstOriginal2,"IMG 7450",TekstKyklop.hent().konverterFilnavnTilTittel(tekstOriginal2));
        tekstKnippe2.printTestresultat();
        tekstKnippe2.assertEqual();

        String tekstOriginal3 = "hallvard.JPG";
        TekstKnippe tekstKnippe3 = new TekstKnippe(tekstOriginal3,"Hallvard",TekstKyklop.hent().konverterFilnavnTilTittel(tekstOriginal3));
        tekstKnippe3.printTestresultat();
        tekstKnippe3.assertEqual();

        String tekstOriginal4 = "første.andre.tredje-som-også_må-med.JPG";
        TekstKnippe tekstKnippe4 = new TekstKnippe(tekstOriginal4,"Første andre tredje som også må med",TekstKyklop.hent().konverterFilnavnTilTittel(tekstOriginal4));
        tekstKnippe4.printTestresultat();
        tekstKnippe4.assertEqual();

    }

    @Test
    public void settStorForbokstavTest() {
        String tekst = "denne skal ha stor forbokstav";
        String tekst_Forventet = "Denne skal ha stor forbokstav";
        String tekst_Funnet = TekstKyklop.hent().settStorForbokstav(tekst);
        printTestresultat(tekst,tekst_Forventet,tekst_Funnet);


    }

    private void printTestresultat(TekstKnippe tekstKnippe){
        printTestresultat(tekstKnippe.tekstOriginal, tekstKnippe.tekstForventet, tekstKnippe.tekstFunnet);
    }

    private void printTestresultat(String tekstOriginal, String tekstForventet, String tekstFunnet) {
        System.out.println();
        System.out.println("Original tekst: " + tekstOriginal);
        System.out.println("Forventet: " + tekstForventet);
        System.out.println("Funnet: " + tekstFunnet);
    }

    private class TekstKnippe {
        String tekstOriginal;
        String tekstForventet;
        String tekstFunnet;

        public TekstKnippe(String tekstOriginal, String tekstForventet, String tekstFunnet) {
            this.tekstOriginal = tekstOriginal;
            this.tekstForventet = tekstForventet;
            this.tekstFunnet = tekstFunnet;
        }

        public void assertEqual(){
            assert tekstFunnet.equals(tekstForventet);
        }

        public void printTestresultat(){
            System.out.println();
            System.out.println("Original tekst: " + tekstOriginal);
            System.out.println("Forventet: " + tekstForventet);
            System.out.println("Funnet: " + tekstFunnet);
        }
    }

}
