package filesystem;

import java.io.File;

public class FS_Utils
{

	/**
	 * Get the extension associated to a File object
	 * @param file object to analyse
	 * @return the extension in a String
	 */
	public static String getExtension(File file)
	{
		String filename = file.getName();
		int dot = filename.lastIndexOf(".");
	    return filename.substring(dot + 1).toLowerCase();
	}

	/**
	 * Get the file name associated to the File object
	 * @param file object to analyse
	 * @return the file name in a String
	 */
	public static String getFilename(File file)
	{
		String filename = file.getAbsolutePath();
		int dot = filename.lastIndexOf(".");
		return filename.substring(0, dot).toLowerCase();
	}

	/**
	 * Pad a string with spaces (put to the right)
	 * @param str to pad
	 * @param n length of the string (filled with spaces to fit that length)
	 * @return the new string
	 */
	public static String PadString(String str, int n)
	{
	     return String.format("%1$-" + n + "s", str);  
	}

}
