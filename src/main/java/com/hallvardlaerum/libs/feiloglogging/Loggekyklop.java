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

public class Loggekyklop {
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


    public static Loggekyklop hent(){
        if (loggekyklop == null) {
            loggekyklop = new Loggekyklop();
        }

        return loggekyklop;
    }

    public Nivaa getNivaa() {
        return nivaa;
    }

    public void settNivaaINFO() { nivaa = Nivaa.INFO;    }
    public void settNivaaDEBUG() { nivaa = Nivaa.DEBUG;    }
    public void settNivaaADVARSEL() { nivaa = Nivaa.ADVARSEL;    }
    public void settNivaaFEIL() { nivaa = Nivaa.FEIL;    }
    public void settNivaaKRITISK_FEIL() { nivaa = Nivaa.KRITISK_FEIL;    }

    public void ikkeVisNotifikasjonerTilBruker(){
        visNotifikasjonerTilBrukerBoolean = false;
    }

    public void visNotifikasjonerTilBruker(){
        visNotifikasjonerTilBrukerBoolean = true;
    }


    public void loggKRITISK_FEIL(String strMelding) {
        printLog(Nivaa.KRITISK_FEIL,strMelding);  //skal alltid vises
    }

    public void loggTilFilKRITISK_FEIL(String strMelding) {
        printLog(Nivaa.KRITISK_FEIL,strMelding, true);  //skal alltid vises
    }

    public void loggFEIL(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.FEIL.ordinal()) {
            printLog(Nivaa.FEIL,strMelding);
        }
    }

    public void loggTilFilFEIL(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.FEIL.ordinal()) {
            printLog(Nivaa.FEIL,strMelding, true);
        }
    }

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


    public void loggDEBUG(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.DEBUG.ordinal()) {
            printLog(Nivaa.DEBUG,strMelding);
        }
    }

    public void loggTilFilDEBUG(String strMelding) {
        if (nivaa.ordinal() >= Nivaa.DEBUG.ordinal()) {
            printLog(Nivaa.DEBUG,strMelding, true);
        }
    }


    public void loggINFO(String strMelding) {
        if (nivaa.ordinal() == Nivaa.INFO.ordinal()) {
            printLog(Nivaa.INFO,strMelding);
        }
    }

    public void loggTEST(String strMelding){
        System.out.println(Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTidSekund() + " | " +
                getCallingMethod(4).toString() + " TEST : " + strMelding);
    }

    public void loggTilFilINFO(String strMelding) {
        if (nivaa.ordinal() == Nivaa.INFO.ordinal()) {
            printLog(Nivaa.INFO,strMelding,true);
        }
    }

    private void printLog(Nivaa nivaa, String meldingString){
        printLog(nivaa, meldingString,false);
    }

    private void printLog(Nivaa nivaa, String strMelding, boolean skrivTilFilBoolean){

        String strNaavaerendeTidspunkt = Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTidSekund();
        if (skrivTilFilBoolean) {

            try {
                bufferedWriter.write(strNaavaerendeTidspunkt + " | " + getCallingMethod().toString() + " " + nivaa.toString() + " : " + strMelding + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(strNaavaerendeTidspunkt + " | " + getCallingMethod().toString() + " " + nivaa.toString() + " : " + strMelding);
        }

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
        return getCallingMethod(5); //Var tidligere 4
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
