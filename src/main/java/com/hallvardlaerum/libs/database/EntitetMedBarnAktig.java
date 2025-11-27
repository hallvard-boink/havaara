package com.hallvardlaerum.libs.database;

import java.util.ArrayList;

public interface EntitetMedBarnAktig<Barneklasse extends EntitetAktig> extends EntitetAktig{

    ArrayList<Barneklasse> hentBarn();

}
