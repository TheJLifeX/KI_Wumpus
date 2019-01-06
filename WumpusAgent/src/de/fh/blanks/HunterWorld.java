package de.fh.blanks;

import de.fh.suche.Suche;
import de.fh.uninformedSearch.Breitensuche;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class HunterWorld {
	private ArrayList<ArrayList<CellInfo>> view;

	private HunterAction previousAction;
	private HunterAction nextAction = HunterAction.GO_FORWARD;
	private Hashtable<Integer, Integer> previousStenchRadar;
	private Point hunterPosition = new Point(1, 1);
	private Direction hunterDirection = Direction.EAST;
	private int numArrows = 5;
	private int maxArrows = numArrows;
	private int wumpiKilled = 0;
	private Point goldPosition = new Point(-1, -1);
	private boolean hasGold = false;
	private boolean wumpusAlive = true;

	/**
	 * Hier wird einen Puffer von Actions gespeichert. Zum beispiel wenn der HUNTER
	 * von einem Quadrat A zu einem anderen B hingehen soll, dann sind die zu
	 * ausführenden Actions hier gespeichert.
	 */
	private LinkedList<HunterAction> bufferActions = new LinkedList<>();

	public HunterWorld() {
		view = new ArrayList<ArrayList<CellInfo>>();
	}

	/**
	 * Hier wird nur: Wenn action == TURN_LEFT oder TURN_RIGHT, die hunterDirection
	 * aktualisiert. Wenn action == GO_FORWARD, die hunterPosition aktualisiert.
	 * 
	 * Hier wird nicht bestimmt welche Aktion als nächstes ausgeführt wird, das wird
	 * in der updateState Methode gemacht.
	 * 
	 * @param previousAction : entpricht letzte Action, die auf dem Server
	 *                       ausgeführt wurde.
	 */
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
				throw new IllegalArgumentException("Unzulässige HunterAction");
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
				throw new IllegalArgumentException("Unzulässige HunterAction");
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
				throw new IllegalArgumentException("Unzulässige HunterAction");
			}
			break;
		}
		default:
			// Hier soll nichts gemacht werden
			break;
		}
	}

	/**
	 * Wichtig zu verstehen: Die updateState Methode wird zuerst ausgeführt, dann
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

		// Aktuelle Reaktion des Server auf die letzte Übermittelte Action.

		// Alle möglichen Serverrückmeldungen:

		if (actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
			this.setWallInToView(this.hunterPosition, this.hunterDirection);
		}

		if (actionEffect == HunterActionEffect.WUMPUS_KILLED) {
			this.wumpusAlive = false;
			++wumpiKilled;
		}
		
		if (actionEffect == HunterActionEffect.GAME_OVER) {
			// Das Spiel ist zum Ende.
			printQuitGame();
			print();
		}

		/*
		 * Mögliche Percepts Über die Welt erhält der Wumpushunter:
		 * 
		 * percept.isBreeze(); percept.isStench(); percept.isGlitter();
		 * percept.isRumble(); percept.isScream(); percept.isBump();
		 * percept.getWumpusStenchRadar()
		 */

		if (actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL
				|| actionEffect == HunterActionEffect.GAME_INITIALIZED
				|| actionEffect == HunterActionEffect.GOLD_FOUND) {

			// wenn Letzte Bewegungsaktion war gültig, dann update HunterPosition.
			this.updateHunterPosition(previousAction);

			if (actionEffect == HunterActionEffect.GOLD_FOUND) {
				// Nach HunterAction.GRAB wurde das Gold gefunden.
				this.hasGold = true;
				this.goldPosition.set(-1, -1);
			}

			// Letzte Bewegungsaktion war gültig
			if (this.previousAction == HunterAction.GO_FORWARD || this.previousAction == HunterAction.GRAB
					|| this.previousAction == HunterAction.SHOOT
					|| actionEffect == HunterActionEffect.GAME_INITIALIZED) {

				if (percept.isBreeze() || percept.isStench() || percept.isGlitter()) {

					if (percept.isBreeze()) {
						this.updateCell(CellType.BREEZE);
					}
					if (percept.isGlitter()) {
						this.goldPosition.set(this.hunterPosition);
						this.updateCell(CellType.GOLD);
						this.bufferActions.push(HunterAction.GRAB);
					}
				} else {
					this.updateCell(CellType.EMPTY);
				}
			}
		}

		if ((actionEffect == HunterActionEffect.NO_MORE_SHOOTS && this.hasGold)
				|| (!this.wumpusAlive && this.hasGold)) {
			this.quitGame();
		}

		if (bufferActions.isEmpty()) {
			this.exploreWorld();
		}

		/*
		 * percept.getWumpusStenchRadar() enthält alle Wumpi in max. R(ie)eichweite in
		 * einer Hashtable. Jeder Wumpi besitzt eine unique WumpusID (getKey). Die
		 * Manhattendistanz zum jeweiligen Wumpi ergibt sich aus der Gestanksitensität
		 * (getValue).
		 */

		Hashtable<Integer, Integer> stenchRadar = percept.getWumpusStenchRadar();


        // Gebe alle riechbaren Wumpis aus;
		if (stenchRadar.isEmpty()) {
//			System.out.println("Kein Wumpi zu riechen");
		} else {
			Point virtualPosition = this.getVirtualPositionHunter(hunterPosition, hunterDirection);
			if (virtualPosition.getX() == 0 || virtualPosition.getY() == 0 ||
					(this.get(virtualPosition) != null && this.get(virtualPosition).getType() == CellType.WALL )) {
				
				// entweder turn_left oder turn_right
				this.bufferActions.push(HunterAction.TURN_LEFT);
				
			} else {
				Map.Entry<Integer, Integer> entry = stenchRadar.entrySet().iterator().next();
				int key = (int) entry.getKey();
				int value = (int) entry.getValue();

				if (value == 3) {
					this.bufferActions.push(HunterAction.SIT);
				} else if (value == 2 || value == 1) {

					if (this.previousStenchRadar == null || this.previousStenchRadar.get(key) == null) {
						this.bufferActions.push(HunterAction.SIT);
					} else if (percept.isRumble() && value < this.previousStenchRadar.get(key)) {
						this.bufferActions.push(HunterAction.SHOOT);
						if (numArrows > 0)
							--numArrows;
					} else {
						this.bufferActions.push(HunterAction.SIT);
					}
				}
			}
			this.previousStenchRadar = stenchRadar;
		}
		
		this.nextAction = this.bufferActions.remove();
	}

	/**
	 * Gibt die nächste Action, die vom Hunter ausgeführt werden soll.
	 */
	public HunterAction getNextAction() {
		return this.nextAction;
	}

	/**
	 * Gibt das Element von der gewünschte Postion, sonst null wenn das Element
	 * nicht existiert.
	 */
	public CellInfo get(int x, int y) {
		if (x < 0 || y < 0) {
			return null;
		}

		try {
			ArrayList<CellInfo> row = this.view.get(y);
			return row.get(x);

		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public CellInfo get(Point p) {
		return this.get(p.getX(), p.getY());
	}

	/**
	 * Fügt ein Element ein Element an der gewünschte Postion ein, oder aktualisiert
	 * ihn, wenn er schon vorhanden ist
	 * 
	 * @return das vorherige Element, wenn ein Element aktualisiert wurde oder null
	 *         falls kein Element aktualisiert wurde.
	 */
	public CellInfo set(int x, int y, CellInfo newCellInfo) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("SET: Unzulässige Koordinaten " + "(" + x + ", " + y + ")" + "\n"
					+ "x und y Koodirnaten müssen größer gleich 0 sein.");
		}

		try {
			ArrayList<CellInfo> row = this.view.get(y);
			return row.set(x, newCellInfo);

		} catch (IndexOutOfBoundsException e) {
			this.add(x, y, newCellInfo);
			return null;
		}
	}

	/**
	 * Fügt ein Element an der gewünschte Postion ein
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

	/**
	 * Gibt alle Information von der HunterWorld aus der Konsole aus.
	 */
	public void print() {
		String out = "HUNTER_WORLD\n{ hunterPosition: " + hunterPosition + ", hunterDirection: " + hunterDirection
				+ ", goldPosition: " + goldPosition + ", hasGold: " + hasGold + ", Anzahl Pfeile"  + ", arrow shot: " + (maxArrows - numArrows)
				+ ", wumpusAlive: " + wumpusAlive + " }\n";

		int rows = view.size();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < view.get(row).size(); col++) {
				CellInfo cellInfo = get(col, row);
				if (cellInfo != null)
					out += cellInfo.toString()
							+ new String(new char[25 - cellInfo.toString().length()]).replace('\0', ' ');
				else
					out += "NULL" + new String(new char[25 - 4]).replace('\0', ' ');
				;
			}
			out += "\n";
		}

		System.out.println(out);
	}

	public void printQuitGame(){
		String gold = (hasGold) ? "Ja" : "Nein";
		String out = "Anzahl Pfeile: " + maxArrows  + ", Pfeile geschossen: " + (maxArrows - numArrows) + ", Wumpi getötet: " + wumpiKilled
				+ ", Gold gefunden?: " + gold;
		System.out.println(out);
	}

	public Direction getHunterDirection() {
		return hunterDirection;
	}

	/**
	 * Aktulisiert eine Zelle, an der Stelle, wo der Hunter liegt, auf Basis von der
	 * Information, die vom Server bekommen wurden.
	 * 
	 * @param cellType
	 */
	public void updateCell(CellType cellType) {
		if (cellType == CellType.GOLD) {
			this.goldPosition.set(this.hunterPosition.getX(), this.hunterPosition.getY());
		}

		CellInfo previousCellInfo = this.set(this.hunterPosition.getX(), this.hunterPosition.getY(), new CellInfo(cellType));

		// prüfe auf null wegen erster Aufruf( game initialized )
		// prüfe auf CellType.TARGET :: Methode wird aufgerufen, nur wenn der Hunter
		// eine ganz neue Zelle entdeckt.
		if (previousCellInfo == null || previousCellInfo.getType() == CellType.TARGET) {
			this.setProbabilityAllAroundCell(cellType);
		}

	}

	private void setProbabilityOfCell(CellType cellType, int x, int y){
		CellInfo targetUnkwonCell = this.get(x, y);
		
		if (targetUnkwonCell != null) {
			
			if(targetUnkwonCell.getType() == CellType.WALL) {
				return;
			}
			
			if (cellType == CellType.BREEZE) {
				targetUnkwonCell.setProbabilityPit(targetUnkwonCell.getProbabilityPit() + 50.0);
			} else if (cellType == CellType.EMPTY) {
				targetUnkwonCell.setProbabilityPit(0.0);
			}
		} else {
			if (cellType == CellType.BREEZE) {
				this.set(x, y, new CellInfo(x, y, 50.0));
			} else if (cellType == CellType.EMPTY) {
				this.set(x, y, new CellInfo(x, y, 0.0));
			}
		}
	}
	/**
	 * Aktulisiert bzw. setzt passende CellInfo von Typ "Unkwon" Cell rund um den Aktuelle
	 * Position der Hunter.
	 * 
	 * @param cellType
	 */
	private void setProbabilityAllAroundCell(CellType cellType) {
		// WEST
		int x = this.hunterPosition.getX() - 1;
		int y = this.hunterPosition.getY();
		if (x > 0)
			setProbabilityOfCell(cellType, x, y);

		// NORTH
		x = this.hunterPosition.getX();
		y = this.hunterPosition.getY() - 1;
		if (y > 0)
			setProbabilityOfCell(cellType, x, y);

		// EAST
		x = this.hunterPosition.getX() + 1;
		y = this.hunterPosition.getY();
		setProbabilityOfCell(cellType, x, y);

		// SOUTH
		x = this.hunterPosition.getX();
		y = this.hunterPosition.getY() + 1;
		setProbabilityOfCell(cellType, x, y);

		/**
		 * Nach alle Aenderung hier wird die UnkwonCells sortiert damit die am
		 * wenigsten gefährliche "UNKWON" Cell am Anfang der Liste stehen.
		 */
		CellInfo.sortUnkwonCells();
	}

	/**
	 * Bestimmt die nächsten Actions und speichert die in der bufferActions list.
	 */
	public void exploreWorld() {

		try {
			CellInfo targetCell = CellInfo.getUnknownCells().remove();
			if (targetCell.getProbabilityPit() >= 100) {
				System.out.println("Ende :: sichere Welt komplett entdeckt!");
				this.quitGame();
				return;
			}
			Point targetPosition = targetCell.getPosition();
			this.get(targetPosition.getX(), targetPosition.getY()).setType(CellType.TARGET);
			Suche suche = new Breitensuche(this, targetPosition);
			this.bufferActions = suche.start();
		} catch (NoSuchElementException e) {
			System.out.println("Ende :: Welt komplett entdeckt!");
			this.quitGame();
		}
	}

	/**
	 * Setzt eine entdeckte Wand-Zelle in der Welt.
	 * 
	 * @param hunterPosition  :: aktuelle hunterPosition.
	 * @param hunterDirection :: aktuelle hunterDirection.
	 */
	private void setWallInToView(Point hunterPosition, Direction hunterDirection) {
		Point virtualHunterPosition = getVirtualPositionHunter(hunterPosition, hunterDirection);
		this.set(virtualHunterPosition.getX(), virtualHunterPosition.getY(), new CellInfo(CellType.WALL));
	}
	
	private Point getVirtualPositionHunter(Point hunterPosition, Direction hunterDirection) {
		Point virtualHunterPosition = new Point(hunterPosition.getX(), hunterPosition.getY());

		switch (hunterDirection) {
		case NORTH:
			virtualHunterPosition.setY(virtualHunterPosition.getY() - 1);
			break;
		case EAST:
			virtualHunterPosition.setX(virtualHunterPosition.getX() + 1);
			break;
		case SOUTH:
			virtualHunterPosition.setY(virtualHunterPosition.getY() + 1);
			break;
		case WEST:
			virtualHunterPosition.setX(virtualHunterPosition.getX() - 1);
			break;
		default:
			throw new IllegalArgumentException("Unzulässige HunterAction");
		}
		return virtualHunterPosition;
	}
	
	public ArrayList<ArrayList<CellInfo>> getView() {
		return this.view;
	}

	public Point getHunterPosition() {
		return this.hunterPosition;
	}
	
	/**
	 * Hunter geht wieder zur Position (1, 1), dann das Spiel beenden
	 */
	private void quitGame() {
		this.bufferActions.clear();
		Point targetPosition = new Point(1, 1);
		this.get(targetPosition.getX(), targetPosition.getY()).setType(CellType.TARGET);
		Suche suche = new Breitensuche(this, targetPosition);
		this.bufferActions = suche.start();
		this.bufferActions.add(HunterAction.QUIT_GAME);
	}
}
