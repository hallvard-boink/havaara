package com.hallvardlaerum.libs.felter;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;

import java.util.Locale;

public class HelTallMester {

    public static String integerFormatertSom00(Integer tall) {
        return String.format("%02d",tall);
    }

    public static String integerFormatertSom000(Integer tall) {
        return String.format("%03d",tall);
    }

    public static Integer konverterStrengMedDesimalTilInteger(String tallString) {
        if (tallString==null || tallString.isEmpty()) {
            return null;
        }

        Float desimaltall = null;
        try {
            desimaltall = Float.parseFloat(tallString);
            return Math.round(desimaltall);

        } catch (NumberFormatException e) {
            String forkortetString = tallString.replace(" ","");
            forkortetString = forkortetString.replaceAll("\\s+","");
            forkortetString = forkortetString.replaceAll("\u00a0","");


            if (forkortetString.contains(".")) {
                forkortetString = TekstKyklop.hent().fjernSisteDelAvStrengMedDelimiter(forkortetString,"\\.");
            } else if (forkortetString.contains(",")) {
                forkortetString = TekstKyklop.hent().fjernSisteDelAvStrengMedDelimiter(forkortetString, "\\,");
            }


            Integer tallv2Integer = null;
            try {
                tallv2Integer = Integer.parseInt(forkortetString);
                return tallv2Integer;

            } catch (NumberFormatException ex) {
                Loggekyklop.hent().loggTilFilINFO("Klarte ikke å konvertere strengen " + forkortetString + " fra originalen " + tallString + " til Integer. Fortsetter");
                return null;
            }

        }

    }

    public static String integerFormatertSomStortTall(Integer tall) {
        if (tall==null) {
            return "";
        } else {
            //return String.format("%1$,",tall);
            return String.format(Locale.of("no"),"%,d", tall);
        }
    }

    public static String integerFormatertSomStortTallMedPadding(Integer tall) {
        if (tall==null) {
            return "";
        } else {
            //return String.format("%1$,",tall);
            return String.format(Locale.of("no"),"%,10d", tall);
        }
    }
}
