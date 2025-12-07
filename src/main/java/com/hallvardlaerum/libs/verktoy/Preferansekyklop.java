package com.hallvardlaerum.libs.verktoy;

//TODO: Lag Preferansekyklop

/**
 * Denne skal lagre preferanser som JSON i en lokal tekstfil, og opprette ny hvis den mangler. Etterhvert
 */
public class Preferansekyklop {
    private static Preferansekyklop preferansekyklop;




    public static Preferansekyklop hent(){
        if (preferansekyklop == null) {
            preferansekyklop = new Preferansekyklop();
        }
        return preferansekyklop;
    }

    private Preferansekyklop(){

    }


}
