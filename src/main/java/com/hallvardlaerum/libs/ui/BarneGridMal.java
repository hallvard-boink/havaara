package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.database.EntitetserviceMedForelderAktig;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;

/**
 * Denne gir standardfunksjonalitet for å vise og redigere barneposter innenfor et redigeringsområde.
 * Lag egen klasse som utvider denne, og populer BarneKlasse og ForelderKlasse med ekte entiteter.
 *
 * Legg til klassen som en vanlig grid i redigeringsområdet med leggTilAndrefelterUnder(),
 * og sørg for å oppdatere innholdet manuelt under instansOppdaterEkstraRedigeringsfelter().
 *
 * Kolonnene oppgis med en ArrayList av Valueprovider
 *
 * @param <BarneKlasse>
 * @param <ForeldreKlasse>
 */


public class BarneGridMal<BarneKlasse extends EntitetMedForelderAktig, ForeldreKlasse extends AbstraktEntitet> extends Grid<BarneKlasse>{
    EntitetserviceMedForelderAktig<BarneKlasse, ForeldreKlasse> barneEntitetservice;
    EntitetserviceAktig<ForeldreKlasse> forelderEntitetservice;
    RedigeringsomraadeAktig<BarneKlasse> barneRedigeringsomraade;
    RedigeringsomraadeAktig<ForeldreKlasse> forelderRedigeringsomraade;
    ArrayList<ValueProvider<BarneKlasse,?>> kolonneArrayList;

    public BarneGridMal(EntitetserviceMedForelderAktig<BarneKlasse, ForeldreKlasse> barneEntitetservice, EntitetserviceAktig<ForeldreKlasse> forelderEntitetservice,
                        ArrayList<ValueProvider<BarneKlasse, ?>> kolonneArrayList) {
        this.barneEntitetservice = barneEntitetservice;
        this.forelderEntitetservice = forelderEntitetservice;
        this.barneRedigeringsomraade = barneEntitetservice.hentRedigeringsomraadeAktig();
        this.forelderRedigeringsomraade = forelderEntitetservice.hentRedigeringsomraadeAktig();
        this.kolonneArrayList = kolonneArrayList;

        leggTilVerdikolonner();
        leggTilKnappekolonnerOgTitler();
        tilpassGrid();

    }

    private void leggTilVerdikolonner(){
        for (ValueProvider<BarneKlasse,?> kolonne: kolonneArrayList) {
            this.addColumn(kolonne);
        }
    }

    private void leggTilKnappekolonnerOgTitler(){
        this.addColumn(new NativeButtonRenderer<>("Rediger", barn -> {
            RedigerEntitetDialog<BarneKlasse, ForeldreKlasse> redigeringsdialog =
                    new RedigerEntitetDialog<>(barneEntitetservice, forelderEntitetservice,
                            "Rediger " + barneEntitetservice.hentEntitetsnavn(), "" );
            redigeringsdialog.vis(barn);
        }));

        this.addColumn(new NativeButtonRenderer<>("Slett", bilIRad -> {
            SlettEntitetDialog<BarneKlasse> bilSlettEntitetDialog = new SlettEntitetDialog<>(this.barneEntitetservice);
            bilSlettEntitetDialog.vis(bilIRad);
        }));

    }

    private void tilpassGrid() {
        addThemeVariants(GridVariant.LUMO_COMPACT);
        getHeaderRows().add(this.appendHeaderRow()); // ekstra toppradbilerGrid
        Paragraph tittelBiler = new Paragraph(barneEntitetservice.hentEntitetsnavn());
        tittelBiler.setClassName(LumoUtility.FontSize.SMALL);
        this.getHeaderRows().getFirst().getCells().getFirst().setComponent(tittelBiler);


        NativeButtonRenderer<BarneKlasse> litenLeggTilNyBilButton = new NativeButtonRenderer<>("Opprett ny",entitet-> {
            RedigerEntitetDialog<BarneKlasse, ForeldreKlasse> redigeringsdialog =
                    new RedigerEntitetDialog<>(barneEntitetservice, forelderEntitetservice,
                            "Rediger " + barneEntitetservice.hentEntitetsnavn(), "" );
            redigeringsdialog.vis(barneEntitetservice.opprettEntitetMedForelder());
        });
        this.getHeaderRows().getLast().getCells().get(1).setComponent(litenLeggTilNyBilButton.createComponent(this.barneEntitetservice.opprettEntitet()));
    }
}
