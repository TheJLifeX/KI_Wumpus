package de.fh.blanks;

enum Type
{
    WUMPUS,
    STENCH,
    GLITTER,
    BREEZE,
    BUMP,
    PIT,
    SCREAM
}

public class Info
{
    int row, col;
    int probability;
    Type type;

    public Info(int row, int col, int probability, Type type)
    {
        this.row = row;
        this.col = col;
        this.probability = probability;
        this.type = type;
    }
}
