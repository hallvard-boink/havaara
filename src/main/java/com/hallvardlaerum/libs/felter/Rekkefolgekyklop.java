package com.hallvardlaerum.libs.felter;

import com.hallvardlaerum.libs.database.RekkefoeolgeAktig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Rekkefølgen starter på 1 dvs steg. Lagringen skjer ikke her, men i prosedyren som kallet.
 */
public class Rekkefolgekyklop {
    private static Rekkefolgekyklop rekkefolgekyklop;
    private static final Integer steg = 10;


    public <T extends RekkefoeolgeAktig> void sorterEtterRekkefoelgenummer(ArrayList<T> liste){
        if (liste==null || liste.isEmpty()) {
            return;
        }
        korrigerManglendeRekkefoelgenumre(liste);
        liste.sort(Comparator.comparing(T::getRekkefoelge));
    }

    public <T extends RekkefoeolgeAktig> void sorterOgoppdaterRekkefoelgenumre(ArrayList<T> liste) {
        sorterEtterRekkefoelgenummer(liste);
        tildelNyeRekkefoelgenumre(liste);
    }

    private <T extends RekkefoeolgeAktig> void korrigerManglendeRekkefoelgenumre(ArrayList<T> liste) {
        int rekkefoelgeInteger = steg;
        for (T objekt:liste) {
            if (objekt.getRekkefoelge()==null || objekt.getRekkefoelge()==0) {
                objekt.setRekkefoelge(rekkefoelgeInteger);
            }
            rekkefoelgeInteger = rekkefoelgeInteger + steg;
        }
    }

    private boolean listaKanIkkeFlyttesI(ArrayList<? extends RekkefoeolgeAktig> liste) {
        return liste == null || liste.isEmpty() || liste.size() == 1;
    }

    public <T extends RekkefoeolgeAktig> void flyttFremover(T objekt, ArrayList<T> liste){
        if (listaKanIkkeFlyttesI(liste)){
            return;
        }

        int posisjon = liste.indexOf(objekt);
        if (posisjon==-1) { // finnes ikke i lista
            return;
        }

        if (posisjon != liste.size()-1) {
            Collections.swap(liste, posisjon, posisjon+1);
            tildelNyeRekkefoelgenumre(liste);
        }

    }

    public <T extends RekkefoeolgeAktig> void flyttBakover(T objekt, ArrayList<T> liste){
        if (listaKanIkkeFlyttesI(liste)){
            return;
        }

        int posisjon = liste.indexOf(objekt);
        if (posisjon==-1) { // finnes ikke i lista
            return;
        }

        if (posisjon != 0) {
            Collections.swap(liste, posisjon, posisjon-1);
            tildelNyeRekkefoelgenumre(liste);
        }
    }

    public <T extends RekkefoeolgeAktig> void flyttFoerst(T objekt, ArrayList<T> liste){
        if (listaKanIkkeFlyttesI(liste)){
            return;
        }

        if (!objekt.equals(liste.getLast())) {
            liste.remove(objekt);
            liste.addFirst(objekt);
            tildelNyeRekkefoelgenumre(liste);
        }
    }

    public <T extends RekkefoeolgeAktig>void flyttSist(T objekt, ArrayList<T> liste){
        if (listaKanIkkeFlyttesI(liste)){
            return;
        }

        if (!objekt.equals(liste.getFirst())) {
            liste.remove(objekt);
            liste.addLast(objekt);
            tildelNyeRekkefoelgenumre(liste);
        }
    }




    private <T extends RekkefoeolgeAktig> void tildelNyeRekkefoelgenumre(ArrayList<T> liste) {
        if (liste==null || liste.isEmpty()) {
            return;
        }
        Integer rekkefoelge = 0;
        for (int i = 0; i<liste.size(); i++) {
            rekkefoelge = rekkefoelge + steg;
            RekkefoeolgeAktig objekt = liste.get(i);
            objekt.setRekkefoelge(rekkefoelge);
        }
    }



    public static Rekkefolgekyklop hent(){
        if (rekkefolgekyklop == null) {
            rekkefolgekyklop = new Rekkefolgekyklop();
        }
        return rekkefolgekyklop;
    }

    private Rekkefolgekyklop(){

    }


}
