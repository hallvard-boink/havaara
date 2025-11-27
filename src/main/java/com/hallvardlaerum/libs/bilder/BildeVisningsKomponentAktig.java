package com.hallvardlaerum.libs.bilder;

/**
 * Vise frem et bilde
 */

public interface BildeVisningsKomponentAktig {

    void byggOppBildeFraBytes(byte[] data);
    void hentBildeFraFilnavn(String filnavnString);


}
