package application;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import userinterface.PA_GraphicalInterface;
import errors.EX_Exception;
import errors.EX_Exception.ErrorType;
import errors.PA_ErrorReporter;

public class PA_Application
{

	// Error Reporting Object
	private PA_ErrorReporter errorreporter;

	/**
	 * Main method of the entire project
	 * @param args passed to the program
	 */
	public static void main(String[] args)
	{
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	    	// Non fatal error. Continue program execution
	    	JOptionPane.showMessageDialog(null, e.getMessage(), "Warning - Look and Feel", JOptionPane.WARNING_MESSAGE);
	    }
	    new PA_Application();
	}

	/**
	 * Application constructor
	 */
	public PA_Application()
	{
		try {
			this.errorreporter = new PA_ErrorReporter();
			new PA_GraphicalInterface(this.errorreporter);
		} catch(EX_Exception e) {
			if(e.getErrorType() == ErrorType.ERROR_REPORTER)
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal Error - Error Reporting Module", JOptionPane.ERROR_MESSAGE);
			else
				this.errorreporter.error(e);
			System.exit(-1);
		}
	}

}
