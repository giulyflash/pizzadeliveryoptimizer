package datastructures;

import java.io.Serializable;

public class DS_Path implements Serializable
{

	// Serialisation ID required by the JGraphX package
	private static final long serialVersionUID = 5044317856854020624L;

	// Size of the House object on disk (add 1 extra byte for the deleted field)
	public static final int RECORD_SIZE = 4 + 4 + 4 + 1;

    // Weight of this path
    private int weight;

    // ID associated with this path (memory only, in file it's the relative offset to record)
    private int id;

    // Is this path a part of the shortest path?
    private boolean isShortest;

    // Starting house
    private final DS_House v_start;

	// Ending house
	private final DS_House v_end;

	/**
	 * Main constructor
	 * @param s starting house
	 * @param e ending house
	 * @param weight time taken to cross the path
	 */
    public DS_Path(DS_House s, DS_House e, int weight)
    {
    	this.isShortest = false;
        this.v_start = s;
        this.v_end = e;
        this.weight = weight;
        this.id = -1;
    }

    /**
     * Get this path's starting house
     * @return the start house
     */
	public DS_House getStart()
	{
		return this.v_start;
	}

	/**
	 * Get this path's ending house
	 * @return the end house
	 */
    public DS_House getEnd()
    {
        return this.v_end;
    }

    /**
     * Get the time taken to cross the path
     * @return time taken to cross path
     */
    public int getWeight()
    {
        return this.weight;
    }

    /**
     * Set the time taken to cross the path
     * @param weight/time associated to the path
     */
    public void setWeight(int weight)
    {
    	this.weight = weight;
    }

    /**
     * Get the ID associated with this path
     * @return the ID as an integer
     */
    public int getID()
    {
    	return this.id;
    }

    /**
     * Set the ID of this path (this can only be set once!)
     * @param id to set the path to
     */
    public void setID(int id)
    {
    	if(this.id < 0) this.id = id;
    }

    /**
     * Set whether or not this path is part of the shortest one
     * @param isShortest status
     */
    public void setIsShortest(boolean isShortest)
    {
    	this.isShortest = isShortest;
    }

    /**
     * Determine if this path is part of the shortest one
     * @return whether or not it is
     */
    public boolean isShortest()
    {
    	return this.isShortest;
    }

    /**
     * Clear the content of this Path
     */
    public void clear()
    {
    	this.setIsShortest(false);
    }

    /**
     * Over-ride the toString() method to return the weight
     * @return the string representation of the weight
     */
    public String toString()
    {
    	return "" + this.weight;
    }

}
