package com.hallvardlaerum.libs.feiloglogging;

import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.filerogopplasting.StandardmappeEnum;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Loggekyklop implements LoggekyklopAktig{
    private static Loggekyklop loggekyklop;

    public enum Nivaa {
        KRITISK_FEIL, FEIL, ADVARSEL, DEBUG, INFO
    }

    private Nivaa nivaa = Nivaa.DEBUG;
    private Nivaa forrigeNivaa = Nivaa.DEBUG;
    private boolean visNotifikasjonerTilBrukerBoolean = true;
    private boolean forrigeVisNotifikasjonerTilBrukerBoolean = true;
    private BufferedWriter bufferedWriter;


    private Loggekyklop() {
    }


    public static LoggekyklopAktig bruk() {
        return (LoggekyklopAktig) hent();
    }

    public static Loggekyklop hent(){
        if (loggekyklop==null) {
            loggekyklop = new Loggekyklop();
        }
        return loggekyklop;
    }

    /**
     * Forbered til bruk av loggefil ved import
     */

    @Override
    public void forberedTilImportloggTilFil(){
        huskStatus();
        settNivaaINFO();
        initierLoggfil();
    }

    @Override
    public void avsluttImportloggTilFil(){
        lukkLoggfil();
    }

    /**
     * Husk loggenivået og om notifikasjoner vises til bruker eller ikke
     */
    public void huskStatus(){
        forrigeNivaa = nivaa;
        forrigeVisNotifikasjonerTilBrukerBoolean = visNotifikasjonerTilBrukerBoolean;
    }

    /**
     * Tilbakestill loggenivået og om notifikasjoner vises til bruker eller ikke
     */
    public void tilbakestillStatus() {
        nivaa = forrigeNivaa;
        visNotifikasjonerTilBrukerBoolean = forrigeVisNotifikasjonerTilBrukerBoolean;
    }

    public void initierLoggfil(){
        if (bufferedWriter!=null) { //har allerede en fil gående. Skitten løsning, men la gå for nå,
            return;
        }
        String filnavnString = "Importlogg_" + Datokyklop.hent().hentDagensDatoTidForFiler() + ".txt";
        File loggFile = Filkyklop.hent().hentElleropprettFil(StandardmappeEnum.LOGS,filnavnString);
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(loggFile));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void lukkLoggfil() {
        if (bufferedWriter!=null) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public Nivaa getNivaa() {
        return nivaa;
    }

    @Override
    public void settNivaaINFO() { nivaa = Nivaa.INFO;    }
    @Override
    public void settNivaaDEBUG() { nivaa = Nivaa.DEBUG;    }
    @Override
    public void settNivaaADVARSEL() { nivaa = Nivaa.ADVARSEL;    }
    @Override
    public void settNivaaFEIL() { nivaa = Nivaa.FEIL;    }
    @Override
    public void settNivaaKRITISK_FEIL() { nivaa = Nivaa.KRITISK_FEIL;    }

    @Override
    public void ikkeVisNotifikasjonerTilBruker(){
        visNotifikasjonerTilBrukerBoolean = false;
    }

    @Override
    public void visNotifikasjonerTilBruker(){
        visNotifikasjonerTilBrukerBoolean = true;
    }


    @Override
    public void loggKRITISK_FEIL(String strMelding) {
        printLog(Nivaa.KRITISK_FEIL,strMelding);  //skal alltid vises
    }

    public void loggTilFilKRITISK_FEIL(String strMelding) {
        printLog(Nivaa.KRITISK_FEIL,strMelding, true);  //skal alltid vises
    }

    @Override
    public void loggFEIL(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.FEIL.ordinal()) {
            printLog(Nivaa.FEIL,strMelding);
        }
    }


    @Override
    public void loggADVARSEL(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.ADVARSEL.ordinal()) {
            printLog(Nivaa.ADVARSEL,strMelding);
        }
    }

    public void loggTilFilADVARSEL(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.ADVARSEL.ordinal()) {
            printLog(Nivaa.ADVARSEL,strMelding,true);
        }
    }


    @Override
    public void loggDEBUG(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.DEBUG.ordinal()) {
            printLog(Nivaa.DEBUG,strMelding);
        }
    }


    @Override
    public void loggINFO(String strMelding) {
        if (nivaa.ordinal() == Nivaa.INFO.ordinal()) {
            printLog(Nivaa.INFO,strMelding);
        }
    }

    @Override
    public void loggTEST(String strMelding){
        System.out.println(Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTidSekund() + " | " +
                getCallingMethod(4).toString() + " TEST : " + strMelding);
    }

    private void printLog(Nivaa nivaa, String meldingString){
        printLog(nivaa, meldingString,false);
    }

    private void printLog(Nivaa nivaa, String strMelding, boolean skrivTilFilBoolean){
        String strNaavaerendeTidspunkt = Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTidSekund();
        String radString = strNaavaerendeTidspunkt + " | " + getCallingMethod().toString() + " " + nivaa.toString() + " : " + strMelding + "\n";

        if (skrivTilFilBoolean) {
            try {
                bufferedWriter.write(radString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.print(radString);
        evtVisNotifikasjon(strMelding);

    }


    private void evtVisNotifikasjon(String strMelding) {
        if (visNotifikasjonerTilBrukerBoolean) {
            if (UI.getCurrent()!=null) {
                UI.getCurrent().access(() -> {
                    Notification.show(strMelding).setPosition(Notification.Position.MIDDLE);
                });
            }
        }
    }

    public StackTraceElement getCallingMethod() {
        return getCallingMethod(6); //Var tidligere 4
    }


    public StackTraceElement getCallingMethod(Integer intForskyving) {
        String strUtgangspunkt = "getAllStackTraces";
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        Boolean blnFunnet = false;
        StackTraceElement[] stackTraceElements;
        for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
            stackTraceElements = entry.getValue();
            for (int i = 0; i < stackTraceElements.length; i++) {
                if (stackTraceElements[i].getMethodName().equals(strUtgangspunkt)) {
                    //printNaermesteMetoderIStacken(stackTraceElements,i);  //Brukes for feilsøking
                    return stackTraceElements[i + intForskyving];
                }
            }
        }
        return null;
    }

    private void printNaermesteMetoderIStacken(StackTraceElement[] stackTraceElements, Integer i) {
        System.out.println("Fant getAllStackTraces på " + i);
        int jStart = Math.min(i, 3) * -1;
        for (int j = jStart; j < 5; j++) {
            System.out.println("i " + j + " : " + stackTraceElements[i + j]);
        }
    }


    /**
     * Spesialprosedyre som tar teksten vi får fra stacktrace, for eksempel slik:
     * app//com.hallvardlaerum.testing.test.LoggekyklopTest.printCallingMethod(LoggekyklopTest.java:24)
     *
     * Denne prosedyren skal gjøre at vi får følgende i stedet:
     * app//com.hallvardlaerum.testing.test.LoggekyklopTest.printCallingMethod(linje 24)
     *
     * @param strTekst Tekstrekken vi får fra Stacktrace
     * @return Ny tekstrekke med passende linjeangivelse
     */

    public String fiksLinjeangivelse(String strTekst) {
        if (strTekst.isBlank() || strTekst.isEmpty()) {
            return "";
        }

        String strFiksetTekst=strTekst.replaceAll("\\(.*\\:","(linje ");
        return strFiksetTekst;

    }


}
