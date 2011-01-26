package userinterface;

import com.mxgraph.view.mxGraph;

public class UI_mxGraph extends mxGraph
{

	// Local copy of the map editor instance for event reporting
	private UI_MapEditor mapeditor;

	/**
	 * Customised mxGraph constructor
	 * @param mapeditor to use
	 */
	public UI_mxGraph(UI_MapEditor mapeditor)
	{
		super();
		this.mapeditor = mapeditor;
		this.selectionModel = new UI_mxGraphSelectionModel(this, mapeditor);
		this.setModel(new UI_mxGraphModel());
	}

	/**
	 * Call back function for cells being moved
	 * @param cells that were moved
	 * @param dx by which they were moved
	 * @param dy by which they were moved
	 * @param disconnect -
	 * @param constraint -
	 */
	public void cellsMoved(Object[] cells, double dx, double dy, boolean disconnect, boolean constraint)
	{
		super.cellsMoved(cells, dx, dy, disconnect, constraint);
		this.mapeditor.cellsMoved(cells, dx, dy);
	}

}
