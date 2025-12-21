package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.vaadin.flow.component.grid.Grid;

/**
 * Dette er en grid som er tilpasset slik jeg vil ha den.
 * @param <Entitet>
 */
public class HallvardsGrid<Entitet extends EntitetAktig> extends Grid<Entitet> {
    private GridInnholdsTypeEnum gridInnholdsTypeEnum;

    public HallvardsGrid(GridInnholdsTypeEnum gridInnholdsTypeEnum) {
        super();
        this.gridInnholdsTypeEnum = gridInnholdsTypeEnum;
    }
}
