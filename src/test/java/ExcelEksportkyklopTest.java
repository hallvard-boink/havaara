import com.hallvardlaerum.libs.eksportimport.ExcelEksportkyklop;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Arrays;

public class ExcelEksportkyklopTest {

    //@Test
    public void eksporterArrayListAvStrengerSomXLSTest() {
        ArrayList<ArrayList<String>> rader = new ArrayList<>();
        rader.add(new ArrayList<>(Arrays.asList("Byer","Nedbør")));
        rader.add(new ArrayList<>(Arrays.asList("Oslo","500")));
        rader.add(new ArrayList<>(Arrays.asList("Bergen","8000")));
        rader.add(new ArrayList<>(Arrays.asList("Trondheim","4000")));

        ExcelEksportkyklop.hent().eksporterArrayListAvStrengerSomXLS(rader,"Byer i Norge");
    }


}

