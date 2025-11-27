import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.filerogopplasting.Filkategori;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FilkyklopTest {
    ArrayList<File> filer;

//    @Test
//    public void konverterBytesTilMbTest() {
//        Long filstoerrelseBytes = 10000000L;
//        //Float filstorrelseMb = 9.536743F;
//        Float filstorrelseMb = Filkyklop.hent().konverterBytesTilMb(filstoerrelseBytes);
//        System.out.println("Bytes " + filstoerrelseBytes + " er " + filstorrelseMb + " i Mb");
//    }
//
//    @Test
//    public void settSammenFilnavnOgTilleggTest() {
//        System.out.println(Filkyklop.hent().settSammenFilnavnOgTillegg("MyFile.txt","",""));
//        System.out.println(Filkyklop.hent().settSammenFilnavnOgTillegg("MyFile.txt","","01"));
//        System.out.println(Filkyklop.hent().settSammenFilnavnOgTillegg("MyFile.txt","A","01"));
//        System.out.println(Filkyklop.hent().settSammenFilnavnOgTillegg("MyFile.txt","B","01"));
//    }
//
//    @Test
//    public void printStartPathTest() {
//        Filkyklop.hent().printAbsolutePath("");
//        Filkyklop.hent().printAbsolutePath("./");
//        Filkyklop.hent().printAbsolutePath("./src");
//    }
//
//    //@Test
//    public void printInnholdIMappeTest() {
//        Filkyklop.hent().printInnholdIMappe(Filkategori.BILDE.getMappeNavn());
//    }
//
//
//    //@Test
//    public void finnUniktFilnavnTest() {
//        opprettTestfiler();
//        Filkyklop.hent().printInnholdIMappe(Filkategori.BILDE.getMappeNavn());
//        File filSomFinnes = new File("test_04.jpg");
//        File filMedNyttNavn = Filkyklop.hent().opprettFilMedUniktNavn(filSomFinnes.getName(), Filkategori.BILDE);
//        File filMedNyttNavn2 = Filkyklop.hent().opprettFilMedUniktNavn(filSomFinnes.getName(), Filkategori.BILDE);
//        System.out.println("=== Oppretter test_04,jpg med nytt navn ===");
//        try {
//            filMedNyttNavn.createNewFile();
//            filer.add(filMedNyttNavn);
//            filMedNyttNavn2.createNewFile();
//            filer.add(filMedNyttNavn2);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Filkyklop.hent().printInnholdIMappe(Filkategori.BILDE.getMappeNavn());
//        slettTestfiler();
//    }
//
//    private void slettTestfiler(){
//        for (File fil:filer) {
//            fil.delete();
//        }
//    }
//
//    private void opprettTestfiler(){
//        filer = new ArrayList<>();
//        for (int i = 1;i<100; i++) {
//            opprettNyFil("test_04_" + HelTallMester.integerFormatertSom00(i) + ".jpg",Filkategori.BILDE);
//        }
//    }
//
//    private void opprettNyFil(String strFilnavn, Filkategori filkategori) {
//        String strPath = filkategori.getMappeNavn() + "/" + strFilnavn;
//        File file = new File(strPath);
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        filer.add(file);
//    }
}
