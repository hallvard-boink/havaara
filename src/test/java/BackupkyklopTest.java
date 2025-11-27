import com.hallvardlaerum.libs.ui.UIKyklop;
import com.hallvardlaerum.libs.verktoy.Backupkyklop;
import org.junit.jupiter.api.Test;


import java.io.File;

public class BackupkyklopTest {

    //@Test
    public void startBackupdemonTest() {

        Backupkyklop.hent().startBackupdemon();
        UIKyklop.hent().vent(10000);

        Backupkyklop.hent().stoppBackupdemon();

    }

    //@Test
    public void hentBackupFilerTest() {
        File[] filer = Backupkyklop.hent().hentBackupFiler();
        for (File fil:filer) {
            System.out.println(fil.getName());
        }

    }
}
