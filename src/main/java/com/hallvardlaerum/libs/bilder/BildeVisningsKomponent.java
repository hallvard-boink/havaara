package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkategori;
import com.vaadin.flow.component.html.Image;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

//TODO: Legg til tittel og omgjør til VerticalLayout, eller lage en ny komponent med Layout?

public class BildeVisningsKomponent extends Image implements BildeVisningsKomponentAktig {

    public BildeVisningsKomponent() {
        this.setMaxHeight("800px");
        this.setMaxWidth("800px");
        //this.setSizeFull();
    }

    @Override
    public void byggOppBildeFraBytes(byte[] data){
        if (data==null) {
            setSrc("");
        } else {
            setSrc("data:image;base64," + Base64.getEncoder().encodeToString(data));
        }

    }

    @Override
    public void hentBildeFraFilnavn(String strFilnavn) {
        if (strFilnavn!=null && !strFilnavn.isEmpty()) {
            Path path = Paths.get(Filkategori.BILDE.getMappeNavn() + "/" + strFilnavn);
            if (path.toFile().exists()) {
                try {
                    byggOppBildeFraBytes(Files.readAllBytes(path));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Loggekyklop.hent().loggADVARSEL("Filen finnes ikke");
                byggOppBildeFraBytes(null);
            }

        } else {
            byggOppBildeFraBytes(null);
        }
    }






}
