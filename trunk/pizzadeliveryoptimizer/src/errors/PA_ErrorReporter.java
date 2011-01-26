package errors;

import javax.swing.JOptionPane;

public class PA_ErrorReporter
{

	// TODO: Implement own error dialogs and allow for the extended information if available!
	// Extended information located in the PRP_Exception object

	/**
	 * Error reporter constructor
	 * @throws PRP_Exception
	 */
	public PA_ErrorReporter() throws EX_Exception
	{
		//
	}

	/**
	 * Display an appropriate error message based on a PRP_Exception
	 * @param exception to process
	 */
	private void display(EX_Exception exception, String severity, int type)
	{
		String title = severity + " - " + exception.getErrorTypeAsString();
		String message = exception.getMessage();
		do {
			if(exception.getExtendedException() != null)
				message += "\n\nExtended Information:\n\n" + exception.getExtendedException().getMessage();
			if(exception.getExtendedException() instanceof EX_Exception)
				exception = (EX_Exception)exception.getExtendedException();
			else
				break;
		} while(true);
		JOptionPane.showMessageDialog(exception.getParent(), message, title, type);
	}

	/**
	 * Warn the user there has been an irrecoverable error and we need to quit
	 * @param exception object describing the situation
	 */
	public void error(EX_Exception exception)
	{
		this.display(exception, "Fatal Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Display a warning message to the user to get his attention on an important matter
	 * @param exception object describing the situation
	 */
	public void warning(EX_Exception exception)
	{
		this.display(exception, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Display a simple message to remind the user of the situation
	 * @param exception object describing the situation
	 */
	public void message(EX_Exception exception)
	{
		this.display(exception, "Information", JOptionPane.INFORMATION_MESSAGE);
	}

}
