package com.hallvardlaerum.libs.database;

import java.time.LocalDateTime;
import java.util.List;

public interface RepositoryTillegg<Entitet> {

    List<Entitet> findByRedigertDatoTidBetween(LocalDateTime fraDatoTid, LocalDateTime tilDatoTid);
    Entitet findFirstByOrderByRedigertDatoTidDesc();
}
