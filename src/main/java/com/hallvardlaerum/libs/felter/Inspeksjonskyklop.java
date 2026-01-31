package com.hallvardlaerum.libs.felter;

import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Inspeksjonskyklop {
    private static Inspeksjonskyklop inspeksjonskyklop;

    public static Inspeksjonskyklop hent(){
        if (inspeksjonskyklop == null) {
            inspeksjonskyklop = new Inspeksjonskyklop();
        }
        return inspeksjonskyklop;
    }

    public ArrayList<UtvidetFelt> byggOppFeltliste(Object o){
        ArrayList<UtvidetFelt> feltListe = new ArrayList<>();
        Field[] arFields = o.getClass().getDeclaredFields();
        for (Field field:arFields) {
            if (field.isAnnotationPresent(SkalEksporteres.class)) {
                field.setAccessible(true);
                feltListe.add(new UtvidetFelt(field, o.getClass(), null));
            }
        }
        Field[] arFieldsSuper = o.getClass().getSuperclass().getDeclaredFields();
        for (Field superField:arFieldsSuper) {
            if (superField.isAnnotationPresent(SkalEksporteres.class)) {
                superField.setAccessible(true);
                feltListe.add(new UtvidetFelt(superField,o.getClass(),o.getClass().getSuperclass()));
            }
        }
        return feltListe;
    }

    private Inspeksjonskyklop(){

    }

    public class UtvidetFelt {
        Field felt;
        Class klasse;
        Class superklasse;

        public UtvidetFelt(Field felt, Class klasse, Class superklasse) {
            this.felt = felt;
            this.klasse = klasse;
            this.superklasse = superklasse;
        }

        public Field getFelt() {
            return felt;
        }

        public void setFelt(Field felt) {
            this.felt = felt;
        }

        public Class getKlasse() {
            return klasse;
        }

        public void setKlasse(Class klasse) {
            this.klasse = klasse;
        }

        public Class getSuperklasse() {
            return superklasse;
        }

        public void setSuperklasse(Class superklasse) {
            this.superklasse = superklasse;
        }
    }


}
