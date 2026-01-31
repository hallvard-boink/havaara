package com.hallvardlaerum.libs.verktoy;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.eksportimport.CSVEksportkyklop;
import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.feiloglogging.Infobit;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.EnhetskonverteringsMester;
import com.hallvardlaerum.libs.felter.Inspeksjonskyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.filerogopplasting.StandardmappeEnum;
import com.hallvardlaerum.libs.ui.MainViewmal;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Backupkyklop {

// ===========================
// region Felter
// ===========================

    private static Backupkyklop backupkyklop;
    private ArrayList<EntitetserviceAktig<? extends EntitetAktig, ?>> entitetservicene;
    private Anchor lastnedAnchor;
    private String strBackupFilnavn;
    private File zipFil = null;
    private ScheduledExecutorService scheduler;
    private ArrayList<Infobit> backuper;
    private Boolean blnBackupErAktivert = false;
    private Span badgeSpan = null;
    private Tab backupsTab = null;
    private MainViewmal mainViewMal = null;

// endregion


// ===========================
// region Forvalte entitetservicer
// ===========================

    public void leggTilEntitetservice(EntitetserviceAktig<? extends EntitetAktig, ?> entitetservice) {
        if (entitetservicene == null) {
            entitetservicene = new ArrayList<>();
        }
        if (!entitetservicene.contains(entitetservice)) {
            entitetservicene.add(entitetservice);
        }
    }

    public void printEntitetservicer() {
        System.out.println();
        System.out.println("ENTITETSERVICENE");
        for (EntitetserviceAktig<?, ?> entitetserviceAktig:entitetservicene) {
            Loggekyklop.bruk().loggDEBUG("Entitetservice " + entitetserviceAktig.hentEntitetsnavn());
        }
    }

    public <T extends EntitetAktig> EntitetserviceAktig<T,?> finnEntitetservice(Class<T> entitetKlasse) {
        for (EntitetserviceAktig<?,?> entitetservice:entitetservicene) {
            if (entitetservice.hentEntitetsnavn().equalsIgnoreCase(entitetKlasse.getSimpleName())) {
                return (EntitetserviceAktig<T,?>)entitetservice;
            }
        }
        return null;
    }

    public ArrayList<EntitetserviceAktig<? extends EntitetAktig, ?>> hentEntitetservicene() {
        return entitetservicene;
    }

    public boolean omEntitetserviceErByggetOppAllerede(){
        return (entitetservicene == null || entitetservicene.isEmpty());
    }

// endregion



// ===========================
// region GUI: Badge og Buttons
// ===========================

    public ArrayList<Inspeksjonskyklop.UtvidetFelt> byggOppAlleFelterArrayList(){
        ArrayList<Inspeksjonskyklop.UtvidetFelt> alleFelterArrayList = new ArrayList<>();
        for (EntitetserviceAktig<?, ?> entitetservice : entitetservicene) {
            EntitetAktig entitet = entitetservice.opprettEntitet();
            alleFelterArrayList.addAll(Inspeksjonskyklop.hent().byggOppFeltliste(entitet));
        }
        return alleFelterArrayList;
    }


    public Span hentBackupBadge() {
        if (badgeSpan == null) {
            badgeSpan = new Span();
        }
        badgeSpan = new Span();
        oppdaterBackupBadge();
        return badgeSpan;
    }


    public void oppdaterBackupTab(){
        if (backupsTab !=null) {
            String strClassname;
            if (blnBackupErAktivert) {
                strClassname = LumoUtility.TextColor.SUCCESS;
            } else {
                strClassname = LumoUtility.TextColor.ERROR;
            }
            backupsTab.getElement().getClassList().add(strClassname);
        }

    }


    private void oppdaterBackupBadge(){
        if (blnBackupErAktivert) {
            badgeSpan.setText("Backup aktivert " + Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTid());
            badgeSpan.getElement().getThemeList().clear();
            badgeSpan.getElement().getThemeList().add("badge success"); //grønn
        } else {
            badgeSpan.setText("Backup ikke aktiv");
            badgeSpan.getElement().getThemeList().clear();
            badgeSpan.getElement().getThemeList().add("badge error"); //rød
        }
    }


    private void oppdaterBackupBadgeTabOgListe(){
        oppdaterBackupTab();
        oppdaterBackupBadge();
        oppdaterBackupListe();
    }


    public Button hentStartBackupDemonButton(){
        Button startDemonButton = new Button("Start regelmessig backup hver time");
        startDemonButton.addClickListener(e -> {
            opprettOgLagreBackupSomZipfil(); //Gjør uansett backup nå
            startBackupdemon();
        });
        return startDemonButton;
    }

    public Button hentStoppBackupDemonButton() {
        Button stoppDemonButton = new Button("Stopp regelmessig backup");
        stoppDemonButton.addClickListener(e -> stoppBackupdemon());
        return stoppDemonButton;
    }


    public Anchor hentBackupLenkeButton(String strApplikasjonsnavn){
        File mappe = hentBackupmappeFile();
        if (mappe==null) {
            Loggekyklop.bruk().loggFEIL("BackupFolder er null, avbryter");
            return null;
        }

        String pathString = "";
        try {
            pathString = hentBackupmappeFile().getCanonicalPath() + "/" + opprettFilnavnForBackup(strApplikasjonsnavn);
        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL("Fikk ikke hentet backupmappeFile sin canonicalPath, avbryter");

        }

        zipFil = new File(pathString);
        lastnedAnchor = new Anchor(DownloadHandler.forFile(zipFil),"");

        Button lastNedBackupButton = new Button("Last ned backup som zip-fil nå");
        lastNedBackupButton.setTooltipText("Kjører en backup med en gang, og lar deg laste den ned lokalt som zip-fil.");
        lastnedAnchor.add(lastNedBackupButton);
        lastNedBackupButton.addClickListener(e -> {
            opprettOgLagreBackupSomZipfil();
        });
        return lastnedAnchor;
    }


    public Anchor hentBackupLenkeButtonTilGrid(String strFilnavn) {
        String pathString="";
        try {
            pathString = hentBackupmappeFile().getCanonicalPath() + "/" + strFilnavn;
        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL("Fikk ikke hentet backupmappeFile sin canonicalPath, avbryter");
        }
        File zipFil = new File(pathString);
        return new Anchor(DownloadHandler.forFile(zipFil),"");
    }

// endregion





    // ===========================
    // region Backupdemon og behov for backup
    // ===========================


    /** Kjør backup en time etter siste endring
     *
     */
    public void startBackupdemon(){
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            public void run() {
                if (omDatabasenErEndretNylig()) {
                    Loggekyklop.bruk().loggINFO(Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTid() + ": Eksporterte alle data som zip-fil");
                    opprettOgLagreBackupSomZipfil();

                } else {
                    Loggekyklop.bruk().loggINFO(Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTid() + ": Ingen endringer i databasen.");
                }
            }
        };
        // Utfør oppgaven nå, og gjenta den hver 30. minutt
        // Grensen for backup er satt til mellom 1 og 2 timer siden for å unngå at det kjøres backup mens databasen brukes.
        // TODO Burde sikkert sjekke at det ikke kjøres backup akkurat mens brukeren bruker applikasjonen aktivt.
        //
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.MINUTES);
        blnBackupErAktivert = true;
        oppdaterBackupBadgeTabOgListe();
    }


    public void stoppBackupdemon(){
        scheduler.close();
        blnBackupErAktivert = false;
        oppdaterBackupBadge();
    }



    public Boolean omDatabasenErEndretNylig(){
        LocalDateTime forEnTimeSiden = LocalDateTime.now().minusHours(1);
        LocalDateTime forToTimerSiden = LocalDateTime.now().minusHours(2);

        StringBuilder sb = new StringBuilder();
        sb.append("Sjekker endringer mellom ");
        sb.append(Datokyklop.hent().formaterDatoTid(forToTimerSiden)).append(" og ");
        sb.append(Datokyklop.hent().formaterDatoTid(forEnTimeSiden)).append(" i entitetene ");

        Boolean blnErEndret = false;
        for (EntitetserviceAktig<?, ?> entitetservice: entitetservicene) {
            sb.append(entitetservice.hentEntitetsnavn()).append(" ");
            if (!entitetservice.finnAlleRedigertDatoTidMellom(forToTimerSiden, forEnTimeSiden).isEmpty()) {
               blnErEndret = true;
            }
        }
        System.out.println("omDatabasenErEndretNylig " + sb.toString());
        return blnErEndret;
    }

// endregion



// ===========================
// region Filhåndtering
// ===========================

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public String opprettFilnavnForBackup(String strApplikasjonsNavn){
        StringBuilder sb = new StringBuilder();
        sb.append(strApplikasjonsNavn).append("_Backup_");
        sb.append(Datokyklop.hent().hentDagensDatoTidForFiler());
        sb.append(".zip");
        strBackupFilnavn = sb.toString();

        return strBackupFilnavn;
    }

    public boolean pakkutBackupfiler(File file){
        if (file == null) {
            return false;
        }

        // Se https://www.baeldung.com/java-compress-and-uncompress

        String fileZip = file.getPath();
        File destDir = hentUnzipmappeFile();

        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        } catch (Exception e) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke å unzippe backupfil, avbryter");
            return false;
        }

        return true;
    }


    public File hentSisteBackupfilFile(){
        File[] backupfiler = hentBackupFiler();
        if (backupfiler.length>0) {
            return backupfiler[0];
        } else {
            return new File("");
        }

    }



    public String opprettFilnavnForEntitetensTekstfilIZipfil(EntitetserviceAktig<?, ?> entitetservice) {
        return entitetservice.hentEntitetsnavn() + ".csv";
    }


    public File hentBackupmappeFile(){
        return Filkyklop.hent().hentEllerOpprettStandardmappe(StandardmappeEnum.BACKUPS);
    }


    public File hentUnzipmappeFile(){
        return Filkyklop.hent().hentEllerOpprettStandardmappe(StandardmappeEnum.UNZIP);
    }


    public File[] hentBackupFiler(){
        File backupmappeFile = hentBackupmappeFile();
        File[] filerEmptyArray = new File[0];
        if (backupmappeFile==null) {
            return filerEmptyArray;
        } else {
            return Filkyklop.hent().filtrerEtterFilType(backupmappeFile.listFiles(),"zip");
        }
    }


// endregion



// ===========================
// region Oppdatere liste og håndtere backups
// ===========================

    private void oppdaterBackupListe(){
        mainViewMal.oppdaterBackupteksttabell();
    }


    public ArrayList<ArrayList<String>> hentEntiteterAntallposterSistendret(){
        ArrayList<ArrayList<String>> radene = new ArrayList<>();
        for (EntitetserviceAktig<? extends EntitetAktig, ?> service: entitetservicene) {
            ArrayList<String> rad = new ArrayList<>();
            rad.add(service.hentEntitetsnavn());
            rad.add(Integer.toString(service.finnAlle().size()));

            EntitetAktig entitet = service.finnSistRedigert();
            if (entitet == null) {
                rad.add("(ingen poster ennå)");
            } else {
                rad.add(Datokyklop.hent().formaterDatoTid(entitet.getRedigertDatoTid()));
            }

            radene.add(rad);
        }

        return radene;
    }

    public ArrayList<Infobit> getBackupInfobiter() {
        backuper = new ArrayList<>();
        File[] filer = hentBackupFiler();
        if (filer!=null && filer.length>0) {
            for (File fil:filer) {
                String strStoerrelse = EnhetskonverteringsMester.presenterFilstoerrelseMenneskelesbart(fil.length());
                backuper.add(new Infobit(fil.getName(), strStoerrelse));
            }
            backuper.sort((o1,o2) -> -1 * o1.getTittel().compareTo(o2.getTittel()));
        }
        return backuper;
    }


    public ArrayList<Infobit> hentAntallPosterIHverEntitet(){
        ArrayList<Infobit> infobitene = new ArrayList<>();
        for (EntitetserviceAktig<? extends EntitetAktig, ?> service: entitetservicene) {
            infobitene.add(new Infobit(service.hentEntitetsnavn(),service.finnAlle().size() + " poster"));
        }
        return infobitene;
    }

    public void slettBackupfil(String strFilnavn) {
        File file = new File(hentBackupmappeFile().getAbsoluteFile()+"/" + strFilnavn);
        if (!file.exists()) {
            Notification.show("Fant ingen fil med navnet " + strFilnavn + " i mappen " + hentBackupmappeFile().getName() + ", avbryter.");
            return;
        }
        boolean blnDone = file.delete();
        if (!blnDone) {
            Loggekyklop.bruk().loggADVARSEL("Backupfilen " + strFilnavn + " ble ikke slettet likevel.");
        }

        oppdaterBackupListe();
    }

    public void restoreBackupfilButtonMethod(String strFilnavn) {
        File file = new File(hentBackupmappeFile().getAbsoluteFile()+"/" + strFilnavn);
        if (!file.exists()) {
            Notification.show("Fant ingen fil med navnet " + strFilnavn + " i mappen " + hentBackupmappeFile().getName() + ", avbryter.");
            return;
        }

        if (!pakkutBackupfiler(file)) return;
        if (!slettAllePostene()) return;
        if (!importerBackupfilerSomCSV()) return;
        if (!Filkyklop.hent().slettFileneIMappe(hentUnzipmappeFile().toPath())) return;

    }

// endregion



// ===========================
// region Kjør backup
// ===========================

    private void opprettOgLagreBackupSomZipfil(){
        ZipOutputStream zipOut;

        try {
            zipOut = new ZipOutputStream(new FileOutputStream(zipFil));
        } catch (FileNotFoundException e) {
            Loggekyklop.bruk().loggFEIL("Feil ved opprettelse av zip-fil: " + e.getMessage() + ", avbryter");
            return;
        }

        ZipEntry zipEntry;
        for (EntitetserviceAktig<? extends EntitetAktig, ?> entitetservice:entitetservicene) {
            if(!entitetservice.finnAlle().isEmpty()) {
                try {
                    zipEntry = new ZipEntry(opprettFilnavnForEntitetensTekstfilIZipfil(entitetservice));
                    zipOut.putNextEntry(zipEntry);
                    CSVEksportkyklop.hent().oppdaterFeltliste(entitetservice);
                    byte[] data = CSVEksportkyklop.hent().hentUtEntiteteneSomTekst(entitetservice).getBytes();
                    zipOut.write(data);
                    zipOut.closeEntry();
                } catch (IOException e) {
                    Loggekyklop.bruk().loggFEIL("Feil ved opprettelse av zipEntry: " + e.getMessage() + ", går videre");
                }
            }
        }
        try {
            zipOut.close();
        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL("Feil ved lukking og lagring av zipFil: " + e.getMessage() + ".");
        }

        mainViewMal.oppdaterBackupteksttabell();
    }

// endregion



// ===========================
// region Kjør Restore
// ===========================


    private Boolean slettAllePostene(){

        // Må gå baklengs gjennom entitetservicene
        for (int i = entitetservicene.size()-1; i>0;i--) {
            entitetservicene.get(i).slettAlle();
        }

        return true;
    }

    private Boolean importerBackupfilerSomCSV() {
        CSVImportmester csvImportmester = new CSVImportmester();
        for (int i= 0; i<entitetservicene.size();i++) {
            EntitetserviceAktig<?, ?> entityservice = entitetservicene.get(i);
            Loggekyklop.bruk().loggDEBUG("Importerer rader for " + entityservice.hentEntitetsnavn());
            String filnavnString = opprettFilnavnForEntitetensTekstfilIZipfil(entityservice);
            Path tekstfilPath = Filkyklop.hent().finnPathIMappe(hentUnzipmappeFile().toPath(), filnavnString);
            csvImportmester.importerFraFileTilKjentEntitet(tekstfilPath,entityservice);
        }

        return true;
    }


// endregion




// ===========================
// region Getters and Setters
// ===========================




    public void setBackupsTab(Tab backupsTab) {
        this.backupsTab = backupsTab;
    }

    public void setMainViewMalInstans(MainViewmal mainViewmal) {
        this.mainViewMal = mainViewmal;
    }

    public Boolean getBlnBackupErAktivert() {
        return blnBackupErAktivert;
    }

    public String getStrBackupFilnavn() {
        return strBackupFilnavn;
    }

    public static Backupkyklop hent(){
        if (backupkyklop == null) {
            backupkyklop = new Backupkyklop();
        }
        return backupkyklop;
    }

    public Backupkyklop() {
    }

// endregion


}
