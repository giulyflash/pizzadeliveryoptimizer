package userinterface;

import com.mxgraph.model.mxGraphModel;

public class UI_mxGraphModel extends mxGraphModel
{

	/**
	 * Customised graph model constructor
	 */
	public UI_mxGraphModel()
	{
		super();
	}

	/**
	 * Determine if a cell can be connected to by an edge
	 * @param object to test
	 * @return false
	 */
	public boolean isConnectable(Object object)
	{
		return false;
	}

}
