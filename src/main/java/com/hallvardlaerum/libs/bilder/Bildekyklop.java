package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.feiloglogging.LoggekyklopAktig;
import com.hallvardlaerum.libs.felter.DesimalMester;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkategori;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.filerogopplasting.LastOppFilDialogAktig;
import com.vaadin.flow.server.streams.UploadMetadata;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Bildekyklop {
    private static Bildekyklop bildekyklop;
    private BufferedImage image;

    private Integer breddeGammel;
    private Integer breddeNy;
    private Integer hoeydeGammel;
    private Integer hoeydeNy;

    private UploadMetadata opplastetfilMetadata;
    private byte[] opplastetfilData;
    private LastOppFilDialogAktig hovedDialog;
    private StoerrelseEnum valgtStoerrelseEnum;

    private OrienteringEnum orienteringEnum;

    public String hentBildedataSomStrengFraFilnavn(String strFilnavn){
        if (strFilnavn!=null && !strFilnavn.isEmpty()) {
            Path path = Paths.get(Filkategori.BILDE.getMappeNavn() + "/" + strFilnavn);
            if (path.toFile().exists()) {
                try {
                    return "data:image;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(path));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Loggekyklop.bruk().loggADVARSEL("Filen finnes ikke");
                return "";
            }
        } else {
            return "";
        }
    }


    public void initierFraLastOppFilDialog(byte[] opplastetfilData, UploadMetadata opplastetfilMetadata, LastOppFilDialogAktig hovedDialog) {
        this.opplastetfilData = opplastetfilData;
        this.opplastetfilMetadata = opplastetfilMetadata;
        this.hovedDialog = hovedDialog;
        finnOrientering();
    }

    public void oppdaterBildeentitetFraFilnavn(String filnavn, AbstraktBildeentitet bilde) {
        if (filnavn==null || filnavn.isEmpty()) {
            bilde.setFilnavn("");
            bilde.setHoeydeIPixler(null);
            bilde.setBreddeIPixler(null);
            bilde.setStoerrelseIMb(null);
            return;
        }

        File bildeFile = Filkyklop.hent().settSammenFile(filnavn, Filkategori.BILDE);
        bilde.setFilnavn(filnavn);

        // Størrelse i MB
        Float stoerrelseiMB = Filkyklop.hent().konverterBytesTilMb(bildeFile.length());


        bilde.setStoerrelseIMb(DesimalMester.brukToDesimaler(stoerrelseiMB.doubleValue()));

        // Bredde og høyde i pixler
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Files.readAllBytes(bildeFile.toPath()));
            image = ImageIO.read(byteArrayInputStream);
            bilde.setBreddeIPixler(image.getWidth());
            bilde.setHoeydeIPixler(image.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Tittel fra filnavn
        if (bilde.getTittel()==null || bilde.getTittel().isEmpty()) {
            bilde.setTittel(TekstKyklop.hent().konverterFilnavnTilTittel(filnavn));
        }

    }

    public void komprimerOgLagreBilde(File nyForminsketFilFile){

        BufferedImage scaledImage = new BufferedImage(breddeNy, hoeydeNy, image.getType());
        Float faktorBredde = (float)breddeNy/breddeGammel;
        Float faktorHoeyde = (float)hoeydeNy/hoeydeGammel;


        final AffineTransform at = AffineTransform.getScaleInstance(faktorBredde,faktorHoeyde);
        final AffineTransformOp ato = new AffineTransformOp(at,AffineTransformOp.TYPE_BICUBIC);
        scaledImage = ato.filter(image,scaledImage);

        try {
            boolean bleSkrevet = ImageIO.write(scaledImage, "jpg",nyForminsketFilFile);
            if (!bleSkrevet) {
                Loggekyklop.bruk().loggADVARSEL("Filen " + nyForminsketFilFile.getName() + " ble ikke skrevet.");
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void finnOrientering(){
        if (opplastetfilData!=null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(opplastetfilData);
            try {
                image = ImageIO.read(byteArrayInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            hoeydeGammel = image.getHeight();
            breddeGammel = image.getWidth();

            if (hoeydeGammel>breddeGammel) {
                orienteringEnum = OrienteringEnum.PORTRETT;
            } else {
                orienteringEnum = OrienteringEnum.LANDSKAP;
            }
        }
    }

    public void finnNyBreddeogHoyde(StoerrelseEnum valgtStoerrelseEnum){
        this.valgtStoerrelseEnum = valgtStoerrelseEnum;
        Float bildeFormat = (float)hoeydeGammel/breddeGammel;

        if (orienteringEnum == OrienteringEnum.LANDSKAP) {
            breddeNy = Math.min(breddeGammel, valgtStoerrelseEnum.getHorisontalPixler());
            hoeydeNy = Integer.parseInt(Integer.toString(Math.round(breddeNy*bildeFormat)));

        } else { //PORTRETT
            hoeydeNy = Math.min(hoeydeGammel, valgtStoerrelseEnum.getHorisontalPixler());
            breddeNy = Integer.parseInt(Integer.toString(Math.round(hoeydeNy*bildeFormat)));
        }
    }

    public static Bildekyklop hent(){
        if (bildekyklop == null) {
            bildekyklop = new Bildekyklop();
        }
        return bildekyklop;
    }

    private Bildekyklop(){
    }

}
