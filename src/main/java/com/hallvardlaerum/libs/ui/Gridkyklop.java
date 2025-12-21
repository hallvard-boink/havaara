package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;

import java.util.List;

/**
 * FastradGrid = Grid med rader som er opprett med setItems(). Kan også kalles småGrid?
 * FleksibelradGrid = Grid med rader som har DataProvider og henter inn rader etter behov. Kan også kalles storGrid?
 */
public class Gridkyklop {
    private static Gridkyklop gridkyklop;


    public void tilpassKolonnerIFastradGrid(Grid<?> grid) {
        List<? extends Grid.Column<?>> kolonner = grid.getColumns();
        grid.setMultiSort(true);
        for (Grid.Column<?> kol:kolonner){
            kol.setResizable(true);
            kol.setSortable(true);
        }
        grid.setSizeFull();
    }


    @Deprecated
    public HeaderRow alleRaderTilpassKolonnerOgOpprettFilteradIGrid(Grid<?> grid){
        HeaderRow headerRow = tilpassKolonnerOgOpprettFilterrad(grid);
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        return headerRow;
    }


    public HeaderRow porsjonsviseRaderTilpassKolonnerOgOpprettFilteradIGrid(Grid<?> grid){
        return tilpassKolonnerOgOpprettFilterrad(grid);
    }

    private HeaderRow tilpassKolonnerOgOpprettFilterrad(Grid<?> grid) {
        tilpassKolonnerIFastradGrid(grid);
        grid.getHeaderRows().clear();
        return grid.appendHeaderRow();
    }

    public static Gridkyklop hent(){
        if (gridkyklop == null) {
            gridkyklop = new Gridkyklop();
        }
        return gridkyklop;

    }

    private Gridkyklop() {
    }
}
