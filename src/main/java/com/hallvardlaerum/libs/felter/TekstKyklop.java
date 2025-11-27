package com.hallvardlaerum.libs.felter;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;

import java.util.ArrayList;

public class TekstKyklop {
    private static TekstKyklop tekstKyklop;

    private TekstKyklop() {
    }

    public static TekstKyklop hent(){
        if (tekstKyklop == null) {
            tekstKyklop = new TekstKyklop();
        }
        return tekstKyklop;
    }

    public boolean inneholderTekst(String entitetensTekst, String filterTekst) {
        return entitetensTekst.toLowerCase().contains(filterTekst.toLowerCase());
    }

    public String konverterFilnavnTilTittel(String filnavnString){
        if (filnavnString==null || filnavnString.isEmpty()) {
            return "";
        }

        filnavnString = filnavnString
            .replaceAll("-"," ")
            .replaceAll("_"," ");

        filnavnString = fjernSisteDelAvStrengMedDelimiter(filnavnString,"\\.");
        filnavnString = filnavnString.replaceAll("\\."," ");
        filnavnString = settStorForbokstav(filnavnString);

        return filnavnString;

    }

    public String settStorForbokstav(String tekst){
        if (tekst==null || tekst.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(tekst.substring(0,1).toUpperCase());
        sb.append(tekst, 1, tekst.length());
        return sb.toString();
    }

    public String fjernSisteDelAvStrengMedDelimiter(String tekst, String delimiter) {
        if (!delimiter.contains("\\")){
            Loggekyklop.hent().loggFEIL("Delimiter uten escape (\\\\) først");
            return tekst;
        }

        String[] deler = tekst.split(delimiter);  //Husk: splitt krever escapecharacter i delimiter
        if (deler.length<2){
            return tekst;
        }

        String delimiterUtenEscape = fjernEscapecharacterFraStreng(delimiter);
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<deler.length-1;i++) {
            if (i>0) {
                sb.append(delimiterUtenEscape);
            }
            sb.append(deler[i]);
        }
        return sb.toString();
    }

    public String fjernEscapecharacterFraStreng(String tekst){
        if(tekst.contains("\\")){
            return tekst.replace("\\","");
        } else {
            return tekst;
        }
    }

    public String hentFoersteDelAvStrengMedDelimiter(String tekst, String delimiter){
        return tekst.split(delimiter)[0];
    }


    public String fjernNylinjeFraTekstfelt(String tekst) {
        return tekst.replaceAll(System.getProperty("line.separator"), " ");
    }

    /**
     *
     * @param strRekke En streng som inneholder flere tekstdeler skilt med strDelimiter
     * @param strDelimiter En streng som inneholder . (punktum) , (komma) eller annet skilletegn som fungerer med regex med \\ foran
     * @param antallDeler  Antall deler som skal hentes ut
     * @return En streng som inneholder ønsket antall tekstdeler
     */
    public String hentSisteDelerAvStrengMedDelimiter(String strRekke, String strDelimiter, Integer antallDeler) {
        if (strRekke == null || strRekke.length()==0 ) { return "";        }
        if (strDelimiter.isEmpty() || strDelimiter.isBlank()) { strDelimiter = "."; }
        if (antallDeler == null || antallDeler == 0) { antallDeler = 1;}

        String regexDel = "\\" + strDelimiter;
        String[] deler = strRekke.split(regexDel);
        if (antallDeler > deler.length) {
            antallDeler = deler.length;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int flytt = deler.length-antallDeler;
        for (int indeks=flytt; indeks<deler.length; indeks++) {
            if (indeks>flytt) { stringBuilder.append(strDelimiter); }
            stringBuilder.append(deler[indeks]);
        }

        return stringBuilder.toString();
    }

    public Integer tellAntallForekomster(String strDetSomSkalTelles, String strTekst) {
        String strUtenSoekestreng = strTekst.replaceAll(strDetSomSkalTelles, "");
        return strTekst.length()- strUtenSoekestreng.length();
    }




    /**
     *
     * @param rekke Inneholder tekstdeler adskilt med delimiter
     * @param delimiter
     * @return Siste tekstdel i rekken
     */
    public String hentSisteIStrengMedDelimiter(String rekke, String delimiter){
        return hentSisteDelerAvStrengMedDelimiter(rekke,delimiter,1);
    }

    /**
     *
     * @param rekke Inneholder tekstdeler adskilt med punktum (.)
     * @return Siste dekstdel i rekken
     */

    public String hentSisteIStrengMedDelimiter(String rekke) {
        return hentSisteDelerAvStrengMedDelimiter(rekke,".",1);
    }

    /**
     *
     * @param arraylistRader ArrayList av strenger
     * @return streng med teksten i arraylist, adskilt med newline (\n)
     */
    public String konverterArraylistraderTilLangStreng(ArrayList<String> arraylistRader, boolean taMedKolonnetitler){
        StringBuffer sb = new StringBuffer();
        int intRadnr = 0;


        if (taMedKolonnetitler) {
            for (String rad:arraylistRader) {
                intRadnr++;
                if (intRadnr>1) {
                    sb.append("\n");
                }
                sb.append(rad);
            }
        } else {
            for (int i=1; i<arraylistRader.size();i++) {
                if (i>1) {
                    sb.append("\n");
                }
                sb.append(arraylistRader.get(i));
            }
        }

        return sb.toString();
    }

    public String konverterArraylistraderTilLangStreng(ArrayList<String> arraylistRader){
        return konverterArraylistraderTilLangStreng(arraylistRader,true);
    }


}
