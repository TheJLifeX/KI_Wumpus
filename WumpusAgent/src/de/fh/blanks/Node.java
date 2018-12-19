package de.fh.blanks;

import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import java.util.concurrent.LinkedBlockingQueue;

public class Node
{
    /**
     * Parent-Node
     */
	private Node parent;

    /**
     * Path-Cost to this Node.
     */
	private float pathCost;

    /**
     * Estimated Cost from this Node to the Goal-Node.
     * Could be updated, since Goal-Node changes while moving through the world.
     */
	private float heuristic;

    /**
     * Internal World-Model of the Hunter. Stores relevant information for the Hunter to consider.
     */
	private HunterWorld hunterWorld;

    /**
     * Will store sequence of HunterActions to get to this Node from the Start-Node.
     */
	private LinkedBlockingQueue<HunterAction> hunterActionList = new LinkedBlockingQueue<>();

    /**
     * Constructor for Root-Node.
     */
	public Node()
    {
        parent = null;
    }

    /**
     * Constructor for an ordinary Node.
     * Assign parent.
     * Copy parent's HunterWorld.
     * Update HunterWorld of this Node with given HunterPercept and HunterAction.
     * @param parent Parent-Node of this Node.
     */
    public Node(Node parent, HunterPercept percept, HunterAction action)
    {
        this.parent = parent;
        hunterWorld = parent.getHunterWorld().clone();
        hunterWorld.processPercept(percept);
        hunterWorld.processAction(action);
    }

    /**
     * Finales Ziel ist "HunterPosition = { 1, 1 } && hasGold() == true"
     * Zwischenziele sind immer eine Position, oder auch andere Bedingungen wie "numArrows = 3" ? Predicate könnten dann nützlich sein. Erstmal Position.
     * @return
     */
    public boolean isGoal(Point goalPosition)
    {
        return hunterWorld.getHunterPosition().equals(goalPosition);
    }



    @Override
    public String toString()
    {
        // TODO
        return "";
    }

    /**
     * Returns HashCode of this Node.
     * War ich mir nicht sicher, wie ich den berechnen soll.
     * @return HashCode of this Node.
     */
    @Override
    public int hashCode()
    {
     return (pathCost + " " + heuristic + " " + hunterWorld.getHunterPosition().toString()).hashCode();
    }

    /* Getter and Setter from this Point on ... */
    public Node getParent()
    {
        return parent;
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    public float getPathCost()
    {
        return pathCost;
    }

    public void setPathCost(float pathCost)
    {
        this.pathCost = pathCost;
    }

    public float getHeuristic()
    {
        return heuristic;
    }

    public void setHeuristic(float heuristic)
    {
        this.heuristic = heuristic;
    }

    /**
     *
     * @return PathCost + Heuristic
     */
    public float getEvaluation()
    {
        return pathCost + heuristic;
    }

    public HunterWorld getHunterWorld()
    {
        return hunterWorld;
    }
}