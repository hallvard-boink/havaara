package com.hallvardlaerum.libs.eksportimport;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import java.util.ArrayList;
import java.util.List;

public abstract class CSVImportassistentMal<Entitet extends EntitetAktig> implements CSVImportassistentAktig<Entitet>{
    private ArrayList<String> cellerArrayList;

    private Entitet entitet;

    private String cellerString = null;
    private String feltnavneneString = null;
    private ArrayList<String>feltnavnCSVArrayList ;



//    @Override
//    public Entitet konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
//        this.feltnavnCSVArrayList = feltnavnCSVArrayList;
//        cellerArrayList = new ArrayList<>(List.of(celler));
//
////        entitet = postService.opprettEntitet();
//
////        String statusString = hentVerdi("Status");
////        if (statusString.equalsIgnoreCase("Reservert")) {  //Denne skal ikke importeres
////            return null;
////        }
////
////        entitet.setPostklasseEnum(PostklasseEnum.NORMALPOST);
////        entitet.setNormalPoststatusEnum(NormalpoststatusEnum.UBEHANDLET);
////        entitet.setNormalposttypeEnum(NormalposttypeEnum.NORMAL);
//
////        String datoString = hentVerdi("Utført dato");
////        LocalDate dato = Datokyklop.hent().opprettDatoSom_DDpMMpYYYY(datoString);
//        //LocalDate dato = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(datoString);
////        entitet.setDatoLocalDate(dato);
////        entitet.setTekstFraBankenString(hentVerdier(false,false,"Beskrivelse","Melding/KID/Fakt.nr"));
////        entitet.setInnPaaKontoInteger(parseInt(hentVerdi("Beløp inn")));
////        entitet.setUtFraKontoInteger(parseInt(hentVerdi("Beløp ut")));
////        entitet.setEkstraInfoString(hentVerdier(true,true,
////                "Type", "Undertype","Fra konto","Avsender","Til konto","Mottakernavn","Valuta"));
////
////        postService.lagre(entitet);
//        return entitet;
//    }
@Override
public void lesInnFeltnavnogCeller(ArrayList<String> feltnavnCSVArrayList, String[] celler){
        this.feltnavnCSVArrayList = feltnavnCSVArrayList;
        cellerArrayList = new ArrayList<>(List.of(celler));
    }


    @Override
    public String hentImportRadString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<cellerArrayList.size(); i++) {
            String feltnavnString = feltnavnCSVArrayList.get(i);
            String verdiString = cellerArrayList.get(i);
            sb.append(feltnavnString).append(": ").append(verdiString).append("| ");
        }
        return sb.toString();
    }

    @Override
    public Integer parseInt(String integerString) {
        if (integerString==null || integerString=="") {
            return null;
        } else {
            Integer tallInteger = HelTallMester.konverterStrengMedDesimalTilInteger(integerString);

            if (tallInteger!=null && tallInteger<0) {
                tallInteger = -tallInteger;
            }
            return tallInteger;
        }
    }

    @Override
    public String hentVerdier(boolean setterInnLinjeskift, boolean setterInnFeltnavn, String... feltnavnene){
        if (feltnavnene==null) {
            return null;
        }

        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (String feltnavnString:feltnavnene) {
            String verdi = hentVerdi(feltnavnString);
            if (verdi!=null && !verdi.isEmpty()) {
                if (setterInnFeltnavn) {
                    sb.append(feltnavnString).append(": ");
                }
                sb.append(verdi);
                if (i<feltnavnene.length-1) {
                    if (setterInnLinjeskift) {
                        sb.append("\n");
                    } else {
                        sb.append(", ");
                    }
                }
            }
            i++;
        }

        return sb.toString();
    }

    @Override
    public String hentVerdi(String feltnavn) {
        Integer posInteger = finnPosisjon(feltnavn);
        if (posInteger==null) {
            Loggekyklop.hent().loggINFO("Fant ikke '"+ feltnavn +"' i "  + hentFeltnavnCSVString());
            return "";
        } else {
            if (posInteger>cellerArrayList.size()-1) {
                return "";
            } else {
                return cellerArrayList.get(posInteger);
            }
        }
    }

    @Override
    public String hentFeltnavnCSVString(){
        if (feltnavneneString==null) {
            StringBuilder sb = new StringBuilder();
            for (String feltnavn:feltnavnCSVArrayList) {
                sb.append(feltnavn).append(" ");
            }
            feltnavneneString = sb.toString();
        }
        return feltnavneneString;
    }



    @Override
    public Integer finnPosisjon(String feltnavn) {
        for (int i = 0; i<feltnavnCSVArrayList.size(); i++) {
            if (feltnavn.equalsIgnoreCase(feltnavnCSVArrayList.get(i))) {
                return i;
            }
        }
        return null;
    }

    @Override
    public String hentcellerString(){
        StringBuilder sb = new StringBuilder();
        for (String celleString:cellerArrayList) {
            sb.append(celleString).append(" ");
        }
        cellerString = sb.toString();
        return cellerString;
    }

}
