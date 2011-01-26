package userinterface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class UI_DataEditor extends JDialog implements ActionListener
{

	// Serialisation ID required by the Eclipse IDE
	private static final long serialVersionUID = 1L;

	// Panel components (labels, buttons and input field)
	private JLabel titleLabel, indicatorLabel, dataLabel;
	private JButton okButton, cancelButton;
	private JTextField inputField;

	// Temporary storage for the user input
	private String userInput;

	/**
	 * Data editor constructor
	 * @param owner of this dialog
	 * @param indicatorLabel string (second row, middle alignment text label) (cannot be null)
	 * @param dataLabel string (third row, left alignment text label) (cannot be null)
	 * @param inputField string (third row, middle and right alignment text field) (can be null)
	 */
	public UI_DataEditor(Frame owner, String indicatorLabel, String dataLabel, String inputField)
	{
		super(owner, "Pizza Delivery Optimzer Data Editor by Peregrine Park");

		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setModal(true);

		this.initComponents();

		this.indicatorLabel.setText(indicatorLabel);
		this.dataLabel.setText(dataLabel);
		this.inputField.setText((inputField == null) ? "" : inputField);

		userInput = null;
	}

	/**
	 * Initialise all the components on the frame and place them
	 */
	private void initComponents()
	{
		titleLabel = new JLabel();
		indicatorLabel = new JLabel();
		okButton = new JButton();
		cancelButton = new JButton();
		dataLabel = new JLabel();
		inputField = new JTextField();

		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		titleLabel.setText("Pizza Delivery Optimzer Data Editor by Peregrine Park");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(titleLabel);
		titleLabel.setBounds(0, 0, 350, 20);

		indicatorLabel.setText("????");
		indicatorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(indicatorLabel);
		indicatorLabel.setBounds(0, 20, 350, 20);

		okButton.setText("Ok");
		contentPane.add(okButton);
		okButton.setBounds(85, 70, 100, okButton.getPreferredSize().height);
		okButton.addActionListener(this);

		cancelButton.setText("Cancel");
		contentPane.add(cancelButton);
		cancelButton.setBounds(190, 70, 100, 25);
		cancelButton.addActionListener(this);

		dataLabel.setText("???");
		dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(dataLabel);
		dataLabel.setBounds(0, 45, 100, 20);

		contentPane.add(inputField);
		inputField.setBounds(100, 45, 250, inputField.getPreferredSize().height);

		contentPane.setPreferredSize(new Dimension(375, 150));
		setSize(375, 150);
		setLocationRelativeTo(getOwner());
	}

	/**
	 * Button click call back method
	 * @param event description
	 */
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
			this.userInput = this.inputField.getText();
		else
			this.userInput = null;
		this.dispose();
	}

	/**
	 * Get the user input from the dialog
	 * @return the string representation of that input
	 */
	public String getUserInput()
	{
		this.setVisible(true);
		return this.userInput;
	}

}
