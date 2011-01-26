package userinterface;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;

import datastructures.DS_House;
import datastructures.DS_Path;
import errors.EX_Exception;
import errors.PA_ErrorReporter;
import filesystem.PA_FileSystem;
import graphsearch.GS_Dijkstra;

public class UI_MapEditor extends JFrame implements ActionListener
{

	// Serialisation ID required by the Eclipse IDE
	private static final long serialVersionUID = 1L;

	// Top panel buttons and labels
	private JLabel titleLabel;
	private JButton createHouse, createPath, editObject;
	private JButton removeButton, moveSelectButton, openMap, newMap;

	// Panel definitions (top, middle and bottom)
	private JPanel toppanel, middlepanel, bottompanel;

	// Bottom panel progress bar and labels
	private JProgressBar searchProgress;
	private JLabel statsLabel, statusLabel;

	// Move/Select button final text definitions
	private final int MOVE_MODE = 0, SELECT_MODE = 1;
	private final String[] buttonText = { "Moving", "Selecting" };
	private final String[] tooltipText = { "You are now in move mode, simply move a house by dragging it",
			"You are now in select mode, select a house to get calculate the minimum path to it" };

	// Error reporter to use when anything occurs
	private PA_ErrorReporter reporter;

	// File system object to use
	private PA_FileSystem filesystem;

	// Dijkstra graph searching object
	private GS_Dijkstra dijkstra;

	// Graph object (located on the middle panel)
	private UI_mxGraph graph;

	// Current set of vertices on the map canvas (for orphan detection)
	private Object[] vertices;

	// Temporary variables for path creation
	private boolean isPathCreating;
	private DS_House startHouse;

	// Determines if we're in moving mode or selecting mode
	private boolean isMoving;

	/**
	 * Map editor GUI constructor
	 * @param reporter to use in case of an error/warning/message
	 */
	public UI_MapEditor(PA_ErrorReporter reporter)
	{
		this.isMoving = true;
		this.dijkstra = null;
		this.filesystem = null;
		this.startHouse = null;
		this.isPathCreating = false;
		this.reporter = reporter;

		this.initComponents();
		this.setVisible(true);

		this.setApplicationStatus("Application loaded...");
		this.setToolSetStatus(false);
		this.updateStats();

		this.graph = new UI_mxGraph(this);

		this.graph.setCellsEditable(false);
		this.graph.setCellsDisconnectable(false);
		this.graph.setCellsDeletable(false);
		this.graph.setCellsResizable(false);
		this.graph.setAllowLoops(false);
		this.graph.setAllowDanglingEdges(false);
		this.graph.setVertexLabelsMovable(false);
		this.graph.setKeepEdgesInBackground(true);

		mxGraphComponent mxComponent = new mxGraphComponent(this.graph);
		mxComponent.setAntiAlias(true);
		mxComponent.setTextAntiAlias(true);
		mxComponent.setBounds(0, 0, 800, 490);

		JScrollPane scrollPane = new JScrollPane(mxComponent);
		this.middlepanel.add(scrollPane);
	}

	/**
	 * Initialise all the components on the frame and place them
	 */
	private void initComponents()
	{
		toppanel = new JPanel();
		createHouse = new JButton();
		createPath = new JButton();
		editObject = new JButton();
		removeButton = new JButton();
		titleLabel = new JLabel();
		openMap = new JButton();
		newMap = new JButton();
		middlepanel = new JPanel();
		bottompanel = new JPanel();
		searchProgress = new JProgressBar();
		statsLabel = new JLabel();
		statusLabel = new JLabel();
		moveSelectButton = new JButton();

		this.setTitle("Pizza Delivery Optimizer - IB Computer Science Project 2011");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(null);

		toppanel.setLayout(null);

		createHouse.setText("Create House");
		createHouse.setToolTipText("Add a new house to the map");
		toppanel.add(createHouse);
		createHouse.setBounds(10, 35, 110, 35);
		createHouse.addActionListener(this);

		createPath.setText("Create Path");
		createPath.setToolTipText("Add a new path to the map (first click on the starting house and then on the target house)");
		toppanel.add(createPath);
		createPath.setBounds(130, 35, 110, 35);
		createPath.addActionListener(this);

		editObject.setText("Edit");
		editObject.setToolTipText("Edit the currently selected object");
		toppanel.add(editObject);
		editObject.setBounds(250, 35, 110, 35);
		editObject.addActionListener(this);

		removeButton.setText("Remove");
		removeButton.setToolTipText("Removes the currently selected object");
		toppanel.add(removeButton);
		removeButton.setBounds(370, 35, 85, 35);
		removeButton.addActionListener(this);

		moveSelectButton.setText(this.buttonText[MOVE_MODE]);
		moveSelectButton.setToolTipText(this.tooltipText[MOVE_MODE]);
		toppanel.add(moveSelectButton);
		moveSelectButton.setBounds(465, 35, 85, 35);
		moveSelectButton.addActionListener(this);

		titleLabel.setText("Pizza Delivery Optimizer - IB Computer Science Project 2011 by Peregrine Park");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		toppanel.add(titleLabel);
		titleLabel.setBounds(0, 10, 800, titleLabel.getPreferredSize().height);

		openMap.setText("Open Map");
		openMap.setToolTipText("Open a map available on the computer's file system");
		toppanel.add(openMap);
		openMap.setBounds(560, 35, 110, 35);
		openMap.addActionListener(this);

		newMap.setText("New Map");
		newMap.setToolTipText("Create a new map from scratch");
		toppanel.add(newMap);
		newMap.setBounds(680, 35, 110, 35);
		newMap.addActionListener(this);

		this.add(toppanel);
		toppanel.setBounds(0, 0, 800, 80);

		middlepanel.setLayout(new GridLayout(1, 1));
		middlepanel.setBounds(0, 80, 800, 490);
		this.add(middlepanel);

		bottompanel.setLayout(null);

		searchProgress.setStringPainted(true);
		searchProgress.setToolTipText("Progress of the current operation");
		bottompanel.add(searchProgress);
		searchProgress.setBounds(585, 0, 210, 20);

		statsLabel.setText("text");
		bottompanel.add(statsLabel);
		statsLabel.setBounds(365, 0, 200, 20);

		statusLabel.setText("text");
		bottompanel.add(statusLabel);
		statusLabel.setBounds(5, 0, 340, 20);

		this.add(bottompanel);
		bottompanel.setBounds(0, 575, 800, 20);

		this.setSize(new Dimension(806, 633));
	}

	/**
	 * Set whether or not the buttons to create and edit houses and paths are enabled
	 * @param status to set them to
	 */
	private void setToolSetStatus(boolean status)
	{
		this.createHouse.setEnabled(status);
		this.createPath.setEnabled(status);
		this.editObject.setEnabled(status);
		this.removeButton.setEnabled(status);
		this.moveSelectButton.setEnabled(status);
	}

	/**
	 * Set whether or not the buttons to create and edit houses and paths are enabled
	 * but omitting the moveSelectButton
	 * @param status to set them to
	 */
	private void setToolSetStatusSpecial(boolean status)
	{
		this.createHouse.setEnabled(status);
		this.createPath.setEnabled(status);
		this.editObject.setEnabled(status);
		this.removeButton.setEnabled(status);
	}

	/**
	 * Set the application status message in the bottom left corner of the GUI
	 * @param status message to set it to
	 */
	private void setApplicationStatus(String status)
	{
		this.statusLabel.setText(status);
	}

	/**
	 * Update the statistics (count for houses and paths) message on the bottom centre of the GUI
	 */
	private void updateStats()
	{
		String statsString = "";
		if(this.filesystem != null) {
			int[] stats = this.filesystem.getStats();
			statsString = stats[0] + " Houses and " + stats[1] + " Paths";
		} else {
			statsString = "## Houses and ## Paths";
		}
		this.statsLabel.setText(statsString);
	}

	/**
	 * Update the houses and paths on the map's canvas
	 */
	private void updateMap()
	{
		this.graph.getModel().beginUpdate();
		try {
			this.clearMap();
			if(this.filesystem != null) {
				this.vertices = new Object[this.filesystem.getNumHouses()];
				Object parent = this.graph.getDefaultParent();
				for(DS_House house : this.filesystem.getHouseList())
					this.vertices[house.getID()] = this.graph.insertVertex(parent, null, house, house.getX(), house.getY(), 95, 35,
							(house.getID() != 0) ? null : "defaultVertex;fillColor=yellow");
				for(DS_Path path : this.filesystem.getPathList()) {
					Object edge = this.graph.createEdge(parent, null, path, this.vertices[path.getStart().getID()], this.vertices[path.getEnd().getID()], null);
					if(path.isShortest()) this.graph.getModel().setStyle(edge, "defaultEdge;strokeColor=red");//;fillColor=black
					this.graph.addEdge(edge, parent, this.vertices[path.getStart().getID()], this.vertices[path.getEnd().getID()], null);
				}
			}
		} finally {
			this.graph.getModel().endUpdate();
		}
	}

	/**
	 * Clear all of the houses and paths on the map
	 */
	private void clearMap()
	{
		((mxGraphModel)graph.getModel()).clear();
	}

	/**
	 * Check for orphans (vertices with no connections on the map)
	 * @return true if there are, false otherwise
	 */
	private boolean checkForOrphans()
	{
		for(Object vertex : this.vertices) {
			if(vertex != null && this.graph.getModel().getEdgeCount(vertex) == 0) {
				this.setApplicationStatus("Orphan detected (" + this.graph.getModel().getValue(vertex).toString() + "). Cannot proceed...");
				return true;
			}
		}
		return false;
	}

	/**
	 * Handle any events performed by the application (button presses)
	 * @param event description
	 */
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == this.openMap) {
			try {
				this.filesystem = new PA_FileSystem(false);
				this.dijkstra = new GS_Dijkstra(this.filesystem, this.searchProgress);
				this.setApplicationStatus("File system loaded and initliazed...");
				this.setToolSetStatus(true);
				this.setProgress(0, 100, 100);
			} catch(EX_Exception exception) {
				if(!(exception.isIDBased() && exception.getID() == 1))
					this.reporter.error(exception);
				this.filesystem = null;
			}
		} else if(event.getSource() == this.newMap) {
			try {
				this.filesystem = new PA_FileSystem(true);
				this.dijkstra = new GS_Dijkstra(this.filesystem, this.searchProgress);
				this.filesystem.addHouse(new DS_House("Pizzeria", 50, 50));
				this.setApplicationStatus("File system created and initliazed...");
				this.setToolSetStatus(true);
				this.setProgress(0, 100, 100);
			} catch(EX_Exception exception) {
				if(!(exception.isIDBased() && exception.getID() == 1))
					this.reporter.error(exception);
				this.filesystem = null;
			}
		} else if(event.getSource() == this.createHouse) {
			UI_DataEditor houseEditor = new UI_DataEditor(this, "House Creation Tool", "House name:", null);
			String houseName = houseEditor.getUserInput();
			if(houseName != null) {
				try {
					this.filesystem.addHouse(new DS_House(houseName, 50, 50));
					this.setApplicationStatus("House created and added successfully...");
					this.setProgress(0, 100, 100);
				} catch(EX_Exception e) {
					this.reporter.warning(e);
				}
			} else {
				this.setApplicationStatus("Operation cancelled...");
				this.setProgress(0, 100, 0);
			}
		} else if(event.getSource() == this.createPath) {
			this.setApplicationStatus("Now select the starting house...");
			this.isPathCreating = true;
			this.startHouse = null;
		} else if(event.getSource() == this.editObject) {
			if(this.graph.getSelectionCell() != null) {
				Object selection = this.graph.getModel().getValue(this.graph.getSelectionCell());
				if(selection instanceof DS_House) {
					DS_House house = (DS_House)selection;
					UI_DataEditor houseEditor = new UI_DataEditor(this, "House Editing Tool", "House name:", house.getName());
					String houseName = houseEditor.getUserInput();
					if(houseName != null) {
						try {
							house.setName(houseName);
							this.filesystem.overwriteHouse(house.getID(), house);
							this.setApplicationStatus("House edited successfully...");
							this.setProgress(0, 100, 100);
						} catch(EX_Exception e) {
							this.reporter.warning(e);
						}
					} else {
						this.setApplicationStatus("Operation cancelled...");
						this.setProgress(0, 100, 0);
					}
				} else if(selection instanceof DS_Path) {
					DS_Path path = (DS_Path)selection;
					UI_DataEditor pathEditor = new UI_DataEditor(this, "Path Editing Tool", "Path length:", "" + path.getWeight());
					String pathWeight = pathEditor.getUserInput();
					if(pathWeight != null) {
						try {
							path.setWeight(Integer.parseInt(pathWeight));
							this.filesystem.overwritePath(path.getID(), path);
							this.setApplicationStatus("Path edited successfully...");
							this.setProgress(0, 100, 100);
						} catch(EX_Exception e) {
							this.reporter.warning(e);
						}
					} else {
						this.setApplicationStatus("Operation cancelled...");
						this.setProgress(0, 100, 0);
					}
				}
			}
		} else if(event.getSource() == this.removeButton) {
			if(this.graph.getSelectionCell() != null) {
				Object selection = this.graph.getModel().getValue(this.graph.getSelectionCell());
				boolean removed = true;
				try {
					if(selection instanceof DS_House) {
						if(((DS_House)selection).getID() == 0) {
							this.setApplicationStatus("Cannot remove 0rth element (pizzeria)...");
							removed = false;
						} else {
							this.filesystem.removeHouse(((DS_House)selection).getID());
						}
					} else if(selection instanceof DS_Path) {
						this.filesystem.removePath(((DS_Path)selection).getID());
					}
					if(removed) {
						this.setApplicationStatus("Item removed successfully...");
						this.setProgress(0, 100, 100);
					}
				} catch (EX_Exception e) {
					this.reporter.warning(e);
				}
			}
		} else if(event.getSource() == this.moveSelectButton) {
			int mode = MOVE_MODE;
			if(this.isMoving == true) {
				mode = SELECT_MODE;
				this.isMoving = false;
				this.setToolSetStatusSpecial(false);
				this.setApplicationStatus("Now in selection mode...");
			} else {
				this.isMoving = true;
				this.setToolSetStatusSpecial(true);
				this.setApplicationStatus("Now in moving mode...");
			}
			this.moveSelectButton.setText(this.buttonText[mode]);
			this.moveSelectButton.setToolTipText(this.tooltipText[mode]);
			this.dijkstra.clear();
		}
		this.updateMap();
		this.updateStats();
	}

	private void setProgress(int min, int max, int val)
	{
		this.searchProgress.setMinimum(min);
		this.searchProgress.setMaximum(max);
		this.searchProgress.setValue(val);
	}

	/**
	 * Selection change call back method
	 */
	public void selectionChange()
	{
		Object object = this.graph.getSelectionCell();
		if(this.isPathCreating) {
			if(object != null && this.graph.getModel().getValue(object) instanceof DS_House) {
				DS_House house = (DS_House)this.graph.getModel().getValue(object);
				if(this.startHouse == null) {
					this.setApplicationStatus("Now select the target house...");
					this.startHouse = house;
				} else {
					try {
						this.filesystem.addPath(new DS_Path(this.startHouse, house, 1));
						this.setApplicationStatus("Path created successfully...");
						this.setProgress(0, 100, 100);
					} catch(EX_Exception e) {
						this.reporter.warning(e);
					} finally {
						this.isPathCreating = false;
						this.startHouse = null;
						this.updateMap();
					}
				}
			}
		} else if(this.isMoving == false && object != null && this.graph.getModel().getValue(object) instanceof DS_House) {
			try {
				if(!this.checkForOrphans())
					this.dijkstra.search(this.filesystem.getHouseByID(0), (DS_House)this.graph.getModel().getValue(object));
			} catch(EX_Exception e) {
				if(e.isIDBased() && e.getID() == 1) {
					this.setApplicationStatus("Destination unreachable from the pizzeria...");
					this.searchProgress.setValue(0);
				} else {
					this.reporter.warning(e);
				}
			} finally {
				this.updateMap();
			}
		}
	}

	/**
	 * Cells moved call back method
	 * @param cells that were moved
	 * @param dx by which they were moved
	 * @param dy by which they were moved
	 */
	public void cellsMoved(Object[] cells, double dx, double dy)
	{
		try {
			for(Object obj : cells) {
				if(this.graph.getModel().getValue(obj) instanceof DS_House) {
					DS_House house = (DS_House)this.graph.getModel().getValue(obj);
					house.setX(house.getX() + (int)dx);
					house.setY(house.getY() + (int)dy);
					this.filesystem.overwriteHouse(house.getID(), house);
				}
			}
		} catch(EX_Exception e) {
			this.reporter.warning(e);
		}
	}

}
