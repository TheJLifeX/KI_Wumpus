package de.fh.uninformedSearch;

import de.fh.blanks.HunterWorld;
import de.fh.blanks.Point;
import de.fh.suche.Knoten;
import de.fh.suche.Suche;

public abstract class UninformedSearch extends Suche {


    public UninformedSearch(HunterWorld hunterWorld, Point zielPosition) {
        super(hunterWorld, zielPosition);
    }

    /**
     * Konkrete Implentierung des Einfügens eines Knoten in
     * die Openlist der entsprechenden Suche
     *
     * @param expansionsKandidat
     */
    public abstract void fuegeKnotenEin(Knoten expansionsKandidat);

}
