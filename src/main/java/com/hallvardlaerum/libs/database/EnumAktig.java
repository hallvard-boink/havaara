package com.hallvardlaerum.libs.database;

import com.hallvardlaerum.libs.felter.DatopresisjonEnum;

public interface EnumAktig {

    String getTittel();
    String getBeskrivelse();
    String getTittelIImportfil();

    static <E extends Enum<E> & EnumAktig> E hentFraTittel(Class<E> enumType, String tittel) {
        if (tittel == null || tittel.isEmpty()) return null;
        for (E e : enumType.getEnumConstants()) {
            if (e.getTittel().equalsIgnoreCase(tittel)) return e;
        }
        return null;
    }


}
