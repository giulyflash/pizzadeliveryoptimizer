package graphsearch;

import javax.swing.JProgressBar;

import datastructures.DS_House;
import datastructures.DS_Path;
import errors.EX_Exception;
import errors.EX_Exception.ErrorType;
import filesystem.PA_FileSystem;

public class GS_Dijkstra
{

	// Local copy of the file system object to use during the graph searching process
	private PA_FileSystem filesystem;

	// Local copy of the progress bar to inform the user on the graph searching process
	private JProgressBar progressbar;

	/**
	 * Dijkstra graph searching class constructor
	 * @param fs to use with this object
	 * @param progressbar object to report the progress of current operations
	 */
    public GS_Dijkstra(PA_FileSystem fs, JProgressBar progressbar)
    {
    	this.progressbar = progressbar;
        this.filesystem = fs;
    }

    /**
     * Set the progress bar's value to 1 more than it is currently at
     */
    private void progressStep()
    {
    	this.progressbar.setValue(this.progressbar.getValue() + 1);
    }

    /**
     * Start the graph searching process and link up the two requested houses
     * @param start house
     * @param end house
     * @throws EX_Exception
     */
    public void search(DS_House start, DS_House end) throws EX_Exception
    {
    	this.progressbar.setMaximum(this.filesystem.getNumHouses());
    	this.progressbar.setValue(0);

    	this.clear();
    	start.setDistance(0);
    	this.dijkstra(start);

    	if(end.getShortest() == null)
    		throw new EX_Exception(1, ErrorType.ERROR_GRAPHSEARCHING);

    	DS_House house = end;
		while(house != start) {
			house.getShortest().setIsShortest(true);
			house = house.getShortest().getStart();
		}
		this.progressStep();
    }

    /**
     * Clear up all data in the paths and houses of the file system
     */
    public void clear()
    {
    	for(DS_House house : this.filesystem.getHouseList())
    		house.clear();
    	for(DS_Path path : this.filesystem.getPathList())
    		path.clear();
    }

    /**
     * Dijkstra's graph searching procedure (recursive method)
     * @param current house to process
     * @throws EX_Exception
     */
    private void dijkstra(DS_House current) throws EX_Exception
    {
    	try {
    		for(DS_Path Path : current.getPaths()) {
    			if(Path.getEnd().getDistance() == -1 || current.getDistance() + Path.getWeight() < Path.getEnd().getDistance()) {
    				Path.getEnd().setDistance(current.getDistance() + Path.getWeight());
    				Path.getEnd().setShortest(Path);
    			}
	        }
	        current.setVisited(true);
	        for(DS_Path Path : current.getPaths())
	        	if(Path.getEnd().isVisited() == false)
	        			this.dijkstra(Path.getEnd());
			this.progressStep();
    	} catch(Exception exception) {
    		throw new EX_Exception(exception.getMessage(), ErrorType.ERROR_GRAPHSEARCHING);
    	}
    }

}
