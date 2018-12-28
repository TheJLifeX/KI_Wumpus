package de.fh.blanks;

/**
 * A Point represents a 2D-Coordinate (x, y).
 */
public class Point
{
    private int x;
    private int y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void set(Point p)
    {
        this.x = p.x;
        this.y = p.y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;

        if (!(o instanceof Point))
            return false;

        Point other = (Point)o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}
