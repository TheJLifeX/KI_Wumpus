package de.fh.blanks;

public class HunterWorldTest
{

	public static void main(String[] args) {
		HunterWorld<CellInfo> world = new HunterWorld<>();
		/*for(int i = 1; i < 4; i++) {
			world.add(i, 1, new CellInfo(CellType.BREEZE));
		}
		world.add(3, 2, new CellInfo(CellType.BREEZE));
		world.add(3, 3, new CellInfo(CellType.BREEZE));
		world.add(2, 3, new CellInfo(CellType.BREEZE));
		CellInfo previousCellInfo = world.set(2,3, new CellInfo(CellType.WUMPUS));
		world.set(2, 4, new CellInfo(CellType.BREEZE));
		world.set(3, 4, new CellInfo(CellType.BREEZE));
		world.set(4, 4, new CellInfo(CellType.BREEZE));
		world.set(0, 0, new CellInfo(CellType.HUNTER, 1));*/

		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
			{
				if (row % 2 == 0)
					world.add(row, col, new CellInfo(CellType.BREEZE, 0.5));
				else
					world.add(row, col, new CellInfo(CellType.STENCH, 0.3));
			}

		world.print();
	}
}
