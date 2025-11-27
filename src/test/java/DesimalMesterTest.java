import com.hallvardlaerum.libs.feiloglogging.TestMester;
import com.hallvardlaerum.libs.felter.DesimalMester;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class DesimalMesterTest {


    @Test
    public void konverterBigdecimalTilIntegerTest() {
        BigDecimal testBigDecimal = new BigDecimal("123.89");
        Integer forventetInteger = 124;
        Integer utregnetInteger = DesimalMester.konverterBigdecimalTilInteger(testBigDecimal);
        TestMester.logOgAssertInteger(testBigDecimal.toPlainString(), forventetInteger,utregnetInteger);
    }

    //@Test
    public void brukToDesimalerTest() {
        Double originalDouble = 12.34567d;
        Double forventetDouble = 12.35d;
        Double funnetDouble = DesimalMester.brukToDesimaler(originalDouble);

        TestMester.logOgAssertDouble(originalDouble.toString(), forventetDouble, funnetDouble);

    }

    //@Test
    public void konverterDoubleTilFormatertIntegerSomStrengTest() {
        Double originalDouble = 12345.34567d;
        String forventetString = "12 345";
        String funnetString = DesimalMester.konverterDoubleTilFormatertIntegerSomStreng(originalDouble);

        TestMester.logOgAssertString(originalDouble.toString(), forventetString, funnetString);
    }

}
