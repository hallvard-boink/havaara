package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;

import java.util.List;

/**
 * Denne bør erstattes med HallvardsGrid
 */
@Deprecated
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
