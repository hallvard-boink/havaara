import com.hallvardlaerum.libs.bilder.OrienteringEnum;
import org.junit.jupiter.api.Test;

public class CSVImportkyklopTest {


    public CSVImportkyklopTest() {
    }


    @Test
    public void sammenlignEnum(){
        OrienteringEnum orienteringEnum = OrienteringEnum.LANDSKAP;
        String landskapString = "LANDSKAP";
        assert orienteringEnum.toString().equals(landskapString);

    }





}
