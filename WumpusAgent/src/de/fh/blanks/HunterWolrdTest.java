package de.fh.blanks;

public class HunterWolrdTest {

	public static void main(String[] args) {
		HunterWorld<CellInfo> world = new HunterWorld<>();
		for(int i = 1; i < 4; i++) {
			world.add(i, 1, new CellInfo(CellTyp.BREEZE));
		}
		world.add(3, 2, new CellInfo(CellTyp.BREEZE));
		world.add(3, 3, new CellInfo(CellTyp.BREEZE));
		world.add(2, 3, new CellInfo(CellTyp.BREEZE));
		CellInfo previousCellInfo = world.set(2,3, new CellInfo(CellTyp.WUMPUS));
		world.set(2, 4, new CellInfo(CellTyp.BREEZE));
		
		world.print();
		
		System.out.println("Previous CellInfo (2,3): " + previousCellInfo);
	}
}
