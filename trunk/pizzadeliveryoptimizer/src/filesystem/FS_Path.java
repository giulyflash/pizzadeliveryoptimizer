package filesystem;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import datastructures.DS_GenericList;
import datastructures.DS_House;
import datastructures.DS_Path;
import errors.EX_Exception;
import errors.EX_Exception.ErrorType;

public class FS_Path extends FS_File<DS_Path>
{

	// House file system associated with this path file system
	private FS_House fshouse = null;

	/**
	 * Path file system constructor
	 * @param file to use
	 * @param fshouse that contains all the houses
	 * @param create decides whether to authorise FileNotFound exceptions
	 * @throws PRP_Exception
	 */
	public FS_Path(File file, FS_House fshouse, boolean create) throws EX_Exception
	{
		this.content = new DS_GenericList<DS_Path>();
		this.fshouse = fshouse;
		this.file = file;
		if(create == false) this.read();
	}

	/**
	 * Read the path file's data into the list
	 * @param fshouse that contains all the houses
	 * @throws PRP_Exception
	 */
	protected void read() throws EX_Exception
	{
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(this.file));
			int nrec = (int)(this.file.length() / DS_Path.RECORD_SIZE);
			for(int i = 0; i < nrec; i++) {
				boolean deleted = dis.readBoolean();
				int startid = dis.readInt();
				int endid = dis.readInt();
				int weight = dis.readInt();
				if(deleted == false) {
					DS_House start = this.fshouse.getHouseFromID(startid);
					if(start == null) throw new EX_Exception("Inconsisted path data (id:" + i + ") - starting house unavailable", ErrorType.ERROR_FILESYSTEM);
					DS_House end = this.fshouse.getHouseFromID(endid);
					if(end == null) throw new EX_Exception("Inconsisted path data (id:" + i + ") - ending house unavailable", ErrorType.ERROR_FILESYSTEM);
					DS_Path path = new DS_Path(start, end, weight);
					start.addPath(path);
					path.setID(i);
					this.content.add(path);
				}
			}
		} catch(FileNotFoundException e) {
			throw new EX_Exception("Missing file: " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(IOException e) {
			throw new EX_Exception("I/O error whilst reading " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(Exception e) {
			throw new EX_Exception("Unknown error whilst reading " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} finally {
			try {
				if(dis != null) dis.close();
			} catch (IOException e) {
				throw new EX_Exception("Couldn't close " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
			}
		}
	}

	/**
	 * Add a path record to the list and file
	 * @param object of type Path to add to the content list and file
	 * @return false on duplicate, otherwise true
	 */
	public boolean add(DS_Path object) throws EX_Exception
	{
		if(this.contains(object))
			return false;
		object.setID(this.setRecordDetails(this.getAvailableID(), object));
		this.getContentList().add(object);
		object.getStart().addPath(object);
		return true;
	}

	/**
	 * Check if the passed object already exists in the system
	 * @param object to look for
	 * @return true if duplicate, false otherwise
	 */
	protected boolean contains(DS_Path object)
	{
		for(DS_Path path : this.getContentList())
			if(path.getStart().getID() == object.getStart().getID() &&
					path.getEnd().getID() == object.getEnd().getID())
				return true;
		return false;
	}

	/**
	 * Remove a path from the list based on an ID
	 * @param object of type Path to remove from the content list and file
	 * @return true if success, false otherwise
	 * @throws PRP_Exception
	 */
	public boolean remove(DS_Path object) throws EX_Exception
	{
		return this.remove(object.getID());
	}

	/**
	 * Remove a path from the based on an ID
	 * @param id of the object to remove
	 * @return true if success, false otherwise
	 * @throws PRP_Exception
	 */
	private boolean remove(int id) throws EX_Exception
	{
		Iterator<DS_Path> itr = this.content.iterator();

		while(itr.hasNext()) {
			DS_Path path = itr.next();
			if(path.getID() == id) {
				this.setDeleted(id);
				itr.remove();
				return true;
			}
		}

		return false;
	}

	/**
	 * Set the deleted field of a record to true
	 * @param recID of the field to delete
	 * @throws PRP_Exception
	 */
	protected void setDeleted(int recID) throws EX_Exception
	{
		RandomAccessFile rafile = null;
		try {
			rafile = new RandomAccessFile(this.file, "rwd");
			if(this.isValidID(recID) == true) {
				rafile.seek(DS_Path.RECORD_SIZE * recID);
				rafile.writeBoolean(true);
			}
		} catch(FileNotFoundException e) {
			throw new EX_Exception("Missing file: " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(IOException e) {
			throw new EX_Exception("I/O exception whilst writing to " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} finally {
			try {
				if(rafile != null) rafile.close();
			} catch (IOException e) {
				throw new EX_Exception("Couldn't close " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
			}
		}
	}

	/**
	 * Get the next available ID (used in conjunction the add method)
	 * @return the next available ID as an integer (-1 if you need to append)
	 * @throws PRP_Exception
	 */
	protected int getAvailableID() throws EX_Exception
	{
		int nrec = (int)(this.file.length() / DS_Path.RECORD_SIZE);
		RandomAccessFile rafile = null;
		try {
			rafile = new RandomAccessFile(this.file, "rwd");
			for(int i = 0; i < nrec; i++) {
				rafile.seek(DS_Path.RECORD_SIZE * i);
				if(rafile.readBoolean() == true)
					return i;
			}
			return -1;
		} catch(FileNotFoundException e) {
			throw new EX_Exception("Missing file: " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(IOException e) {
			throw new EX_Exception("I/O exception whilst writing to " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} finally {
			try {
				if(rafile != null) rafile.close();
			} catch (IOException e) {
				throw new EX_Exception("Couldn't close " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
			}
		}
	}

	/**
	 * Set the record data at the desired position with the desired content
	 * @param position of the record to add (-1 to append to the end of file)
	 * @param object containing the data to set
	 * @return returns the position where it was added
	 * @throws PRP_Exception
	 */
	protected int setRecordDetails(int position, DS_Path object) throws EX_Exception
	{
		RandomAccessFile rafile = null;
		try {
			rafile = new RandomAccessFile(this.file, "rwd");
			if(position == -1) {
				rafile.seek(Math.max(0, this.file.length()));
				position = (int)(this.file.length() / DS_Path.RECORD_SIZE);
			} else {
				rafile.seek(DS_Path.RECORD_SIZE * position);
			}
			rafile.writeBoolean(false);
			rafile.writeInt(object.getStart().getID());
			rafile.writeInt(object.getEnd().getID());
			rafile.writeInt(object.getWeight());
			return position;
		} catch(FileNotFoundException e) {
			throw new EX_Exception("Missing file: " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(IOException e) {
			throw new EX_Exception("I/O exception whilst writing to " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} finally {
			try {
				if(rafile != null) rafile.close();
			} catch (IOException e) {
				throw new EX_Exception("Couldn't close " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
			}
		}
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
		if(this.isValidID(id) == false)
			return false;
		else
			this.setRecordDetails(id, path);
		return true;
	}

	/**
	 * Determine if the requested ID is valid (exists in the file)
	 * @param id to test
	 * @return true if yes, false otherwise
	 */
	public boolean isValidID(int id)
	{
		return (this.getTotalSpaces() <= id) ? false : true;
	}

	/**
	 * Get a Path object from an ID
	 * @param id to look for
	 * @return The desired Path object, otherwise null
	 */
	public DS_Path getPathFromID(int id)
	{
		Iterator<DS_Path> itr = this.content.iterator();
		while(itr.hasNext()) {
			DS_Path path = itr.next();
			if(path.getID() == id)
				return path;
		}
		return null;
	}

	/**
	 * Get the total number of spaces in the file (therefore max number of IDs);
	 * @return the total spaces (max ID)
	 */
	public int getTotalSpaces()
	{
		return ((int)(this.file.length() / DS_Path.RECORD_SIZE));
	}

}
