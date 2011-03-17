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

public class FS_House extends FS_File<DS_House>
{

	// Paths file system object
	private FS_Path fspath;

	/**
	 * House file system handler constructor
	 * @param file to use
	 * @param create decides whether to authorise FileNotFound exceptions
	 * @throws PRP_Exception
	 */
	public FS_House(File file, boolean create) throws EX_Exception
	{
		try {
			this.content = new DS_GenericList<DS_House>();
			this.file = file;
			if(create == false) this.read();
		} catch(EX_Exception e) {
			if(!(e.getExtendedException() instanceof FileNotFoundException) && create == false)
				throw e;
		}
	}

	/**
	 * Read the house file's data into the list
	 * @throws PRP_Exception
	 */
	protected void read() throws EX_Exception
	{
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(this.file));
			int nrec = this.getTotalSpaces();
			for(int i = 0; i < nrec; i++) {
				boolean deleted = dis.readBoolean();
				byte[] namedata = new byte[24];
				dis.readFully(namedata);
				String name = new String(namedata).trim();
				if(deleted == false) {
					DS_House house = new DS_House(name, dis.readInt(), dis.readInt());
					house.setID(i);
					this.content.add(house);
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
	 * Add a House record to the list and file
	 * @param object of type House to add to the content list and file
	 * @return false on duplicate, true otherwise
	 * @throws PRP_Exception
	 */
	public boolean add(DS_House object) throws EX_Exception
	{
		if(this.contains(object)) return false;
		object.setID(this.setRecordDetails(this.getAvailableID(), object));
		this.content.add(object);
		return true;
	}

	/**
	 * Remove a house from the list based on an ID
	 * @param object of type House to remove from the content list and file
	 * @return true if the object is found, false otherwise
	 * @throws PRP_Exception
	 */
	public boolean remove(DS_House object) throws EX_Exception
	{
		Iterator<DS_House> itr = this.content.iterator();

		while(itr.hasNext()) {
			DS_House house = itr.next();
			if(house.getID() == object.getID()) {
				for(DS_Path path : this.fspath.getContentList())
					if(path.getStart().getID() == object.getID() || path.getEnd().getID() == object.getID())
						this.fspath.remove(path);
				this.setDeleted(object.getID());
				itr.remove();
				return true;
			}
		}

		return false;
	}

	/**
	 * Set the path file system object associated with this house object
	 * @param fspath to attach
	 */
	public void setFSPath(FS_Path fspath)
	{
		this.fspath = fspath;
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
				rafile.seek(DS_House.RECORD_SIZE * recID);
				rafile.writeBoolean(true);
			}
		} catch(FileNotFoundException e) {
			throw new EX_Exception("Missing file: " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(IOException e) {
			throw new EX_Exception("I/O exception whilst writing to " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(Exception e) {
			throw new EX_Exception("Unknown error whilst reading " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
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
		int nrec = (int)(this.file.length() / DS_House.RECORD_SIZE);
		RandomAccessFile rafile = null;
		try {
			rafile = new RandomAccessFile(this.file, "rwd");
			for(int i = 0; i < nrec; i++) {
				rafile.seek(DS_House.RECORD_SIZE * i);
				if(rafile.readBoolean() == true)
					return i;
			}
			return -1;
		} catch(FileNotFoundException e) {
			throw new EX_Exception("Missing file: " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(IOException e) {
			throw new EX_Exception("I/O exception whilst writing to " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(Exception e) {
			throw new EX_Exception("Unknown error whilst reading " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
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
	protected int setRecordDetails(int position, DS_House object) throws EX_Exception
	{
		RandomAccessFile rafile = null;
		try {
			rafile = new RandomAccessFile(this.file, "rwd");
			String name = object.getName().substring(0, Math.min(object.getName().length(), 24));
			name = FS_Utils.PadString(name, 24);
			if(position == -1) {
				rafile.seek(Math.max(0, this.file.length()));
				position = (int)(this.file.length() / DS_House.RECORD_SIZE);
			} else {
				rafile.seek(DS_House.RECORD_SIZE * position);
			}
			rafile.writeBoolean(false);
			rafile.writeBytes(name);
			rafile.writeInt(object.getX());
			rafile.writeInt(object.getY());
			return position;
		} catch(FileNotFoundException e) {
			throw new EX_Exception("Missing file: " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(IOException e) {
			throw new EX_Exception("I/O exception whilst writing to " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} catch(Exception e) {
			throw new EX_Exception("Unknown error whilst reading " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
		} finally {
			try {
				if(rafile != null) rafile.close();
			} catch (IOException e) {
				throw new EX_Exception("Couldn't close " + this.file.getAbsolutePath(), ErrorType.ERROR_FILESYSTEM, e);
			}
		}
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
		if(this.isValidID(id) == false)
			return false;
		this.setRecordDetails(id, house);
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
	 * Get a House object from an ID
	 * @param id to look for
	 * @return The desired House object, otherwise null
	 */
	public DS_House getHouseFromID(int id)
	{
		Iterator<DS_House> itr = this.content.iterator();
		while(itr.hasNext()) {
			DS_House house = itr.next();
			if(house.getID() == id)
				return house;
		}
		return null;
	}

	/**
	 * Get the total number of spaces in the file (therefore max number of IDs);
	 * @return the total spaces (max ID)
	 */
	public int getTotalSpaces()
	{
		return ((int)(this.file.length() / DS_House.RECORD_SIZE));
	}

	/**
	 * Check if the passed object already exists in the system
	 * @param object to look for
	 * @return true if duplicate, false otherwise
	 */
	protected boolean contains(DS_House object)
	{
		for(DS_House house : this.getContentList())
			if(house.getID() == object.getID())
				return true;
		return false;
	}

}
