package filesystem;

import java.io.File;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import datastructures.DS_GenericList;
import datastructures.DS_House;
import datastructures.DS_Path;
import errors.EX_Exception;
import errors.EX_Exception.ErrorType;

public class PA_FileSystem
{

	// House file system object
	private FS_House fshouse;

	// Path file system object
	private FS_Path fspath;

	/**
	 * File system constructor (initialise the entire data structure initialisation and setup the file system link
	 * @param create decides whether to load or create
	 * @throws PRP_Exception
	 */
	public PA_FileSystem(boolean create) throws EX_Exception
	{
		File[] files = this.getMapFiles(create);
		if(files != null) {
			this.fshouse = new FS_House(files[0], create);
			this.fspath = new FS_Path(files[1], this.fshouse, create);
			this.fshouse.setFSPath(this.fspath);
		} else {
			throw new EX_Exception(1, ErrorType.ERROR_FILESYSTEM);
		}
	}

	/**
	 * Get the user to choose the map files
	 * @param create decides whether to load or create
	 * @return an array of two or three file objects containing (house, path)
	 * @throws PRP_Exception
	 */
	public File[] getMapFiles(boolean create) throws EX_Exception
	{
		JFileChooser jc = new JFileChooser();
		jc.setFileFilter(new FileNameExtensionFilter("Map Component Files", "mph", "mpp"));

		File[] result = new File[2];
		int retval = jc.showOpenDialog(null);
		if(retval == JFileChooser.APPROVE_OPTION) {
			File file = jc.getSelectedFile();
			if(create == true)
				file = new File(file.getAbsolutePath() + ".mph");
			if(FS_Utils.getExtension(file).equals("mph")) {
				result[0] = file;
				result[1] = new File(FS_Utils.getFilename(file) + ".mpp");
			} else if(FS_Utils.getExtension(file).equals("mpp")) {
				result[0] = new File(FS_Utils.getFilename(file) + ".mph");
				result[1] = file;
			}
		} else {
			return null;
		}

		return result;
	}

	/**
	 * Add a house to the house file system
	 * @param house object to add
	 * @throws PRP_Exception
	 */
	public void addHouse(DS_House house) throws EX_Exception
	{
		this.fshouse.add(house);
	}

	/**
	 * Add a path to the path file system
	 * @param path object to add
	 * @return true if duplicate, false otherwise
	 * @throws PRP_Exception
	 */
	public boolean addPath(DS_Path path) throws EX_Exception
	{
		return this.fspath.add(path);
	}

	/**
	 * Get the list of houses associated to this file system instance
	 * @return the linked list with the house data
	 */
	public DS_GenericList<DS_House> getHouseList()
	{
		return this.fshouse.getContentList();
	}

	/**
	 * Get the list of paths associated to this file system instance
	 * @return the linked list with the path data
	 */
	public DS_GenericList<DS_Path> getPathList()
	{
		return this.fspath.getContentList();
	}

	/**
	 * Remove a house from the file system
	 * @param houseID of the house to remove
	 * @return true on success, otherwise false
	 * @throws PRP_Exception
	 */
	public boolean removeHouse(int houseID) throws EX_Exception
	{
		return this.fshouse.remove(this.fshouse.getHouseFromID(houseID));
	}

	/**
	 * Remove a path from the file system
	 * @param pathID of the path to remove
	 * @return true on success, otherwise false
	 * @throws PRP_Exception
	 */
	public boolean removePath(int pathID) throws EX_Exception
	{
		return this.fspath.remove(this.fspath.getPathFromID(pathID));
	}

	/**
	 * Get the local House object from its ID
	 * @param id to look for
	 * @return associated House object
	 */
	public DS_House getHouseByID(int id)
	{
		return this.fshouse.getHouseFromID(id);
	}

	/**
	 * Get the statistics associated to this file system instance (number of houses, number of paths)
	 * @return an array of two integers: number of houses, number of paths respectively
	 */
	public int[] getStats()
	{
		int status[] = { this.fshouse.getContentList().size(), this.fspath.getContentList().size() };
		return status;
	}

	/**
	 * Get the number of valid houses in the content list
	 * @return the number of valid houses
	 */
	public int getNumHouses()
	{
		return this.fshouse.getTotalSpaces();
	}

	/**
	 * Overwrite an existing path with new house data
	 * @param id where the new data is to be written
	 * @param house data to overwrite with
	 * @return true on success, false otherwise
	 * @throws PRP_Exception
	 */
	public boolean overwriteHouse(int id, DS_House house) throws EX_Exception
	{
		return this.fshouse.overwriteHouse(id, house);
	}

	/**
	 * Overwrite an existing path with new path data
	 * @param id where the new data is to be written
	 * @param path data to overwrite with
	 * @return true on success, false otherwise
	 * @throws PRP_Exception
	 */
	public boolean overwritePath(int id, DS_Path path) throws EX_Exception
	{
		return this.fspath.overwritePath(id, path);
	}

	/**
	 * Set the appropriate paths' weights to a new value
	 * @param id of the paths to edit
	 * @param weight to change to
	 * @throws PRP_Exception
	 */
	public void setPathWeight(int id, int weight) throws EX_Exception
	{
		try {
			Iterator<DS_Path> itr = this.fspath.getContentList().iterator();
			while(itr.hasNext()) {
				DS_Path path = itr.next();
				if(path.getID() == id) {
					path.setWeight(weight);
					this.overwritePath(id, path);
					break;
				}
			}
		} catch(Exception e) {
			throw new EX_Exception("Failed to change the requested paths' timings...", ErrorType.ERROR_FILESYSTEM, e);
		}
	}

}
