import com.hallvardlaerum.libs.felter.Datokyklop;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class DatokyklopTest {

    //@Test
    public void finnSisteIMaanedenTest(){
        LocalDate forventetDato = LocalDate.of(2025,11,30);
        LocalDate testDato = LocalDate.of(2025,11,15);
        LocalDate utregnetDato = Datokyklop.hent().finnSisteIMaaneden(testDato);
        assert(utregnetDato.isEqual(forventetDato));

        forventetDato = LocalDate.of(2025,12,31);
        testDato = LocalDate.of(2025,12,30);
        utregnetDato =Datokyklop.hent().finnSisteIMaaneden(testDato);
        assert(utregnetDato.isEqual(forventetDato));

        forventetDato = LocalDate.of(2022,02,28);
        testDato = LocalDate.of(2022,02,1);
        utregnetDato =Datokyklop.hent().finnSisteIMaaneden(testDato);
        System.out.println("Testdato " + testDato + ", forventetDato " + forventetDato+ ", utregnetDato: " + utregnetDato);
        assert(utregnetDato.isEqual(forventetDato));


    }

    //@Test
    public void opprettDatoSomYYYY_MM_DDTest() {
        LocalDate forventetLocalDate = LocalDate.parse("2025-01-25");
        LocalDate utregnetLocalDate = Datokyklop.hent().opprettDatoSomYYYY_MM_DD("2025-01-25");
        assert(forventetLocalDate.isEqual(utregnetLocalDate));

    }

    //@Test
    public void opprettDatoSom_DDpMMpYYYYTest() {
        LocalDate forventetLocalDate = LocalDate.parse("2025-01-25");
        LocalDate utregnetLocalDate = Datokyklop.hent().opprettDatoSom_DDpMMpYYYY("25.01.2025");
        assert(forventetLocalDate.isEqual(utregnetLocalDate));

    }

    //@Test
    public void formaterDatoSomMaanedstekstAar(){
        LocalDate dato = LocalDate.parse("2025-01-25");
        String utregnetdatoString = Datokyklop.hent().formaterLocalDate_MaanedsnavnAar(dato);
        String forventetdatoString = "Januar 2025";
        System.out.println(utregnetdatoString + " vs " + forventetdatoString);
        assert(utregnetdatoString.equals(forventetdatoString));

    }

}
