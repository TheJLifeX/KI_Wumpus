package de.fh;

import java.util.LinkedList;
import de.fh.agent.WumpusHunterAgent;
import de.fh.blanks.CellInfo;
import de.fh.blanks.CellType;
import de.fh.blanks.HunterWorld;
import de.fh.blanks.Point;
import de.fh.heuristicalSearch.AStern;
import de.fh.heuristicalSearch.Bestensuche;
import de.fh.heuristicalSearch.Dijkstra;
import de.fh.suche.Suche;
import de.fh.suche.Suche.Suchstrategie;
import de.fh.uninformedSearch.Breitensuche;
import de.fh.uninformedSearch.Tiefensuche;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;

/* Test */

/*
 * DIESE KLASSE VERÄNDERN SIE BITTE NUR AN DEN GEKENNZEICHNETEN STELLEN
 * wenn die Bonusaufgabe bewertet werden soll.
 */
public class MyAgent extends WumpusHunterAgent {

	HunterWorld world = new HunterWorld();
	Suchstrategie suchstrategie = Suchstrategie.BREITENSUCHE;
	LinkedList<HunterAction> hunterActionList = new LinkedList<HunterAction>();
	boolean moveHunter = true;
	
	public static void main(String[] args) {

		MyAgent agent = new MyAgent("");
		MyAgent.start(agent,"127.0.0.1", 5000);
	}

	public MyAgent(String name) {

		super(name);
	}

	/**
	 * In dieser Methode kann das Wissen über die Welt (der State, der Zustand)
	 * entsprechend der aktuellen Wahrnehmungen anpasst, und die "interne Welt",
	 * die Wissensbasis, des Agenten kontinuierlich ausgebaut werden.
	 *
	 * Wichtig: Diese Methode wird aufgerufen, bevor der Agent handelt, d.h.
	 * bevor die action()-Methode aufgerufen wird...
	 *
	 * @param percept Aktuelle Wahrnehmung
	 * @param actionEffect Reaktion des Servers auf vorhergewählte Aktion
	 */
	@Override
	public void updateState(HunterPercept percept, HunterActionEffect actionEffect) {
		this.world.updateState(percept, actionEffect);
	}

	/**
	 * Diesen Part erweitern Sie so, dass die nächste(n) sinnvolle(n) Aktion(en),
	 * auf Basis der vorhandenen Zustandsinformationen und gegebenen Zielen, ausgeführt wird/werden.
	 * Der action-Part soll den Agenten so intelligent wie möglich handeln lassen
	 *
	 * Beispiel: Wenn die letzte Wahrnehmung
	 * "percept.isGlitter() == true" enthielt, ist "HunterAction.GRAB" eine
	 * geeignete T�tigkeit. Wenn Sie wissen, dass ein Quadrat "unsicher"
	 * ist, k�nnen Sie wegziehen
	 *
	 * @return Die nächste HunterAction die vom Server ausgeführt werden soll
	 */
	@Override
	public HunterAction action() {

		/*HunterAction
        M�gliche HunterActions sind m�glich:

       	HunterAction.GO_FORWARD
       	HunterAction.TURN_LEFT
		HunterAction.TURN_RIGHT
		HunterAction.SHOOT
		HunterAction.SIT
		HunterAction.GRAB
		HunterAction.QUIT_GAME
		*/
		
		

//		this.world.processAction(nextAction);
//		nextAction = HunterAction.GO_FORWARD;
//		System.out.println(world);
		
		Suche suche = null;
		Point zielPosition = new Point(4, 4);
		if (this.moveHunter) {
			
			nextAction = HunterAction.SIT;
			// Test
			// Virtuelle Welt aufbauen
			for (int i = 1; i < 4; i++) {
				world.set(i, 1, new CellInfo(CellType.BREEZE));
			}
			world.set(3, 2, new CellInfo(CellType.BREEZE));
			world.set(3, 3, new CellInfo(CellType.BREEZE));
			
			world.set(2, 3, new CellInfo(CellType.BREEZE));
			world.set(2, 3, new CellInfo(CellType.STENCH));
			
			world.set(2, 4, new CellInfo(CellType.BREEZE));
			world.set(3, 4, new CellInfo(CellType.BREEZE));
			world.set(4, 4, new CellInfo(CellType.BREEZE));
			// end Virtuelle Welt aufbauen
			world.print();
            switch (suchstrategie){
                case TIEFENSUCHE:
                    suche = new Tiefensuche(this.world, zielPosition);
                    break;
                case BREITENSUCHE:
                    suche = new Breitensuche(this.world, zielPosition);
                    break;
                case DIJKSTAR:
                    suche = new Dijkstra(this.world, zielPosition);
                    break;
                case BESTENSUCHE:
                    suche = new Bestensuche(this.world, zielPosition);
                    break;
                case ASTERN:
                    suche = new AStern(this.world, zielPosition);
                    break;
            }
//            
    		hunterActionList = suche.start();
            
            moveHunter = false;
		} else {
    		if(hunterActionList != null) {
    			if(!hunterActionList.isEmpty()) {
    				nextAction = hunterActionList.remove();
    				System.out.println("-> " + nextAction.toString());
    			} else {
    				nextAction = HunterAction.QUIT_GAME;
    			}
    		}
		}
	
		return nextAction;
	}
}