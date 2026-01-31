package com.hallvardlaerum.libs.felter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Math.round;

public class DesimalMester {

    /**
     * @deprecated Bruk tilsvarende i HelTallMester i stedet
     * Her bestemmer DecimalFormat antall sifre, tydeligvis. Men da må ikke tallet bli for lite.
     */
    @Deprecated
    public static String konverterDoubleTilFormatertIntegerSomStreng(double tallDouble){
        Integer tallInteger = Math.round((float)tallDouble);
        return HelTallMester.integerFormatertSomStortTall(tallInteger);
    }

    public static Double brukToDesimaler(double tall){
        return rundAv(tall, 2);
    }


    private static double rundAv(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @deprecated Bruk tilsvarende i HelTallMester i stedet
     *
     */
    @Deprecated
    public static Integer konverterBigdecimalTilInteger(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        } else {
            return HelTallMester.konverterStrengMedDesimalTilInteger(bigDecimal.toString());
        }

    }
}
