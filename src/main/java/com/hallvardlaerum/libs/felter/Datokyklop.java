package com.hallvardlaerum.libs.felter;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Locale;

public class Datokyklop {
    private static Datokyklop datokyklop;
    private DatePicker.DatePickerI18n datoformatI18n;
    private DateTimePicker.DateTimePickerI18n datotidformatI18n;

    private final DateTimeFormatter datoTidDateTimeFormatter;
    private final DateTimeFormatter filtilpassetDateTimeFormatter;
    private final DateTimeFormatter standardDateTimeFormatter;
    private final DateTimeFormatter norskDateTimeFormatter;
    private final DateTimeFormatter datoTidSekundDateTimeFormatter;
    private final DateTimeFormatter aarMaanedDateTimeFormatter;
    private final DateTimeFormatter aarDateTimeFormatter;
    private final DateTimeFormatter maanedsnavnAarTimeFormatter;


    public LocalDate finnFoersteIAaret(LocalDate dato){
        if (dato==null) {
            return null;
        }
        return LocalDate.of(dato.getYear(),1,1);

    }

    public LocalDate finnSisteIAaret(LocalDate datoFra){
        if (datoFra==null) {
            return null;
        }
        return LocalDate.of(datoFra.getYear(), 12,31);
    }

    public LocalDate finnSisteIMaaneden(LocalDate dato) {
        if (dato==null) {
            return null;
        }

        LocalDate nyDato = dato.plusMonths(1);
        nyDato = LocalDate.of(nyDato.getYear(), nyDato.getMonth(), 1);
        nyDato = nyDato.minus(1, ChronoUnit.DAYS);
        return nyDato;
    }


    public String formaterLocalDateMedPresisjon(LocalDate localDate, DatopresisjonEnum datopresisjonEnum) {
        if (localDate==null) {
            return "ukjent";
        }
        if (datopresisjonEnum==null) {
            return formaterDato(localDate);
        }

        switch (datopresisjonEnum) {
            case FULL_DATO -> {
                return formaterDato(localDate);
            }
            case MAANED -> {
                return formaterLocalDate_YYYY_MM(localDate);
            }
            case AAR -> {
                return formaterLocalDate_YYYY(localDate);
            }
            case UKJENT_DATO -> {
                return "(ukjent dato)";
            }
        }

        return "";

    }

    public String formaterLocalDate_MaanedsnavnAar(LocalDate localDate) {
        if (localDate==null) {
            return "";
        } else {
            return TekstKyklop.hent().settStorForbokstav(maanedsnavnAarTimeFormatter.format(localDate));
        }
    }


    public String formaterLocalDate_YYYY_MM(LocalDate localDate) {
        if (localDate==null) {
            return "";
        } else {
            return aarMaanedDateTimeFormatter.format(localDate);
        }
    }

    public String formaterLocalDate_YYYY(LocalDate localDate) {
        if (localDate == null) {
            return "";
        } else {
            return aarDateTimeFormatter.format(localDate);
        }
    }

    public String formaterDatoTid(LocalDateTime localDateTime) {
        if (localDateTime==null) {
            return "";
        } else {
            return datoTidDateTimeFormatter.format(localDateTime);
        }
    }

    public String formaterDato(LocalDate localDate) {
        if (localDate == null) {
            return "";
        } else {
            return standardDateTimeFormatter.format(localDate);
        }
    }

    public void fiksDatoformat(DatePicker datePicker) {
        datePicker.setI18n(datoformatI18n);
    }

    public void fiksDatotidformat(DateTimePicker datetimePicker) {
        datetimePicker.setDatePickerI18n(datoformatI18n);
        datetimePicker.setStep(Duration.ofMinutes(1));
    }


    public String hentDagensDatoSomEnkelDato() {
        return LocalDateTime.now().format(standardDateTimeFormatter);
    }


    public String hentNaavaerendeTidspunktSomDatoTid() {
        return LocalDateTime.now().format(datoTidDateTimeFormatter);
    }

    public String hentNaavaerendeTidspunktSomDatoTidSekund() {
        return LocalDateTime.now().format(datoTidSekundDateTimeFormatter);
    }


    public String hentDagensDatoTidForFiler(){
        return LocalDateTime.now().format(filtilpassetDateTimeFormatter);
    }

    public LocalDate opprettDatoSom_DDpMMpYYYY(String strDato){
        // 22.05.2023
        if (strDato==null || strDato.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(strDato, norskDateTimeFormatter);
        } catch (Exception e) {
            return null;
        }

    }

    public LocalDate opprettDatoSomYYYY_MM_DD(String strDato) {
        if (strDato==null || strDato.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(strDato, standardDateTimeFormatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static Datokyklop hent(){
        if (datokyklop == null) {
            datokyklop = new Datokyklop();
        }
        return datokyklop;
    }


    private Datokyklop() {
        String strDatoformat = "yyyy-MM-dd";
        aarMaanedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        aarDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");
        standardDateTimeFormatter = DateTimeFormatter.ofPattern(strDatoformat);
        norskDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        datoTidDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        filtilpassetDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm");
        datoTidSekundDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        maanedsnavnAarTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.of("nb"));

        datoformatI18n = new DatePicker.DatePickerI18n();
        datoformatI18n.setDateFormat(strDatoformat);


    }
}
