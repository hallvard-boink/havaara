package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.filerogopplasting.LastOppFilDialogAktig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.streams.UploadMetadata;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

public class KomprimerBildeDialog extends Dialog {
    private UploadMetadata opplastetfilMetadata;
    private byte[] opplastetfilData;
    private Integer breddeGammel;
    private Integer breddeNy;
    private Integer hoeydeGammel;
    private Integer hoeydeNy;
    private ByteArrayInputStream byteArrayInputStream;
    private StoerrelseEnum valgtStoerrelseEnum;
    private OrienteringEnum orienteringEnum;
    private BufferedImage image;
    private File nyForminsketFilFile;
    private LastOppFilDialogAktig hovedDialog;
    private VerticalLayout verticalLayout;
    private FormLayout formLayout;
    private Span tekstSpan;


    public KomprimerBildeDialog(LastOppFilDialogAktig hovedDialog) {
        this.hovedDialog = hovedDialog;
        this.opplastetfilData = hovedDialog.getOpplastetfilData();
        this.opplastetfilMetadata = hovedDialog.getOpplastetfilMetadata();

        opprettLayout();

    }

    public void vis(){
        Bildekyklop.hent().initierFraLastOppFilDialog(opplastetfilData,opplastetfilMetadata,hovedDialog);
        oppdaterTekst();
        this.open();
    }

    private void opprettLayout(){
        verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        add(verticalLayout);

        opprettLayout_opprettTittelOgTekst();

        formLayout = new FormLayout();
        verticalLayout.add(formLayout);

        opprettLayout_opprettStoerrelseButtons();
        opprettLayout_opprettAvbrytButton();

    }

    private void opprettLayout_opprettAvbrytButton(){
        ButtonMedEnum avbrytButton = new ButtonMedEnum(StoerrelseEnum.ORIGINAL);
        avbrytButton.setText("Avbryt");
        avbrytButton.addClickListener(e -> close());
        formLayout.add(avbrytButton);
    }

    private void opprettLayout_opprettStoerrelseButtons(){
        for (StoerrelseEnum stoerrelse : StoerrelseEnum.values()) {
            ButtonMedEnum buttonMedEnum = new ButtonMedEnum(stoerrelse);
            buttonMedEnum.addClickListener(e -> {
                valgtStoerrelseEnum = ((ButtonMedEnum)e.getSource()).stoerrelseEnumHosButton;
                Bildekyklop.hent().finnNyBreddeogHoyde(valgtStoerrelseEnum);
                hovedDialog.settSkalKomprimeresOgOppdaterLayout(true);
                close();
            });
            formLayout.add(buttonMedEnum);
        }
    }

    private void opprettLayout_opprettTittelOgTekst(){
        setHeaderTitle("Velg forminsking av opplastet bilde");

        tekstSpan = new Span("<init>");
        verticalLayout.add(tekstSpan);
    }

    private void oppdaterTekst(){
        tekstSpan.setText("Bildet " + hovedDialog.getFilnavnString() + " er litt stort (" +
                breddeGammel + " x " + hoeydeGammel + ", " +
                Filkyklop.hent().konverterBytesTilMb(opplastetfilMetadata.contentLength()) + " Mb), " +
                "og det blir litt stort. " +
                "Velg ny størrelse på forminsket bilde, eller behold originalen.");
    }

    private class ButtonMedEnum extends Button {
        StoerrelseEnum stoerrelseEnumHosButton;

        public ButtonMedEnum(StoerrelseEnum stoerrelseEnum) {
            this.stoerrelseEnumHosButton = stoerrelseEnum;
            this.setText(stoerrelseEnum.getTittel());
        }
    }



    // Denne kjøres fra LastOppFilDialog - flyttes til Bildekyklop
//    public void lagreOpplastetFilMedKomprimering(){
//
//        BufferedImage scaledImage = new BufferedImage(breddeNy, hoeydeNy, BufferedImage.TYPE_INT_ARGB);
//        Float faktorBredde = (float)breddeNy/breddeGammel;
//        Float faktorHoeyde = (float)hoeydeNy/hoeydeGammel;
//
//
//        final AffineTransform at = AffineTransform.getScaleInstance(faktorBredde,faktorHoeyde);
//        final AffineTransformOp ato = new AffineTransformOp(at,AffineTransformOp.TYPE_BICUBIC);
//        scaledImage = ato.filter(image,scaledImage);
//
//        nyForminsketFilFile = Filkyklop.hent().settSammenFile(hovedDialog.getFilnavnString(), hovedDialog.getFilkategori());
//        try {
//            ImageIO.write(scaledImage, "jpg",nyForminsketFilFile);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    //    private void finnOrientering(){
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(opplastetfilData);
//        try {
//            image = ImageIO.read(byteArrayInputStream);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        hoeydeGammel = image.getHeight();
//        breddeGammel = image.getWidth();
//
//        if (hoeydeGammel>breddeGammel) {
//            orienteringEnum = OrienteringEnum.PORTRETT;
//        } else {
//            orienteringEnum = OrienteringEnum.LANDSKAP;
//        }
//    }

    // Skal flyttes til Bildekyklop
//    private void finnNyBreddeogHoyde(){
//        Float bildeFormat = (float)hoeydeGammel/breddeGammel;
//
//        if (orienteringEnum == OrienteringEnum.LANDSKAP) {
//            breddeNy = Math.min(breddeGammel, valgtStoerrelseEnum.getHorisontalPixler());
//            hoeydeNy = Integer.parseInt(Integer.toString(Math.round(breddeNy*bildeFormat)));
//            //hoeydeNy = Math.min(hoeydeGammel, valgtStoerrelseEnum.vertikalPixler);
//        } else { //PORTRETT
//            hoeydeNy = Math.min(hoeydeGammel, valgtStoerrelseEnum.getHorisontalPixler());
//            breddeNy = Integer.parseInt(Integer.toString(Math.round(hoeydeNy*bildeFormat)));
//        }
//    }




// SLETTES
    // Ved å bruke java.imageio.ImageIO:
//    File imageFile = new File("C:\\path\\to\\pdf\\image.tif");
//    BufferedImage image = ImageIO.read(imageFile);
//    final int w = image.getWidth();
//    final int h = image.getHeight();
//    BufferedImage scaledImage = new BufferedImage((w * 2), (h * 2), BufferedImage.TYPE_INT_ARGB);
//    final AffineTransform at = AffineTransform.getScaleInstance(2.0, 2.0);
//    final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
//    scaledImage = ato.filter(image, scaledImage);



//        try {
//            Image imageReduced = image.getScaledInstance(breddeNy,hoeydeNy,Image.SCALE_SMOOTH);
//
//            // Denne metoden gir kanskje dårlig kvalitet, må testes
//
//            File outputFile = new File(opplastetfilMetadata.fileName());
//            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputFile);
//            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
//            writer.setOutput(imageOutputStream);
//
//            ImageWriteParam param = writer.getDefaultWriteParam();
//            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//            param.setCompressionQuality(0.5f); // Adjust quality (0.0 to 1.0)
//
//            writer.write(null, new IIOImage((RenderedImage) imageReduced, null, null), param);
//            imageOutputStream.close();
//            writer.dispose();
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

}
