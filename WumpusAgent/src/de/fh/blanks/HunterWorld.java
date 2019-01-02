package de.fh.blanks;

import de.fh.suche.Suche;
import de.fh.uninformedSearch.Breitensuche;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HunterWorld {
	private ArrayList<ArrayList<CellInfo>> view;

//	private HunterPercept previousPercept; Diese Variable wird eventuell sp�ter benutzt werden.
	private HunterAction previousAction;
	private HunterActionEffect actionEffect;
	private HunterAction nextAction = HunterAction.GO_FORWARD;
//	private HunterActionEffect previousActionEffect; Diese Variable wird eventuell sp�ter benutzt werden.
	private HunterPercept percept;
	private Hashtable<Integer, Integer> stenchRadar;

	private Point hunterPosition = new Point(1, 1);
	private Direction hunterDirection = Direction.EAST;
	private int numArrows = 5;
	private Point goldPosition = new Point(-1, -1);
	private boolean hasGold = false;
	private boolean wumpusAlive = true;


	/*
	  Diese Attribute werden benutzt um die 2 verschiedenen Wumpi-Arten festzustellen und spaeter zu toeten
	*/

    // Wenn wir den Wumpus riechen, setzen wir aus um festzustellen ob dieser aktiv oder passiv ist
	private boolean doSit;
	private int sitCounter;
    // Ein Boolean, ist entweder null (noch keine Information), true (der Wumpus ist passiv) oder false (der Wumpus ist aktiv)
	private Boolean wumpusIsPassive;
	// Wird benutzt um zu gucken ob sich die Intensitaet des Geruchs aendert, um dann zu entscheiden um welchen Wumpus es sich handelt
	private Integer oldStenchIntensity;



	/**
	 * Hier wird einen Puffer von Actions gespeichert.
	 * Zum beispiel wenn der HUNTER von einem Quadrat A zu einem anderen B hingehen soll, dann sind die zu ausf�hrenden Actions hier gespeichert.
	 */
	private LinkedList<HunterAction> bufferActions = new LinkedList<>();

	public HunterWorld() {
		view = new ArrayList<ArrayList<CellInfo>>();
		wumpusIsPassive = null;
		oldStenchIntensity = null;
		doSit = false;
		sitCounter = 0;
	}

	/**Hier wird nur:
	 * Wenn action == TURN_LEFT oder TURN_RIGHT, die hunterDirection aktualisiert.
	 * Wenn action == GO_FORWARD, die hunterPosition aktualisiert.
	 * 
	 * Hier wird nicht bestimmt welche Aktion als n�chstes ausgef�hrt wird,
	 * das wird in der updateState Methode gemacht.
	 * 
	 * @param previousAction : entpricht letzte Action, die auf dem Server ausgef�hrt wurde.
	 */

	/**
	 * Wichtig zu verstehen: Die updateState Methode wird zuerst ausgef�hrt, dann
	 * die action Methode.
	 * 
	 * @param percept: Information, die wir von der Server bekommen haben, durch
	 *        unsere letzte Action.
	 * @param actionEffect: Information, die wir von der Server bekommen haben,
	 *        durch unsere letzte Action.
	 */
	public void updateState(HunterPercept percept, HunterActionEffect actionEffect, HunterAction previousAction) {
		/**
		 * Je nach Sichtbarkeit & Schwierigkeitsgrad (laut Serverkonfiguration) aktuelle
		 * Wahrnehmung des Hunters. Beim Wumpus erhalten Sie je nach Level mehr oder
		 * weniger Mapinformationen.
		 */
		this.percept = percept;
		this.actionEffect = actionEffect;

		// Aktuelle Reaktion des Server auf die letzte �bermittelte Action.


		stenchRadar = percept.getWumpusStenchRadar();
		System.out.println("STENCH_RADAR: " + stenchRadar.get(10));

		// Alle m�glichen Serverr�ckmeldungen:
		if(wumpusIsPassive == null){
			stenchRadar = this.percept.getWumpusStenchRadar();
			Integer stenchIntensity = null;
			if(!stenchRadar.values().isEmpty())
				stenchIntensity = (Integer) stenchRadar.values().toArray()[0];

			if(sitCounter > 0)
				--sitCounter;

			// Prueft ob sich die intensitaet veraendert, wenn ja ist der Wumpus aktiv, sonst passiv
			if(stenchIntensity == null || (oldStenchIntensity != null && oldStenchIntensity.intValue() != stenchIntensity.intValue()))
				wumpusIsPassive = false;
			else if(oldStenchIntensity != null && sitCounter == 0)
				wumpusIsPassive = true;

			if(!doSit && stenchIntensity != null){
				// Anzahl an Zuegen die der Hunter wartet um zu entscheiden um welchen Wumpus es sich handelt
				sitCounter = 8;
				// Wir legen einmal die alte Intensitaet fest und gucken sich diese mit der neuen unterscheidet
				oldStenchIntensity = stenchIntensity;
				// Der Hunter soll nichts tun um zu entscheiden um welchen Wumpus es sich handelt
				doSit = true;
			}
		}else{
			doSit = false;
			sitCounter = 0;
		}


		System.out.println("!!!Wumpus-Art!!! : " + wumpusIsPassive);



		System.out.println("wumpusIsPassive : " + wumpusIsPassive);

		if (actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
			this.setWallInToView(this.hunterPosition, this.hunterDirection);
		}

		if (actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
			// Nur bei Multiplayermodus
			// Letzte Bewegungsaktion war ein Zusammensto� einem weiteren Hunter
		}



		/*
		 * M�gliche Percepts �ber die Welt erh�lt der Wumpushunter:
		 * 
		 * percept.isBreeze(); percept.isStench(); percept.isGlitter();
		 * percept.isRumble(); percept.isScream(); percept.isBump();
		 * percept.getWumpusStenchRadar()
		 */

		if (actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL || actionEffect == HunterActionEffect.GAME_INITIALIZED || actionEffect == HunterActionEffect.GOLD_FOUND) {
			
			// wenn Letzte Bewegungsaktion war g�ltig, dann update HunterPosition.
			updateHunterPosition(previousAction);
			
			
			if (actionEffect == HunterActionEffect.GOLD_FOUND) {
				// Nach HunterAction.GRAB wurde das Gold gefunden.
//				this.updateCell(CellType.EMPTY);
				this.goldPosition.set(-1, -1);
			}
			
			// Letzte Bewegungsaktion war g�ltig
			if (this.previousAction != HunterAction.TURN_LEFT || this.previousAction != HunterAction.TURN_RIGHT) {
				if( percept.isBreeze() || percept.isStench() || percept.isGlitter() ) {
					
					// TODO: falls beide Bedingung true sind, dann zwei typ in der Cell speichern.
					if (percept.isBreeze()) {
						this.updateCell(CellType.BREEZE);
					}
					if (percept.isStench()) {
						this.updateCell(CellType.STENCH);
					}
					if (percept.isGlitter()) {
						this.goldPosition.set(this.hunterPosition); // Kein Getter f�r hunterPosition n�tig. Neue Point.set(Point p) Methode benutzt.
						// this.goldPosition.set(this.getHunterPosition().getX(), this.getHunterPosition().getY());
						this.updateCell(CellType.GOLD);
						this.bufferActions.push(HunterAction.GRAB);
					}
				}  else {
					this.updateCell(CellType.EMPTY);
				}			
			}
		}
		

		// TODO Wumpus t�ten.
		// Also 1* Erkennen mit h�heren Wahrscheinlichkeit, wo der Wumpus ist.
		//      2* Actions festlegen um ihn zu t�ten (also in der bufferActions pushen).
		
		/*
		 * percept.getWumpusStenchRadar() enth�lt alle Wumpi in max. R(ie)eichweite in
		 * einer Hashtable. Jeder Wumpi besitzt eine unique WumpusID (getKey). Die
		 * Manhattendistanz zum jeweiligen Wumpi ergibt sich aus der Gestanksitensit�t
		 * (getValue).
		 */

		// Beispiel:

		// Gebe alle riechbaren Wumpis aus
//		System.out.println("WumpusID: Intensitaet");
		if (stenchRadar.isEmpty()) {
//			System.out.println("Kein Wumpi zu riechen");
		}
//		for (Map.Entry<Integer, Integer> g : stenchRadar.entrySet()) {
//			System.out.println(g.getKey() + ":\t\t" + g.getValue());
//		}
	}

	/**
	 * Gibt die n�chste Action, die vom Hunter augef�hrt werden soll.
	 */
	public HunterAction action() {
		System.out.println("Sit?????????????: " + doSit);
		if(doSit && sitCounter > 0){
			bufferActions.push(HunterAction.SIT);
		}

		if(bufferActions.isEmpty()) {
			this.exploreWorld();
			System.out.println("Buffer Actions List:");
			this.bufferActions.forEach(System.out::println);
			System.out.println("");
			this.print();
		}

		nextAction = this.bufferActions.remove();

/*		if (actionEffect == HunterActionEffect.GAME_OVER) {
			// Das Spiel ist verloren
			// TODO : Ausgabe von allen gefragete Information von der Aufgabestellung.
			this.nextAction = HunterAction.QUIT_GAME;
		}
		if (this.actionEffect == HunterActionEffect.WUMPUS_KILLED) {
			this.wumpusAlive = false;
		}

		if (actionEffect == HunterActionEffect.NO_MORE_SHOOTS) {
			// TODO A* bis zur Position (1, 1) ( einfach schon implementierte Suchstrategie anwenden)
		}*/
		return nextAction;
	}

	/**
	 * Gibt das Element von der gew�nschte Postion, sonst null wenn das Element nicht existiert.
	 */
	public CellInfo get(int x, int y) 
	{
		if (x < 0 || y < 0)
			return null;

		try 
		{
			ArrayList<CellInfo> row = view.get(y);
			CellInfo cellInfo = row.get(x);
			return cellInfo;

		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}


	public Direction getHunterDirection() {
		return hunterDirection;
	}

	/**
	 * Aktulisiert eine Zelle, an der Stelle, wo der Hunter liegt, auf Basis von der Information,
	 * die vom Server bekommen wurden.
	 * 
	 * @param cellType
	 */
	public void updateCell(CellType cellType) {
		if( cellType == CellType.GOLD) {
			this.goldPosition.set(this.hunterPosition.getX(), this.hunterPosition.getY());
		}
		
		CellInfo previousCellInfo = this.get(this.hunterPosition.getX(), this.hunterPosition.getY());
		
		//  pr�fe auf null wegen erster Aufruf( game initialized )
		//  pr�fe auf CellType.TARGET :: Methode wird aufgerufen, nur wenn der Hunter eine ganz neue Zelle entdeckt.
		if( previousCellInfo == null || previousCellInfo.getType() == CellType.TARGET ) {
			this.setProbabilityAllAroundCell(cellType);
		}
		
		this.set(this.hunterPosition.getX(), this.hunterPosition.getY(), new CellInfo(cellType));
	}
	
	public ArrayList<ArrayList<CellInfo>> getView(){
		return this.view;
	}
	
	public Point getHunterPosition() {
		return this.hunterPosition;
	}
	
	/**
	 * Aktualisiert bzw. setzt passende CellInfo von Typ Wall rund um die aktuelle Position des Hunter.
	 * Aktualisiere Informationen der Zellen rund um den Hunter anhand �bergebenem CellType.
	 * 
	 * @param cellType
	 */
	private void setProbabilityAllAroundCell(CellType cellType) 
	{	
		// WEST
		{
			int x = hunterPosition.getX() - 1;
			int y = hunterPosition.getY();

			if (x > 0) 
			{
				CellInfo targetWall = get(x, y);

				if (targetWall != null) 
				{
					if (cellType == CellType.BREEZE) 
						targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
					else if (cellType == CellType.STENCH)
						targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
					else if (cellType == CellType.EMPTY) 
					{
						targetWall.setProbabilityPit(0.0);
						targetWall.setProbabilityWumpus(0.0);
					}
				} 
				else 
				{
					if (cellType == CellType.BREEZE)
						set(x, y, new CellInfo(x, y, 50.0, 0.0));
					else if (cellType == CellType.STENCH)
						set(x, y, new CellInfo(x, y, 0.0, 50.0));
					else if (cellType == CellType.EMPTY)
						set(x, y, new CellInfo(x, y, 0.0, 0.0));
				}
			}
		}

		// NORTH
		{
			int x = hunterPosition.getX();
			int y = hunterPosition.getY() - 1;

			if (y > 0) 
			{
				CellInfo targetWall = get(x, y);
				if (targetWall != null) 
				{
					if (cellType == CellType.BREEZE)
						targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
					else if (cellType == CellType.STENCH)
						targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
					else if (cellType == CellType.EMPTY) 
					{
						targetWall.setProbabilityPit(0.0);
						targetWall.setProbabilityWumpus(0.0);
					}

				} 
				else 
				{
					if (cellType == CellType.BREEZE)
						set(x, y, new CellInfo(x, y, 50.0, 0.0));
					else if (cellType == CellType.STENCH)
						set(x, y, new CellInfo(x, y, 0.0, 50.0));
					else if (cellType == CellType.EMPTY)
						set(x, y, new CellInfo(x, y, 0.0, 0.0));
				}
			}
		}

		// EAST
		{
			int x = hunterPosition.getX() + 1;
			int y = hunterPosition.getY();
			CellInfo targetWall = get(x, y);
			if (targetWall != null) 
			{
				if (cellType == CellType.BREEZE) 
					targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
				else if (cellType == CellType.STENCH)
					targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
				else if (cellType == CellType.EMPTY) 
				{
					targetWall.setProbabilityPit(0.0);
					targetWall.setProbabilityWumpus(0.0);
				}

			} 
			else 
			{
				if (cellType == CellType.BREEZE)
					set(x, y, new CellInfo(x, y, 50.0, 0.0));
				else if (cellType == CellType.STENCH)
					set(x, y, new CellInfo(x, y, 0.0, 50.0));
				else if (cellType == CellType.EMPTY)
					set(x, y, new CellInfo(x, y, 0.0, 0.0));
			}
		}

		// SOUTH
		{
			int x = hunterPosition.getX();
			int y = hunterPosition.getY() + 1;

			CellInfo targetWall = get(x, y);
			if (targetWall != null) 
			{
				if (cellType == CellType.BREEZE)
					targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
				else if (cellType == CellType.STENCH)
					targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
				else if (cellType == CellType.EMPTY) 
				{
					targetWall.setProbabilityPit(0.0);
					targetWall.setProbabilityWumpus(0.0);
				}

			} 
			else 
			{
				if (cellType == CellType.BREEZE)
					set(x, y, new CellInfo(x, y, 50.0, 0.0));
				else if (cellType == CellType.STENCH)
					set(x, y, new CellInfo(x, y, 0.0, 50.0));
				else if (cellType == CellType.EMPTY)
					set(x, y, new CellInfo(x, y, 0.0, 0.0));
			}
		}

		/**
		 * Nach alle Anderung hier wird die wallList noch sortiert damit die am wenigsten gef�hrliche "Wall"
		 * am Anfang der Liste stehen.
		 */
		CellInfo.sortWallList();
	}
	
	/**
	 * Bestimmt die n�chsten Actions und speichert die in der bufferActions list.
	 */
	public void exploreWorld() {

		try {
			CellInfo targetCell = CellInfo.getUnknownCells().remove();
			if(targetCell.getEstimate() >= 110) {
				this.bufferActions.push(HunterAction.QUIT_GAME);
				System.out.println("Ende :: sichere Welt komplett entdeckt!");
				return;
			}
			Point zielPosition = targetCell.getPosition();
			this.get(zielPosition.getX(), zielPosition.getY()).setType(CellType.TARGET);
			Suche suche = new Breitensuche(this, zielPosition);
			this.bufferActions = suche.start();
		} catch (NoSuchElementException e) {
			this.bufferActions.push(HunterAction.QUIT_GAME);
			System.out.println("Ende :: Welt komplett entdeckt!");
		}
	}
	
	/**
	 * F�gt eine entdeckte Wand-Zelle in der Welt.
	 * @param hunterPosition
	 * @param hunterDirection
	 */
	private void setWallInToView(Point hunterPosition, Direction hunterDirection) {
		Point newHunterPosition = new Point(hunterPosition.getX(), hunterPosition.getY());

		switch (hunterDirection) {
		case NORTH:
			newHunterPosition.setY(newHunterPosition.getY() - 1);
			break;
		case EAST:
			newHunterPosition.setX(newHunterPosition.getX() + 1);
			break;
		case SOUTH:
			newHunterPosition.setY(newHunterPosition.getY() + 1);
			break;
		case WEST:
			newHunterPosition.setX(newHunterPosition.getX() - 1);
			break;
		default:
			throw new IllegalArgumentException("Unzul�ssige HunterAction");
		}

		this.set(newHunterPosition.getX(), newHunterPosition.getY(), null);
	}
	private void updateHunterPosition(HunterAction previousAction) {
		this.previousAction = previousAction;

		switch (previousAction) {
			case TURN_LEFT: {
				switch (hunterDirection) {
					case NORTH:
						this.hunterDirection = Direction.WEST;
						break;
					case EAST:
						this.hunterDirection = Direction.NORTH;
						break;
					case SOUTH:
						this.hunterDirection = Direction.EAST;
						break;
					case WEST:
						this.hunterDirection = Direction.SOUTH;
						break;
					default:
						throw new IllegalArgumentException("Unzul�ssige HunterAction");
				}
				break;
			}
			case TURN_RIGHT: {
				switch (hunterDirection) {
					case NORTH:
						this.hunterDirection = Direction.EAST;
						break;
					case EAST:
						this.hunterDirection = Direction.SOUTH;
						break;
					case SOUTH:
						this.hunterDirection = Direction.WEST;
						break;
					case WEST:
						this.hunterDirection = Direction.NORTH;
						break;
					default:
						throw new IllegalArgumentException("Unzul�ssige HunterAction");
				}
				break;
			}
			case GO_FORWARD: {
				switch (this.hunterDirection) {
					case NORTH:
						this.hunterPosition.setY(this.hunterPosition.getY() - 1);
						break;
					case EAST:
						this.hunterPosition.setX(this.hunterPosition.getX() + 1);
						break;
					case SOUTH:
						this.hunterPosition.setY(this.hunterPosition.getY() + 1);
						break;
					case WEST:
						this.hunterPosition.setX(this.hunterPosition.getX() - 1);
						break;
					default:
						throw new IllegalArgumentException("Unzul�ssige HunterAction");
				}
				break;
			}
			default:
				// Hier soll nichts gemacht werden
				break;
		}
	}

	/**
	 * F�gt ein Element ein Element an der gew�nschte Postion ein, oder aktualisiert ihn, wenn er schon
	 * vorhanden ist
	 *
	 * @return das vorherige Element, wenn ein Element aktualisiert wurde oder null falls kein Element aktualisiert wurde.
	 */
	public CellInfo set(int x, int y, CellInfo newCellInfo) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("SET: Unzul�ssige Koordinaten " + "(" + x + ", " + y + ")" + "\n"
					+ "x und y Koodirnaten m�ssen gr��er gleich 0 sein.");
		}

		try {
			ArrayList<CellInfo> row = this.view.get(y);
			CellInfo previousCellInfo = row.set(x, newCellInfo);
			return previousCellInfo;

		} catch (IndexOutOfBoundsException e) {
			this.add(x, y, newCellInfo);
			return null;
		}
	}

	/**
	 * F�gt ein Element an der gew�nschte Postion ein
	 *
	 * Wird nur in der Set Methode genutzt.
	 */
	private boolean add(int x, int y, CellInfo newCellInfo) {
		if (x < 0 || y < 0) {
			return false;
		}

		try {
			if (y >= this.view.size()) {

				ArrayList<CellInfo> newRow = null;
				for (int j = this.view.size(); j <= y; j++) {
					newRow = new ArrayList<CellInfo>();
					this.view.add(newRow);
					for (int k = 0; k < x; k++) {
						newRow.add(null);
					}
				}
				newRow.add(newCellInfo);
			} else {

				ArrayList<CellInfo> row = this.view.get(y);
				if (x >= row.size()) {
					for (int i = row.size(); i < x; i++) {
						row.add(null);
					}
					row.add(newCellInfo);
				} else {
					this.set(x, y, newCellInfo);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	// TODO diese toString wird nie aufgerufen. Bitte die entsprechen Stelle in der Knoten Klasse anpassen.
	@Override
	public String toString() {
		String s = "";
		for (int row = 0; row < this.view.size(); row++) {
			for (int col = 0; col < this.view.get(row).size(); col++) {
				final CellInfo cellInfo = this.get(col, row);
				if (cellInfo != null) {
					s += cellInfo.getType().toString().substring(0, 2);
				} else {
					s += "NULL";
				}
			}
		}
		return s;
	}

	/**
	 * Gibt alle Information von der HunterWorl aus der Konsole aus.
	 */
	public void print() {
		String out = "HUNTER_WORLD\n{ hunterPosition: " + hunterPosition + ", hunterDirection: " + hunterDirection
				+ ", goldPosition: " + goldPosition + ", hasGold: " + hasGold + ", numArrows: " + numArrows
				+ ", wumpusAlive: " + wumpusAlive + " }\n";

		int rows = view.size();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < view.get(row).size(); col++) {
				CellInfo cellInfo = get(col, row);
				if (cellInfo != null)
					out += cellInfo.toString() + new String(new char[25 - cellInfo.toString().length()]).replace('\0', ' ');
				else
					out += "NULL" + new String(new char[25 - 4]).replace('\0', ' ');;
			}
			out += "\n";
		}

		System.out.println(out);
	}

}
