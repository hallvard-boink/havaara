package com.hallvardlaerum.libs.feiloglogging;

public class TestMester {

    public static void logOgAssertInteger(String testverdiString, Integer forventetInteger, Integer utregnetInteger)  {
        Loggekyklop.hent().loggTEST("Testverdi: " + testverdiString);
        Loggekyklop.hent().loggTEST("Forventet: " + forventetInteger);
        Loggekyklop.hent().loggTEST("Utregnet: " + utregnetInteger);
        assert forventetInteger.equals(utregnetInteger);
    }

    public static void logOgAssertDouble(String testverdiString, Double forventetDouble, Double utregnetDouble)  {
        Loggekyklop.hent().loggTEST("Testverdi: " + testverdiString);
        Loggekyklop.hent().loggTEST("Forventet: " + forventetDouble);
        Loggekyklop.hent().loggTEST("Utregnet: " + utregnetDouble);
        assert forventetDouble.equals(utregnetDouble);
    }

    public static void logOgAssertString(String testverdiString, String forventetString, String utregnetString)  {
        Loggekyklop.hent().loggTEST("Testverdi: " + testverdiString);
        Loggekyklop.hent().loggTEST("Forventet: " + forventetString);
        Loggekyklop.hent().loggTEST("Utregnet: " + utregnetString);
        assert forventetString.equals(utregnetString);
    }

}
