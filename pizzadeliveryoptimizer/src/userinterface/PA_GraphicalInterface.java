package userinterface;

import errors.PA_ErrorReporter;

public class PA_GraphicalInterface
{

	/**
	 * Graphical interface top-level class constructor
	 * @param reporter to use in case of an error/warning/message
	 */
	public PA_GraphicalInterface(PA_ErrorReporter reporter)
	{
		new UI_MapEditor(reporter);
	}

}
