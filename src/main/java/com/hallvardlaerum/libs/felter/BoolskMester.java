package com.hallvardlaerum.libs.felter;

import java.util.Locale;

public class BoolskMester {

    public static Boolean konverterBoolskFraStreng(String boolString) {
        if (boolString==null || boolString.isEmpty()) {
            return null;
        }

        return switch (boolString.toLowerCase(Locale.ROOT)) {
            case "yes", "1", "ja", "true", "sann" -> true;
            case "no", "0", "nei", "false", "usann" -> false;
            default -> null;
        };
    }

}
