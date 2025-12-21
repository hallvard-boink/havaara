package com.hallvardlaerum.libs.feiloglogging;

/**
 * Tilbyr noen nyttige prosedyrer som stadig gjentas ved testing
 */
public class TestMester {

    public static void logOgAssertInteger(String testverdiString, Integer forventetInteger, Integer utregnetInteger)  {
        Loggekyklop.bruk().loggTEST("Testverdi: " + testverdiString);
        Loggekyklop.bruk().loggTEST("Forventet: " + forventetInteger);
        Loggekyklop.bruk().loggTEST("Utregnet: " + utregnetInteger);
        assert forventetInteger.equals(utregnetInteger);
    }

    public static void logOgAssertDouble(String testverdiString, Double forventetDouble, Double utregnetDouble)  {
        Loggekyklop.bruk().loggTEST("Testverdi: " + testverdiString);
        Loggekyklop.bruk().loggTEST("Forventet: " + forventetDouble);
        Loggekyklop.bruk().loggTEST("Utregnet: " + utregnetDouble);
        assert forventetDouble.equals(utregnetDouble);
    }

    public static void logOgAssertString(String testverdiString, String forventetString, String utregnetString)  {
        Loggekyklop.bruk().loggTEST("Testverdi: " + testverdiString);
        Loggekyklop.bruk().loggTEST("Forventet: " + forventetString);
        Loggekyklop.bruk().loggTEST("Utregnet: " + utregnetString);
        assert forventetString.equals(utregnetString);
    }

}
