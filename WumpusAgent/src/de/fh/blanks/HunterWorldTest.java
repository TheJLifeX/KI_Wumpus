package de.fh.blanks;

public class HunterWorldTest
{

	public static void main(String[] args) {
		HunterWorld<CellInfo> world = new HunterWorld<>();
		/*for(int i = 1; i < 4; i++) {
			world.add(i, 1, new CellInfo(InfoType.BREEZE));
		}
		world.add(3, 2, new CellInfo(InfoType.BREEZE));
		world.add(3, 3, new CellInfo(InfoType.BREEZE));
		world.add(2, 3, new CellInfo(InfoType.BREEZE));
		CellInfo previousCellInfo = world.set(2,3, new CellInfo(InfoType.WUMPUS));
		world.set(2, 4, new CellInfo(InfoType.BREEZE));
		world.set(3, 4, new CellInfo(InfoType.BREEZE));
		world.set(4, 4, new CellInfo(InfoType.BREEZE));
		world.set(0, 0, new CellInfo(InfoType.HUNTER, 1));*/

		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
			{
				if (row % 2 == 0)
					world.add(row, col, new CellInfo(InfoType.BREEZE, 0.5));
				else
					world.add(row, col, new CellInfo(InfoType.STENCH, 0.3));
			}
	}
}
