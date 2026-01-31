package com.hallvardlaerum.libs.felter;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;

import java.math.BigDecimal;
import java.util.Locale;

public class HelTallMester {

    public static String formaterIntegerSom00(Integer tall) {
        return String.format("%02d",tall);
    }

    public static String formaterIntegerSom000(Integer tall) {
        return String.format("%03d",tall);
    }


    public static Integer konverterStrengMedDesimalTilInteger(String tallString) {
        if (tallString==null || tallString.isEmpty()) {
            return null;
        }

        Float desimaltall;
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


            int tallv2Integer;
            try {
                tallv2Integer = Integer.parseInt(forkortetString);
                return tallv2Integer;

            } catch (NumberFormatException ex) {
                Loggekyklop.hent().loggINFO("Klarte ikke å konvertere strengen " + forkortetString + " fra originalen " + tallString + " til Integer. Fortsetter");
                return null;
            }

        }

    }

    public static String konverterDoubleTilIntegerFormatertSomStreng(double tallDouble){
        Integer tallInteger = Math.round((float)tallDouble);
        return integerFormatertSomStortTall(tallInteger);
    }

    public static Integer konverterLongTilInteger(Long valueLong) {
        if (valueLong==null){
            return null;
        } else {
            return valueLong.intValue();
        }
    }

    public static String formaterIntegerSomStortTall(Integer tall) {
        if (tall==null) {
            return "";
        } else {
            //return String.format("%1$,",tall);
            return String.format(Locale.of("no"),"%,d", tall);
        }
    }

    public static String formaterIntegerSomStortTallMedPadding(Integer tall) {
        if (tall==null) {
            return "";
        } else {
            //return String.format("%1$,",tall);
            return String.format(Locale.of("no"),"%,10d", tall);
        }
    }


    // === FORELDETE METODER ===

    /**
     * @deprecated bruk formaterIntegerSom00 i stedet
     *
     */
    @Deprecated
    public static String integerFormatertSom00(Integer tall) {
        return String.format("%02d",tall);
    }

    /**
     * @deprecated bruk formaterIntegerSom000 i stedet
     *
     */
    @Deprecated
    public static String integerFormatertSom000(Integer tall) {
        return String.format("%03d",tall);
    }


    /**
     * @deprecated Bruk konverterDoubleTilIntegerFormatertSomStreng i stedet
     *
     */
    @Deprecated
    public static String konverterDoubleTilFormatertIntegerSomStreng(double tallDouble){
        Integer tallInteger = Math.round((float)tallDouble);
        return integerFormatertSomStortTall(tallInteger);
    }


    /**
     * @deprecated Bruk formaterIntegerSomStortTall() i stedet
     *
     */
    @Deprecated
    public static String integerFormatertSomStortTall(Integer tall) {
        return formaterIntegerSomStortTall(tall);
    }

    /**
     * @deprecated Bruk formaterIntegerSomStortTallMedPadding i stedet
     *
     */
    @Deprecated
    public static String integerFormatertSomStortTallMedPadding(Integer tall) {
        return formaterIntegerSomStortTallMedPadding(tall);
    }

    public static Integer konverterBigdecimalTilInteger(BigDecimal bigDecimal) {
        return konverterBigdecimalTilInteger(bigDecimal,false);
    }

    public static Integer konverterBigdecimalTilInteger(BigDecimal bigDecimal, boolean nullSom0) {
        if (bigDecimal == null) {
            if (nullSom0) {
                return 0;
            } else {
                return null;
            }
        } else {
            return konverterStrengMedDesimalTilInteger(bigDecimal.toString());
        }
    }
}
