package com.hallvardlaerum.libs.felter;

/**
 * Static klasse som konverterer mellom ulike måleenheter
 */
public class EnhetskonverteringsMester {

    public static double konverterBytesTilMegabytes(Long bytes) {
        return (double) (bytes / (1024 * 1024));
    }

    public static double konverterBytesTilKilobytes(Long bytes) {
        return (double) bytes/1024;
    }

    public static String presenterFilstoerrelseMenneskelesbart(Long bytes) {
        Double tall = (double)0;
        if (bytes>500000) {
            tall = konverterBytesTilMegabytes(bytes);
            return DesimalMester.konverterDoubleTilFormatertIntegerSomStreng(tall) + " Mb";
        } else if (bytes > 5000) {
            tall = konverterBytesTilKilobytes(bytes);
            return DesimalMester.konverterDoubleTilFormatertIntegerSomStreng(tall) + " Kb";
        } else {
            return bytes + " bytes";
        }

    }



}
