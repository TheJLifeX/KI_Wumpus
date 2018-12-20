package de.fh.uninformedSearch;

import de.fh.blanks.HunterWorld;
import de.fh.blanks.Point;
import de.fh.suche.Knoten;

public class Breitensuche extends UninformedSearch{

    public Breitensuche(HunterWorld hunterWorld, Point zielPosition){
        super(hunterWorld, zielPosition);
    }


    /**
     * Konkrete Implentierung des Einfügens eines Knoten in
     * die Openlist bei der Tiefensuche
     *
     * @param expansionsKandidat
     */
    @Override
    public void fuegeKnotenEin(Knoten expansionsKandidat) {

        // TODO Breitensuche
        //Implementiert openList.add(Index,exp), mit dem richtigen Index gemäß Suchstrategie
    	
        openList.add(expansionsKandidat);
    }
}
