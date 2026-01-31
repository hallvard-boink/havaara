import com.hallvardlaerum.libs.database.EntitetAktig;
import org.junit.jupiter.api.Test;

public class EntitetAktigTest {

    @Test
    public void trekkUtUUID(){
        String originalString = "UUID:iRlvn4kmwseujvn-w49xkljfwekv-NMkejvnjd";
        String forventetString = "iRlvn4kmwseujvn-w49xkljfwekv-NMkejvnjd";
        String utregnetString = EntitetAktig.trekkUtUUID(originalString);
        assert (forventetString.equals(utregnetString));
    }

}
