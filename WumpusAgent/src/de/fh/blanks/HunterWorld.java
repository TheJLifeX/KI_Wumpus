package de.fh.blanks;

import de.fh.suche.Suche;
import de.fh.uninformedSearch.Breitensuche;
import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class HunterWorld {
	private ArrayList<ArrayList<CellInfo>> view;

//	private HunterPercept previousPercept; Diese Variable wird eventuell später benutzt werden.
	private HunterAction previousAction;
	private HunterAction nextAction = HunterAction.GO_FORWARD;
//	private HunterActionEffect previousActionEffect; Diese Variable wird eventuell später benutzt werden.
	private HunterPercept percept;
	private Hashtable<Integer, Integer> stenchRadar;

	private Point hunterPosition = new Point(1, 1);
	private Direction hunterDirection = Direction.EAST;
	private int numArrows = 5;
	private Point goldPosition = new Point(-1, -1);
	private boolean hasGold = false;
	private boolean wumpusAlive = true;
	
	/**
	 * Hier wird einen Puffer von Actions gespeichert.
	 * Zum beispiel wenn der HUNTER von einem Quadrat A zu einem anderen B hingehen soll, dann ist die zu ausführenden Actions hier gespeichert.
	 */
	private LinkedList<HunterAction> bufferActions = new LinkedList<>();

	public HunterWorld() {
		view = new ArrayList<ArrayList<CellInfo>>();
	}

	/**Hier wird nur:
	 * Wenn action == TURN_LEFT oder TURN_RIGHT, die hunterDirection aktualisiert.
	 * Wenn action == GO_FORWARD, die hunterPosition aktualisiert.
	 * 
	 * Hier wird nicht bestimmt welche Aktion als nächstes ausgeführt wird,
	 * das wird in der updateState Methode gemacht.
	 * 
	 * @param previousAction : entpricht letzte Action, die auf dem Server ausgeführt wurde.
	 */
	public void updateHunterPosition(HunterAction previousAction) {
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
	public void updateState(HunterPercept percept, HunterActionEffect actionEffect) {
		/**
		 * Je nach Sichtbarkeit & Schwierigkeitsgrad (laut Serverkonfiguration) aktuelle
		 * Wahrnehmung des Hunters. Beim Wumpus erhalten Sie je nach Level mehr oder
		 * weniger Mapinformationen.
		 */
		this.percept = percept;

		// Aktuelle Reaktion des Server auf die letzte Übermittelte Action.

		// Alle möglichen Serverrückmeldungen:
		if (actionEffect == HunterActionEffect.GAME_INITIALIZED) {
			// Erster Aufruf
			this.updateCell(CellType.EMPTY);
		}

		if (actionEffect == HunterActionEffect.GAME_OVER) {
			// Das Spiel ist verloren
			this.nextAction = HunterAction.QUIT_GAME;
		}

		if (actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
			this.updateCell(null);
			// TODO nextAction des Hunter anpassen, bestimmen, wo ist die Wand.
			// wenn percet.isBump() == true ist. dann bestimmen, in welcher Nachbarzelle  eine die Wand ist.
		}

		if (actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
			// Nur bei Multiplayermodus
			// Letzte Bewegungsaktion war ein Zusammenstoß einem weiteren Hunter
		}

		if (actionEffect == HunterActionEffect.GOLD_FOUND) {
			// Nach HunterAction.GRAB wurde das Gold gefunden.
			this.updateCell(CellType.EMPTY);
			this.goldPosition.set(-1, -1);
		}

		if (actionEffect == HunterActionEffect.WUMPUS_KILLED) {
			// TODO Wumpus postion in der Welt aktualisieren, ähnlich wie oben
			this.wumpusAlive = false;

		}

		if (actionEffect == HunterActionEffect.NO_MORE_SHOOTS) {
			// TODO A* bis zur Position (1, 1) ( einfach schon implementierte Suchstrategie anwenden)
		}

		/*
		 * Mögliche Percepts Über die Welt erhält der Wumpushunter:
		 * 
		 * percept.isBreeze(); percept.isStench(); percept.isGlitter();
		 * percept.isRumble(); percept.isScream(); percept.isBump();
		 * percept.getWumpusStenchRadar()
		 */

		if (actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
			// Letzte Bewegungsaktion war gültig
			if (this.previousAction == HunterAction.GO_FORWARD) {
				if( percept.isBreeze() || percept.isStench() || percept.isGlitter() ) {
					
					// TODO: falls beide Bedingung true sind, dann zwei typ in der Cell speichern.
					if (percept.isBreeze()) {
						this.updateCell(CellType.BREEZE);
					}
					if (percept.isStench()) {
						this.updateCell(CellType.STENCH);
					}
					if (percept.isGlitter()) {
						this.updateCell(CellType.GOLD);
						this.bufferActions.push(HunterAction.GRAB);
					}
				}  else {
					this.updateCell(CellType.EMPTY);
				}			
			}
		}
		
		if(bufferActions.isEmpty()) {
			this.determinateNextActions();
//			System.out.println("Buffer Actions List:");
//			this.bufferActions.forEach(System.out::println);
		} 
//		System.out.println("");
//		this.print();
		this.nextAction = this.bufferActions.remove();
		
		// TODO Wumpus töten.
		// Also 1* Erkennen mit höheren Wahrscheinlichkeit, wo der Wumpus ist.
		//      2* Actions festlegen um ihn zu töten.
		
		/*
		 * percept.getWumpusStenchRadar() enthält alle Wumpi in max. R(ie)eichweite in
		 * einer Hashtable. Jeder Wumpi besitzt eine unique WumpusID (getKey). Die
		 * Manhattendistanz zum jeweiligen Wumpi ergibt sich aus der Gestanksitensität
		 * (getValue).
		 */

		// Beispiel:
		stenchRadar = this.percept.getWumpusStenchRadar();

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
	 * Gibt die nächste Action, die vom Hunter augeführt werden soll.
	 */
	public HunterAction getNextAction() {
		return this.nextAction;
	}

	/**
	 * Gibt das Element von der gewünschte Postion, sonst null wenn das Element nicht existiert.
	 */
	public CellInfo get(int x, int y) {
		if (x < 0 || y < 0) {
			return null;
		}

		try {
			ArrayList<CellInfo> row = this.view.get(y);
			CellInfo cellInfo = row.get(x);
			return cellInfo;

		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}


	/**
	 * Fügt ein Element ein Element an der gewünschte Postion ein, oder aktualisiert ihn, wenn er schon
	 * vorhanden ist
	 *  
	 * @return das vorherige Element, wenn ein Element aktualisiert wurde oder null falls kein Element aktualisiert wurde. 
	 */
	public CellInfo set(int x, int y, CellInfo newCellInfo) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("SET: Unzulässige Koordinaten " + "(" + x + ", " + y + ")" + "\n"
					+ "x und y Koodirnaten müssen größer gleich 0 sein.");
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

	public Direction getHunterDirection() {
		return hunterDirection;
	}

	/**
	 * Aktulisiert eine Zelle, an der Stelle, wo der Hunter liegt, auf Basis von der Information,
	 * die vom Server bekommen wurden.
	 * 
	 * @param cellInfo
	 */
	public void updateCell(CellType cellType) {
		if( cellType == CellType.GOLD) {
			this.goldPosition.set(this.hunterPosition.getX(), this.hunterPosition.getY());
		}
		
		CellInfo previousCellInfo = this.get(this.hunterPosition.getX(), this.hunterPosition.getY());

		this.set(this.hunterPosition.getX(), this.hunterPosition.getY(), new CellInfo(cellType));
		
		//  prüfe auf null wegen erster Aufruf( game initialized )
		//  prüfe auf CellType.TARGET :: Methode wird aufgerufen, nur wenn der Hunter eine ganz neue Zelle entdeckt.
		if( previousCellInfo == null || previousCellInfo.getType() == CellType.TARGET ) {
			this.setProbabilityAllAroundCell(cellType);
		}
	}
	
	public ArrayList<ArrayList<CellInfo>> getView(){
		return this.view;
	}
	
	public Point getHunterPosition() {
		return this.hunterPosition;
	}
	
	/**
	 * Aktulisiert bzw. setzt passende CellInfo von Typ Wall rund um den Aktuelle Position der Hunter.
	 * 
	 * @param cellType
	 */
	private void setProbabilityAllAroundCell(CellType cellType) {
		// WEST
		{
			int x = this.hunterPosition.getX() - 1;
			int y = this.hunterPosition.getY();
			if (x > 0) {
				CellInfo targetWall = this.get(x, y);
				if (targetWall != null) {
					if (cellType == CellType.BREEZE) {
						targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
					} else if (cellType == CellType.STENCH) {
						targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
					} else if (cellType == CellType.EMPTY) {
						targetWall.setProbabilityPit(0.0);
						targetWall.setProbabilityWumpus(0.0);
					}
				} else {
					if (cellType == CellType.BREEZE) {
						this.set(x, y, new CellInfo(x, y, 50.0, 0.0));
					} else if (cellType == CellType.STENCH) {
						this.set(x, y, new CellInfo(x, y, 0.0, 50.0));
					} else if (cellType == CellType.EMPTY) {
						this.set(x, y, new CellInfo(x, y, 0.0, 0.0));
					}
				}
			}
		}

		// NORTH
		{
			int x = this.hunterPosition.getX();
			int y = this.hunterPosition.getY() - 1;
			if (y > 0) {
				CellInfo targetWall = this.get(x, y);
				if (targetWall != null) {
					if (cellType == CellType.BREEZE) {
						targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
					} else if (cellType == CellType.STENCH) {
						targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
					} else if (cellType == CellType.EMPTY) {
						targetWall.setProbabilityPit(0.0);
						targetWall.setProbabilityWumpus(0.0);
					}

				} else {
					if (cellType == CellType.BREEZE) {
						this.set(x, y, new CellInfo(x, y, 50.0, 0.0));
					} else if (cellType == CellType.STENCH) {
						this.set(x, y, new CellInfo(x, y, 0.0, 50.0));
					} else if (cellType == CellType.EMPTY) {
						this.set(x, y, new CellInfo(x, y, 0.0, 0.0));
					}
				}
			}
		}

		// EAST
		{
			int x = this.hunterPosition.getX() + 1;
			int y = this.hunterPosition.getY();
			CellInfo targetWall = this.get(x, y);
			if (targetWall != null) {
				if (cellType == CellType.BREEZE) {
					targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
				} else if (cellType == CellType.STENCH) {
					targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
				} else if (cellType == CellType.EMPTY) {
					targetWall.setProbabilityPit(0.0);
					targetWall.setProbabilityWumpus(0.0);
				}

			} else {
				if (cellType == CellType.BREEZE) {
					this.set(x, y, new CellInfo(x, y, 50.0, 0.0));
				} else if (cellType == CellType.STENCH) {
					this.set(x, y, new CellInfo(x, y, 0.0, 50.0));
				} else if (cellType == CellType.EMPTY) {
					this.set(x, y, new CellInfo(x, y, 0.0, 0.0));
				}
			}
		}

		// SOUTH
		{
			int x = this.hunterPosition.getX();
			int y = this.hunterPosition.getY() + 1;
			CellInfo targetWall = this.get(x, y);
			if (targetWall != null) {
				if (cellType == CellType.BREEZE) {
					targetWall.setProbabilityPit(targetWall.getProbabilityPit() + 60.0);
				} else if (cellType == CellType.STENCH) {
					targetWall.setProbabilityWumpus(targetWall.getProbabilityWumpus() + 60.0);
				} else if (cellType == CellType.EMPTY) {
					targetWall.setProbabilityPit(0.0);
					targetWall.setProbabilityWumpus(0.0);
				}

			} else {
				if (cellType == CellType.BREEZE) {
					this.set(x, y, new CellInfo(x, y, 50.0, 0.0));
				} else if (cellType == CellType.STENCH) {
					this.set(x, y, new CellInfo(x, y, 0.0, 50.0));
				} else if (cellType == CellType.EMPTY) {
					this.set(x, y, new CellInfo(x, y, 0.0, 0.0));
				}
			}
		}
		
		/**
		 * Nach alle Anderung hier wird die wallList noch sortiert damit die am wenigsten gefährliche "Wall"
		 * am Anfang der Liste stehen.
		 */
		CellInfo.sortWallList();
	}
	
	public void determinateNextActions() {
		Point zielPosition = CellInfo.getWallList().remove().getPosition();
		this.get(zielPosition.getX(), zielPosition.getY()).setType(CellType.TARGET);
		Suche suche = new Breitensuche(this, zielPosition);
		this.bufferActions = suche.start();
	}
}
