package filesystem;

import java.io.File;

import datastructures.DS_GenericList;
import errors.EX_Exception;

public abstract class FS_File<K>
{

	// File associated with the instance
	protected File file;

	// List of the file's content
	protected DS_GenericList<K> content;

	/**
	 * Read the data of the file into the content list
	 * @throws PRP_Exception
	 */
	protected abstract void read() throws EX_Exception;

	/**
	 * Add an object to the content list and the file
	 * @param object to add
	 * @return true on success, false otherwise (false on duplicate)
	 * @throws PRP_Exception
	 */
	public abstract boolean add(K object) throws EX_Exception;

	/**
	 * Remove and object from the file and the content list
	 * @param object to remove
	 * @return true if it was successfully added, otherwise false
	 * @throws PRP_Exception
	 */
	public abstract boolean remove(K object) throws EX_Exception;

	/**
	 * Get the next available ID (used in conjunction the add method)
	 * @return the next available ID as an integer
	 */
	protected abstract int getAvailableID() throws EX_Exception;

	/**
	 * Set the deleted field of a record to true
	 * @param recID of the field to delete
	 * @throws PRP_Exception
	 */
	protected abstract void setDeleted(int recID) throws EX_Exception;

	/**
	 * Set the record data at the desired position with the desired content
	 * @param position of the record to add (-1 to append to the end of file)
	 * @param object containing the data to set
	 * @return returns the position where it was added
	 * @throws PRP_Exception
	 */
	protected abstract int setRecordDetails(int position, K object) throws EX_Exception;

	/**
	 * Get the list object with the content of this file system object
	 * @return a list with the objects in it
	 */
	public DS_GenericList<K> getContentList() { return this.content; }

	/**
	 * Determine if the requested ID is valid (exists in the file)
	 * @param id to test
	 * @return true if yes, false otherwise
	 */
	public abstract boolean isValidID(int id);

	/**
	 * Get the total number of spaces in the file (therefore max number of IDs);
	 * @return the total spaces (max ID)
	 */
	public abstract int getTotalSpaces();

	/**
	 * Check if the passed object already exists in the system
	 * @param object to look for
	 * @return true if duplicate, false otherwise
	 */
	protected abstract boolean contains(K object);

}
