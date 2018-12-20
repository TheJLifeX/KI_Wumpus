package de.fh.blanks;

import java.util.LinkedList;

import de.fh.suche.Suche;
import de.fh.uninformedSearch.Breitensuche;
import de.fh.wumpus.enums.HunterAction;

public class HunterWorldTest {

	public static void main(String[] args) {
		
		HunterWorld world = new HunterWorld();
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

//		for (int row = 0; row < 4; row++) {
//			for (int col = 0; col < 4; col++) {
//				if (row % 2 == 0)
//					world.set(row, col, new CellInfo(CellType.BREEZE, 0.5));
//				else
//					world.set(row, col, new CellInfo(CellType.STENCH, 0.3));
//			}
//		}
		world.print(); 
		
		
		Point zielPosition = new Point(4, 4);
		Suche suche = new Breitensuche(world, zielPosition);
		LinkedList<HunterAction> hunterActionList = suche.start();
		
		if(hunterActionList != null) {
			System.out.println("Hunter Action List: ");
			while(!hunterActionList.isEmpty()) {
				System.out.println("-> " + hunterActionList.remove().toString());
			}
		}
		
	}
}
