import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.feiloglogging.LoggekyklopAktig;
import com.hallvardlaerum.libs.felter.HelTallMester;
import org.junit.jupiter.api.Test;

public class HeltallMesterTest {

    //@Test
    public void integerFormatertSomStortTallTest(){
        System.out.println(HelTallMester.integerFormatertSomStortTall(0));
        System.out.println(HelTallMester.integerFormatertSomStortTall(16));
        System.out.println(HelTallMester.integerFormatertSomStortTall(2345));
        System.out.println(HelTallMester.integerFormatertSomStortTall(4200000));
    }

    //@Test
    public void formaterIntegerSom00og000Test() {
        System.out.println(HelTallMester.integerFormatertSom00(4));
        System.out.println(HelTallMester.integerFormatertSom00(23));
        System.out.println(HelTallMester.integerFormatertSom000(4));
        System.out.println(HelTallMester.integerFormatertSom000(23));
        System.out.println(HelTallMester.integerFormatertSom000(123));

    }

    //@Test
    public void konverterDesimalTilIntegerMultiTest(){

//        konverterDesimalTilIntegerDeltest("813.34",813);
//        konverterDesimalTilIntegerDeltest("813,34",813);
//        konverterDesimalTilIntegerDeltest("-813.34",-813);
//        konverterDesimalTilIntegerDeltest("-813,34",-813);
//        konverterDesimalTilIntegerDeltest("1500",1500);
        konverterDesimalTilIntegerDeltest("1 500,00",1500);


    }

    private void konverterDesimalTilIntegerDeltest(String tallString, Integer forventetInteger){
        Integer faktiskInteger = HelTallMester.konverterStrengMedDesimalTilInteger(tallString);
        if (faktiskInteger==null) {
            Loggekyklop.bruk().loggDEBUG("Klarte ikke å konvertere tallet " + tallString);
            assert(false);
        } else {
            assert (faktiskInteger.equals(forventetInteger));
        }
    }



}
