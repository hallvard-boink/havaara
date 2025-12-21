package com.hallvardlaerum.libs.filerogopplasting;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.feiloglogging.LoggekyklopAktig;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Filkyklop {
    private static Filkyklop filkyklop;
    private static Float maksFilStoerrelseMb = 2.0F;  //TODO: Denne kan legges til en preferansekyklop, som lagrer i JSON-fil.
    private static File rotmappeFile = null;
    private static String rotmappeNavnString = null;

    public File hentElleropprettFil(StandardmappeEnum standardmappeEnum, String filnavnString){
        File fil = new File(Filkyklop.hent().hentEllerOpprettStandardmappe(standardmappeEnum) + "/" + filnavnString);
        if (!fil.exists()) {
            try {
                boolean okBoolean = fil.createNewFile();
                if (okBoolean) {
                    return fil;
                } else {
                    Loggekyklop.bruk().loggFEIL("Klarte ikke å opprette filen " + fil.getAbsolutePath() + ", avbryter");
                }
            } catch (IOException e) {
                Loggekyklop.bruk().loggFEIL("Feil ved oppretting av filen " + fil.getAbsolutePath() + ", avbryter");
                return null;
            }
        }

        return fil;
    }

    public File hentElleropprettFil(String filnavnString) {
        return hentElleropprettFil(StandardmappeEnum.TEMP,filnavnString);
    }


    /**
     * Hensikten med denne prosedyren er å gjøre det mulig å laste ned en fil fra server til klientmaskinen.
     * Fremgangsmåten er litt hackete.
     *
     * Prosedyrene skaper et usynlig HTML-Anchor som inneholder en synlig Button.
     * Anchor bruker en tekstlig referanse til en fil, som gjenopprettes når eksportfilen faktisk foreligger på server
     * Det betyr at banen og navnet på filen (filnavnString) må være lik i anchor og i prosedyren som oppretter innholdet,
     * f.eks. ExcelEksportkyklop.hent().eksporterArrayListAvEntiteterSomXLS
     *
     * @param filnavnString Filnavnet, med ekstensjon (.xls, for eksempel)
     * @param tekstPaaButtonString Teksten som skal vises i knappen
     * @param clicklistener Lambdauttrykk på formen buttonclickevent -> metode()
     * @return
     */

    public Anchor hentNedlastingsButtonAnchor(String filnavnString, String tekstPaaButtonString, ComponentEventListener<ClickEvent<Button>> clicklistener) {
        return hentNedlastingsButtonAnchor(filnavnString,StandardmappeEnum.TEMP,tekstPaaButtonString,clicklistener);
    }

    public Anchor hentNedlastingsButtonAnchor(String filnavnString, StandardmappeEnum standardmappeEnum, String tekstPaaButtonString, ComponentEventListener<ClickEvent<Button>> clicklistener){
        if (filnavnString==null ) {
            Loggekyklop.bruk().loggFEIL("filnavnString er null, avbryter");
            return null;
        }

        if (standardmappeEnum==null) {
            Loggekyklop.bruk().loggFEIL("StandardmappeEnum er null, avbryter");
            return null;
        }

        File fil = new File(hentEllerOpprettStandardmappe(standardmappeEnum) + "/" + filnavnString);
        if (!fil.exists()) {
            try {
                fil.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Anchor lastnedButtonAnchor = new Anchor(DownloadHandler.forFile(fil),"");
        Button lastNedBackupButton = new Button(tekstPaaButtonString);
        lastNedBackupButton.addClickListener(clicklistener);
        lastnedButtonAnchor.add(lastNedBackupButton);
        return lastnedButtonAnchor;
    }


    public File hentEllerOpprettStandardmappe(StandardmappeEnum standardmappeEnum) {
        if (rotmappeFile==null) {
            Loggekyklop.bruk().loggFEIL("RotmappeFile er ikke initiert. Du har sannsynligvis glemt å kjøre Versjonskyklop.hent().setApplikasjonsKortnavnString() ved oppstart. Avbryter");
            return null;
        }
        File mappeFile;

        try {
            mappeFile = new File(rotmappeFile.getCanonicalPath() + "/" + standardmappeEnum.getMappenavn());
        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL("Klarte ikke finne CanonicalPath for rotmappeFile. Avbryter.");
            return null;
        }

        if (mappeFile.exists()) {
            return mappeFile;
        }

        boolean okBoolean= mappeFile.mkdir();
        if (okBoolean) {
            return mappeFile;
        } else {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke opprette standardmappe " + mappeFile.getName() + " (" + standardmappeEnum.getMappenavn() + "), avbryter.");
            return null;
        }

    }


    /**
     * Denne må kjøres ved start av appen, for eksempel via Versjonskyklop.hent().setApplikasjonsKortnavnString()
     *
     * @param rotmappeString
     * @return
     */
    public boolean initierRotmappeFile(String rotmappeString){
        if (rotmappeString==null || rotmappeString.isEmpty()) {
            Loggekyklop.bruk().loggFEIL("Appnavnet er ikke oppgitt, avbryter");
            return false;
        }
        String rotmappenavnVasketString = rotmappeString.toLowerCase();

        File mappeDefaultFile = new File("./");
        File mappeDefaultCanonicalFile;
        try {
            mappeDefaultCanonicalFile = mappeDefaultFile.getCanonicalFile();
            File forelderFile = mappeDefaultFile.getCanonicalFile().getParentFile();
            if (forelderFile!=null) {
                if (forelderFile.getName().equals("webapps") || forelderFile.getName().equals("java") || forelderFile.getName().equals("havaara-parent")) {
                    //Java er mappen hvor havaara-parent ligger der jeg utvikler, og webapps er mappen til tomcat
                    if (mappeDefaultCanonicalFile.getName().equals(rotmappenavnVasketString)) { // Er allerede plassert riktig
                        rotmappeFile = mappeDefaultCanonicalFile;
                        return true;
                    } else {//Er plassert riktig, men feil navn
                        if (mappeDefaultCanonicalFile.getName().equals("havaara-parent") || mappeDefaultCanonicalFile.getName().equals("havaara")) {
                            //Litt grisete, men ok. Støtter ikke multimodulutvikling foreløpig.
                            rotmappeFile = mappeDefaultCanonicalFile;
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke hente canonicalFile fra mappeDefaultFile. Er ikke så farlig, fortsetter.");
        }

        File mappeDefaultTomcat10File = new File("/opt/tomcat10/webapps/" + rotmappenavnVasketString);
        if (mappeDefaultTomcat10File.exists()) {
            rotmappeFile = mappeDefaultTomcat10File;
            return true;
        }

        File mappeDefaultTomcat11File = new File("/opt/tomcat11/webapps/" + rotmappenavnVasketString);
        if (mappeDefaultTomcat11File.exists()) {
            rotmappeFile = mappeDefaultTomcat11File;
            return true;
        }

        File mappeDefaultTomcatFile = new File("/opt/tomcat/webapps/"+ rotmappenavnVasketString);
        if (mappeDefaultTomcatFile.exists()) {
            rotmappeFile = mappeDefaultTomcatFile;
            return true;
        }

        Loggekyklop.bruk().loggFEIL("Du må installere tomcat under /opt, slik at applikasjonene kommer under /opt/tomcat/webapps " +
                "eller /opt/tomcat10/webapps. Avbryter");

        return false;

    }



    public boolean slettFileneIMappe(Path mappePath){

        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(mappePath);
            for (Path entry:directoryStream) {
                boolean done = entry.toFile().delete();
                if (!done){
                    Loggekyklop.bruk().loggFEIL("Klarte ikke å slette filen " + entry.getFileName().toString() + ", avbryter");
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL("Klarte ikke å lese fra mappen " + mappePath.getFileName());
            return false;
        }

    }

    public File finnFilIMappe(Path mappePath, String filnavn) {
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(mappePath);
            for (Path entry:directoryStream) {
                if (entry.getFileName().toString().equalsIgnoreCase(filnavn)) {
                    return entry.toFile();
                }
            }
        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL("Klarte ikke å få tak i filene i " + mappePath.toString() + ", avbryter");
            return null;
        }
        return null;
    }

    public Path finnPathIMappe(Path mappePath, String filnavn) {
        return finnFilIMappe(mappePath,filnavn).toPath();
    }

    //TODO: Skill ut som egen klasse?
    public Anchor hentNedlastingsLenkeButton(File fil){
        Anchor lastnedAnchor = new Anchor(DownloadHandler.forFile(fil),"");
        Button lastNedBackupButton = new Button("Last ned filen " + fil.getName());
        lastnedAnchor.add(lastNedBackupButton);
        lastNedBackupButton.addClickListener(e -> {
            //evt. opprette en fil først, hvis det trengs
        });
        return lastnedAnchor;
    }


    public boolean sjekkAtFilenIkkeErForStor(UploadMetadata metadata) {
        Float filStorrelseMb = konverterBytesTilMb(metadata.contentLength());
        return filStorrelseMb <= maksFilStoerrelseMb;
    }

    public Float konverterBytesTilMb(Long stoerrelseBytes) {
        if (stoerrelseBytes==null) {
            return 0F;
        } else {
            return (float)stoerrelseBytes/(1024*1024);
        }
    }

    public boolean sjekkAtFileIkkeFinnesAllerede(String strFilnavn, Filkategori filkategori){
        File fil = settSammenFile(strFilnavn, filkategori);
        return !fil.exists();
    }

    public File opprettFilMedUniktNavn(String strFilnavn, Filkategori filkategori){
        return opprettFilMedUniktNavn(strFilnavn, filkategori,"");
    }

    public File opprettFilMedUniktNavn(String strFilnavn, Filkategori filkategori, String strPostfixDel1) {
        for (int i = 1; i<100; i++) {
            String strFilnavnNytt = settSammenFilnavnOgTillegg(strFilnavn, strPostfixDel1, HelTallMester.integerFormatertSom00(i));
            File fil = settSammenFile( strFilnavnNytt, filkategori);
            if (!fil.exists()) {
                return fil;
            }
        }
        if (strPostfixDel1.isEmpty()){
            strPostfixDel1 = "A";
        } else if (strPostfixDel1.equals("A")) {
            strPostfixDel1 = "B";
        } else if (strPostfixDel1.equals("B")) {
            strPostfixDel1 = "C";
        } else if (strPostfixDel1.equals("C")) {
            strPostfixDel1 = "D";
        } else if (strPostfixDel1.equals("D")) {
            return null;
        }

        return opprettFilMedUniktNavn(strFilnavn, filkategori, strPostfixDel1);
    }

    public String settSammenFilnavnOgTillegg(String strFilnavn, String strPostfixDel1, String strPostfixDel2){
        if (strFilnavn==null || strFilnavn.isEmpty()) {
            return strPostfixDel1 + strPostfixDel2;
        }

        if (!strFilnavn.contains(".")) {
            return strFilnavn + strPostfixDel1 + strPostfixDel2;
        }

        if ((strPostfixDel1==null || strPostfixDel1.isEmpty()) && (strPostfixDel2==null || strPostfixDel2.isEmpty())) {
            return strFilnavn;
        }

        int intPos = strFilnavn.lastIndexOf(".");
        if (intPos== -1) {
            Loggekyklop.bruk().loggFEIL("Filnavnet "  + strFilnavn + " mangler punktum");
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strFilnavn.substring(0,intPos));
            sb.append("_");
            sb.append(strPostfixDel1);
            sb.append(strPostfixDel2);
            sb.append(strFilnavn.substring(intPos));
            return sb.toString();
        }

    }

    public File settSammenFile(String filnavn, Filkategori filkategori){
        File mappe = new File(filkategori.getMappeNavn());
        if (!mappe.exists()){
            boolean suksess = mappe.mkdir();
            if (!suksess) {
                Loggekyklop.bruk().loggFEIL("Klarte ikke å opprette mappen " + mappe.getName());
            }
        }
        return new File(filkategori.getMappeNavn() + "/" + filnavn);
    }

    public void skrivBytesTilFil(File filsti, byte[] data) {
        Path path = Paths.get(filsti.getAbsolutePath());
        try {
            Files.write(path,data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void printInnholdIMappe(String strRelativePath) {
        File file = new File(strRelativePath);
        System.out.println(file.getAbsolutePath() + ":");
        String[] rader = file.list();
        List<String> liste = Arrays.asList(rader);
        Collections.sort(liste);
        if (rader==null) {
            System.out.println("Innhold i file.list() er null, viser listRoots()");
            File[] roots = File.listRoots();
            for (File root:roots) {
                System.out.println(root.getAbsolutePath());
            }
        } else {
            for (String rad:liste) {
                System.out.println(rad);
            }
        }

    }


    public void printAbsolutePath(String strRelativePath){
        File file = new File(strRelativePath);
        System.out.println("AbsolutePath for " + strRelativePath + " er " + file.getAbsolutePath());
    }



    public static Filkyklop hent(){
        if (filkyklop == null) {
            filkyklop = new Filkyklop();
        }
        return filkyklop;
    }

    private Filkyklop() {
    }

    public boolean slettFil(String strFilnavn, Filkategori filkategori) {
        if (strFilnavn==null || strFilnavn.isEmpty()) {
            return false;
        }

        if(filkategori==null) {
            filkategori = Filkategori.BILDE;
        }

        File filSomSkalSlettes = settSammenFile(strFilnavn, filkategori);;
        if (filSomSkalSlettes.exists()) {
            return filSomSkalSlettes.delete();
        } else {
            Loggekyklop.bruk().loggFEIL("Filen " + filSomSkalSlettes.getAbsolutePath() + " som skulle slettes ble ikke funnet. Avbryter.");
            return false;
        }

    }
}
