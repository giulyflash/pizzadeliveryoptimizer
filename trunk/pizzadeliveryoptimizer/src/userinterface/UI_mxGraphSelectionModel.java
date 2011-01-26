package userinterface;

import java.util.Collection;

import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;

public class UI_mxGraphSelectionModel extends mxGraphSelectionModel
{

	// Map editor which implements the call back method
	private UI_MapEditor mapeditor;

	/**
	 * Custom graph selection change model constructor
	 * @param graph to use
	 * @param mapeditor which implements the call back method
	 */
	public UI_mxGraphSelectionModel(mxGraph graph, UI_MapEditor mapeditor)
	{
		super(graph);
		this.mapeditor = mapeditor;
	}

	/**
	 * Fired when the selection on the map changes
	 * @param added selections
	 * @param removed selections
	 */
	protected void changeSelection(Collection<Object> added, Collection<Object> removed)
	{
		super.changeSelection(added, removed);
		mapeditor.selectionChange();
	}

}
