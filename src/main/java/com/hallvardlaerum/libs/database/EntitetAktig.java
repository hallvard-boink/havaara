package com.hallvardlaerum.libs.database;

import java.time.LocalDateTime;
import java.util.UUID;


public interface EntitetAktig {


    UUID getUuid();
    void setUuid(UUID uuid);

    LocalDateTime getOpprettetDatoTid();
    void setOpprettetDatoTid(LocalDateTime opprettetDatoTid);

    LocalDateTime getRedigertDatoTid();
    void setRedigertDatoTid(LocalDateTime redigertDatoTid);

    /**
     * Her opprettes et beskrivende navn på instansen, ved å kombinere noen feltverdier.
     * F.eks: En instans av Bil returnerer eiers navn og bilens registreringsnummer
     * @return
     */
    String hentBeskrivendeNavn();

    String hentEksportID();

    static String trekkUtUUID(String eksportIdString) {
        if (eksportIdString==null) {
            return null;
        } else {
            if (eksportIdString.contains("UUID:")) {
                return eksportIdString.split("UUID:")[1];
            } else {
                return null;
            }
        }
    }

}
