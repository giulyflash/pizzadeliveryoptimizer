package errors;

import java.awt.Component;

public class EX_Exception extends Exception
{

	// Serialisation ID (required when extending Exception)
	private static final long serialVersionUID = 1L;

	// Error types enumeration
	public static enum ErrorType { ERROR_REPORTER, ERROR_FILESYSTEM, ERROR_GRAPHSEARCHING, ERROR_GRAPHICALINTERFACE }

	// Exception's error type
	private final ErrorType et;

	// Parent component that initiated the exception
	private final Component parent;

	// Exception's internal error ID (if not message based)
	private final int id;

	// Extended information about this exception (exception that triggered the creation of this one)
	private final Exception extended;

	/**
	 * Generic exception constructor (used by all other public constructors)
	 * @param message describing the error
	 * @param id describing the exception
	 * @param et of the error (who caused it?)
	 * @param parent component that threw the exception
	 * @param extension to this error
	 */
	private EX_Exception(String message, int id, ErrorType et, Component parent, Exception extension)
	{
		super(message);
		this.extended = extension;
		this.parent = parent;
		this.id = id;
		this.et = et;
	}

	/**
	 * Message based exception constructor (no parent, no extension)
	 * @param message describing the error
	 * @param et of the error (who caused it?)
	 */
	public EX_Exception(String message, ErrorType et)
	{
		this(message, -1, et, null, null);
	}

	/**
	 * Message based exception constructor (no parent, with extension)
	 * @param message describing the error
	 * @param et of the error (who caused it?)
	 * @param extension to this error
	 */
	public EX_Exception(String message, ErrorType et, Exception extension)
	{
		this(message, -1, et, null, extension);
	}

	/**
	 * Message based exception constructor (with parent, no extension)
	 * @param message describing the error
	 * @param et of the error (who caused it?)
	 * @param parent component that threw the exception
	 */
	public EX_Exception(String message, ErrorType et, Component parent)
	{
		this(message, -1, et, parent, null);
	}

	/**
	 * Message based exception constructor (with parent, with extension)
	 * @param message describing the error
	 * @param et of the error (who caused it?)
	 * @param parent component that threw the exception
	 * @param extension to this error
	 */
	public EX_Exception(String message, ErrorType et, Component parent, Exception extension)
	{
		this(message, -1, et, parent, extension);
	}

	/**
	 * ID based exception constructor (no parent, no extension)
	 * @param id describing the exception
	 * @param et of the error (who caused it?)
	 */
	public EX_Exception(int id, ErrorType et)
	{
		this("[ID Exception]", Math.max(0, id), et, null, null);
	}

	/**
	 * ID based exception constructor (no parent, with extension)
	 * @param id describing the exception
	 * @param et of the error (who caused it?)
	 * @param extension to this error
	 */
	public EX_Exception(int id, ErrorType et, Exception extension)
	{
		this("[ID Exception]", Math.max(0, id), et, null, extension);
	}

	/**
	 * ID based exception constructor (with parent, no extension)
	 * @param id describing the exception
	 * @param et of the error (who caused it?)
	 * @param parent component that threw the exception
	 */
	public EX_Exception(int id, ErrorType et, Component parent)
	{
		this("[ID Exception]", Math.max(0, id), et, parent, null);
	}

	/**
	 * ID based exception constructor (with parent, with extension)
	 * @param id describing the exception
	 * @param et of the error (who caused it?)
	 * @param parent component that threw the exception
	 * @param extension to this error
	 */
	public EX_Exception(int id, ErrorType et, Component parent, Exception extension)
	{
		this("[ID Exception]", Math.max(0, id), et, parent, extension);
	}

	/**
	 * Duplicate the content of the exception but associate a new parent component with it
	 * @param exception to duplicate
	 * @param component to associate with
	 * @return the new exception object with the passed parent
	 */
	public EX_Exception duplicateWithParent(EX_Exception exception, Component component)
	{
		if(exception.isIDBased())
			return new EX_Exception(exception.getID(), exception.getErrorType(), component);
		else
			return new EX_Exception(exception.getMessage(), exception.getErrorType(), component);
	}

	/**
	 * Get the error type
	 * @return this exception's ErrorType
	 */
	public ErrorType getErrorType()
	{
		return this.et;
	}

	/**
	 * Get the error type in a string representation
	 * @return a string containing the error type
	 */
	public String getErrorTypeAsString()
	{
		switch(this.et)
		{
		case ERROR_REPORTER:
			{
				return "Error Reporting Module";
			}
		case ERROR_FILESYSTEM:
			{
				return "File System Module";
			}
		case ERROR_GRAPHSEARCHING:
			{
				return "Graph Searching Module";
			}
		case ERROR_GRAPHICALINTERFACE:
			{
				return "Graphical Interface Module";
			}
		default:
			{
				return "Unknown Module";
			}
		}
	}

	/**
	 * Return the parent component that threw the exception
	 * @return Component object
	 */
	public Component getParent()
	{
		return this.parent;
	}

	/**
	 * Get the ID associated with this exception
	 * @return an integer ID
	 */
	public int getID()
	{
		return this.id;
	}

	/**
	 * Determine if this exception is based on a message or an error id
	 * @return whether this is exception is id based or not
	 */
	public boolean isIDBased()
	{
		return this.id != -1;
	}

	/**
	 * Get the exception object that triggered the creation of this PPR_Exception
	 * @return an exception object that contains information about the original error
	 */
	public Exception getExtendedException()
	{
		return this.extended;
	}

}
