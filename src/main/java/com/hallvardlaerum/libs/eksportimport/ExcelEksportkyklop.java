package com.hallvardlaerum.libs.eksportimport;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.feiloglogging.LoggekyklopAktig;
import com.hallvardlaerum.libs.felter.Inspeksjonskyklop;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.filerogopplasting.StandardmappeEnum;
import com.vaadin.flow.component.notification.Notification;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ExcelEksportkyklop implements ExcelEksportkyklopAktig {
    private static ExcelEksportkyklop excelEksportkyklop;
    private Workbook workbook;
    private Sheet sheet;
    private Boolean brukXLSX = false;
    private OutputStream fileoutputstream;
    private CellStyle tittelradCellStyle;
    private String strFilnavn;
    private File eksportfilFile;


    // Denne skapte trøbbel for IDE'en - trenger den egentlig ikke
//    public <T extends EntitetAktig> void eksporterArrayListAvEntiteterSomXLS(Class<T> klasse, String filnavnString) {
//        EntitetserviceAktig<T> entitetservice = Backupkyklop.hent().finnEntitetservice(klasse);
//        if (entitetservice==null) {
//            Loggekyklop.hent().loggFEIL("Fant ikke entitetservice for " + klasse.getName() + " i Backupkyklop, avbryter");
//            return;
//        }
//
//        ArrayList<T> entiteter = entitetservice.finnAlle();
//        eksporterArrayListAvEntiteterSomXLS(entiteter,filnavnString);
//    }


    /**
     * Denne brukes hvis alle feltene i en entitet skal eksporteres, enkelt og greit
     *
     * @param entiteteneArrayList
     * @param strFilnavn
     * @param <T>
     */
    @Override
    public <T> void eksporterArrayListAvEntiteterSomXLS(ArrayList<T> entiteteneArrayList, String strFilnavn) {
        if (entiteteneArrayList==null || entiteteneArrayList.isEmpty()) {
            Loggekyklop.bruk().loggADVARSEL("Arraylist av entiteter var tom, avbryter eksport til XLS");
            return;
        }
        this.strFilnavn = Filkyklop.hent().hentEllerOpprettStandardmappe(StandardmappeEnum.TEMP).getAbsolutePath() + "/" + strFilnavn;
        eksportfilFile = new File(this.strFilnavn);

        opprettWorkbook();
        opprettTittelradstil();

        ArrayList<Inspeksjonskyklop.UtvidetFelt> felterArrayList = Inspeksjonskyklop.hent().byggOppFeltliste(entiteteneArrayList.getFirst());

        // Lag tittelrad
        Row tittelRow = sheet.createRow(0);
        for (int i=0; i<felterArrayList.size(); i++) {
            Cell tittelCell = tittelRow.createCell(i);
            tittelCell.setCellStyle(tittelradCellStyle);
            String navn = TekstKyklop.hent().settStorForbokstav(felterArrayList.get(i).getFelt().getName());
            tittelCell.setCellValue(navn);
            sheet.setColumnWidth(i, 40*256);
        }

        // Sett inn verdier
        for (int j=0; j<entiteteneArrayList.size(); j++) {
            Row rad = sheet.createRow(j+1);
            T entitet = entiteteneArrayList.get(j);
            for (int i=0; i<felterArrayList.size(); i++) {
                Cell celle = rad.createCell(i);
                Inspeksjonskyklop.UtvidetFelt utvidetFelt = felterArrayList.get(i);
                try {
                    Object verdiSomObjekt = utvidetFelt.getFelt().get(entitet);
                    if (verdiSomObjekt!=null) {
                        celle.setCellValue(verdiSomObjekt.toString());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        skrivTilFilOgFullfoer();
        return;
    }

    /**
     * Denne brukes hvis teksten er ferdig satt opp i ArrayList<ArrayList<String>>
     * Denne er bedre å bruke hvis man vil velge ut bestemte felter, styre kolonnetitlene selv, eller tilpasse formatet på cellene
     *
     * @param radeneArrayList
     * @param strFilnavn
     */
    @Override
    public void eksporterArrayListAvStrengerSomXLS(ArrayList<ArrayList<String>> radeneArrayList, String strFilnavn) {
        if (radeneArrayList==null || radeneArrayList.isEmpty()) {
            Loggekyklop.bruk().loggADVARSEL("Arraylist av strenger var tom, avbryter eksport til XLS");
            return;
        }
        this.strFilnavn = Filkyklop.hent().hentEllerOpprettStandardmappe(StandardmappeEnum.TEMP).getAbsolutePath() + "/" + strFilnavn;
        eksportfilFile = new File(this.strFilnavn);

        // xls. For xlsx, bruk XSSFWorkbook
        opprettWorkbook();
        opprettTittelradstil();

        // Finne passelig bredde
        Integer[] kolonnerMaxTegn = new Integer[radeneArrayList.getFirst().size()];
        for (int i=0; i<kolonnerMaxTegn.length; i++) {
            kolonnerMaxTegn[i]=0;
        }

        //Bygge opp celler i regnearket
        for (int radNr= 0; radNr<radeneArrayList.size(); radNr++) {
            Row rad = sheet.createRow(radNr);
            ArrayList<String> cellerArrayList = radeneArrayList.get(radNr);
            for (int celleNr = 0; celleNr<cellerArrayList.size(); celleNr++) {
                Cell cell = rad.createCell(celleNr);
                String verdi = cellerArrayList.get(celleNr);
                cell.setCellValue(verdi);
                if (radNr == 0){
                    cell.setCellStyle(tittelradCellStyle);  // Tittelraden skal bli bold
                }
                if (verdi!=null) {
                    if (kolonnerMaxTegn[celleNr]<verdi.length()) {
                        kolonnerMaxTegn[celleNr] = verdi.length();  //Finne radbredden
                    }
                }
            }
        }

        // Sett kolonnebredde
        for (int kolNr=0; kolNr<kolonnerMaxTegn.length; kolNr++) {
            sheet.setColumnWidth(kolNr, kolonnerMaxTegn[kolNr]*256);
        }

        skrivTilFilOgFullfoer();

        return;

    }

    private void opprettWorkbook() {
        if (brukXLSX) {
            workbook = new XSSFWorkbook();

        } else {
            workbook = new HSSFWorkbook();
        }

        try  {
            fileoutputstream = new FileOutputStream(eksportfilFile);
            sheet = workbook.createSheet("Data");

        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL(e.getMessage());
        }
    }

    private void opprettTittelradstil(){
        Font tittelFont = workbook.createFont();
        tittelFont.setBold(true);
        tittelFont.setFontHeightInPoints((short)(10));
        tittelFont.setFontName("Arial");
        tittelradCellStyle = workbook.createCellStyle();
        tittelradCellStyle.setFont(tittelFont);
    }

    private void skrivTilFilOgFullfoer(){
        try {
            workbook.write(fileoutputstream);
            workbook.close();
            Notification.show("Eksporterte entitetene som Excelfil \""  + strFilnavn + "\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static ExcelEksportkyklopAktig hent() {
        if (ExcelEksportkyklop.excelEksportkyklop == null) {
            ExcelEksportkyklop.excelEksportkyklop = new ExcelEksportkyklop();
        }
        return ExcelEksportkyklop.excelEksportkyklop;
    }

    private ExcelEksportkyklop() {
    }
}
