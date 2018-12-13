package uninformedSearch;

import de.fh.pacman.PacmanPercept;
import de.fh.suche.Knoten;

public class Tiefensuche extends UninformedSearch{

    public Tiefensuche(PacmanPercept pacmanPercept, Knoten zielKnoten){
        super(pacmanPercept, zielKnoten);
    }


    /**
     * Konkrete Implentierung des Einfügens eines Knoten in
     * die Openlist bei der Tiefensuche
     *
     * @param expansionsKandidat
     */
    @Override
    public void fuegeKnotenEin(Knoten expansionsKandidat) {

        //TODO Tiefensuche (Beispiel)
        //Implementiert openList.add(Index,exp), mit dem richtigen Index gemäß Suchstrategie

        for(int i = 0; i < expansionsKandidat.getView().length; i++ ) {
            for (int j = 0; j < expansionsKandidat.getView()[i].length; j++) {
                System.out.print(expansionsKandidat.getView()[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------");
        openList.add(0,expansionsKandidat);

        System.out.println("openList.size(): " + openList.size());
        System.out.println("closedList.size(): " + closedList.size());
    }
}
