package de.fh.uninformedSearch;

import de.fh.blanks.HunterWorld;
import de.fh.blanks.Point;
import de.fh.suche.Knoten;

public class Tiefensuche extends UninformedSearch{

    public Tiefensuche(HunterWorld hunterWorld, Point zielPosition){
        super(hunterWorld, zielPosition);
    }


    /**
     * Konkrete Implentierung des Einf√ºgens eines Knoten in
     * die Openlist bei der Tiefensuche
     *
     * @param expansionsKandidat
     */
    @Override
    public void fuegeKnotenEin(Knoten expansionsKandidat) {

        //TODO Tiefensuche (Beispiel)
        //Implementiert openList.add(Index,exp), mit dem richtigen Index gem‰ﬂ Suchstrategie
        openList.add(0,expansionsKandidat);
    }
}
