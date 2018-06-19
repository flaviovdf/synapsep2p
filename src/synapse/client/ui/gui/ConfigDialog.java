package synapse.client.ui.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import synapse.client.ClientConfig;

/**
 * Configure Dialog manipulates configuration for synapse.
 */
public class ConfigDialog extends JDialog implements ActionListener {
	
	/**
	 * Button used to save information.
	 */
	private JButton saveButton;
	
	/**
	 * Button used to cancel changes.
	 */
	private JButton cancelButton;
	
	/**
	 * Button used to choose log directory.
	 */
	private JButton chooseLog;
	
	/**
	 * Button used to choose shared folder.
	 */
	private JButton chooseFolder;
	
	/**
	 * Text field used to edit port configuration.
	 */
	private JTextField portField;
	
	/**
	 * Text field used to edit shared folder configuration.
	 */
	private JTextField folderField;
	
	/**
	 * Text field used to edit log configuration.
	 */
	private JTextField logField;
	
	/**
	 * Container from the superclass.
	 */
	private Container container;
	
	/**
	 * Used to identify if the program is running from the main gui.
	 */
	private boolean runningFromGui;
	
	/**
	 * Creates a new Config Dialog.
	 * 
	 * @param owner Owner of this dialog.
	 */
	public ConfigDialog(Frame owner) {
		super(owner);
		this.setDialog();
		this.runningFromGui = true;
	}
	
	/**
	 * Creates a new Config Dialog with no owner.
	 */
	public ConfigDialog () {
		this.setDialog();
		this.runningFromGui = false;
	}
	
	/**
	 * Sets options for the config dialog.
	 */
	private void setDialog() {
		super.setTitle("Configuration for Synapse");		
		super.setSize(300, 140);
		super.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		super.setLocation(screenSize.width/2 - (300/2),
                screenSize.height/2 - (140/2));
		
		container = super.getContentPane();
		container.setLayout(new BorderLayout());
		
		this.centerPanel();
		this.southPanel();
		
		super.setVisible(true);
	}

	/**
	 * Creates and configures south panel.
	 */
	private void southPanel() {
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);
		this.cancelButton.setToolTipText("Cancel changes, and exits this dialog");
		
		this.saveButton = new JButton("Save");
		this.saveButton.addActionListener(this);
		if (this.runningFromGui) {
			this.saveButton.setToolTipText("Save changes, will only take effect when program is rerun");
		}
		else {
			this.saveButton.setToolTipText("Save changes and runs Synapse");
		}
		
		
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(saveButton);
		southPanel.add(cancelButton);
		container.add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * Creates and configures central panel.
	 */
	private void centerPanel() {
		this.chooseLog = new JButton("Choose");
		this.chooseLog.setToolTipText("Choose Log");
		this.chooseLog.addActionListener(this);
		
		this.chooseFolder = new JButton("Choose");
		this.chooseFolder.setToolTipText("Choose Shared Folder");
		this.chooseFolder.addActionListener(this);
		
		JPanel center = new JPanel(new GridLayout(3, 3, 5, 5));
		center.add(new JLabel("Default Port: "));
		this.portField = new JTextField(ClientConfig.getPort());
		center.add(portField);
		center.add(new JLabel());
		
		center.add(new JLabel("Log: "));
		this.logField = new JTextField(ClientConfig.getRootDir()+File.separator+ClientConfig.getLogFilename());
		center.add(logField);
		center.add(this.chooseLog);
		
		center.add(new JLabel("Shared Folder: "));
		this.folderField = new JTextField(ClientConfig.getSharedFolder());
		center.add(folderField);
		center.add(this.chooseFolder);
		
		container.add(center, BorderLayout.CENTER);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.cancelButton) {
			super.dispose();
		}
		if (e.getSource() == this.saveButton) {
			
			String newPort = this.portField.getText();
			String newFolder = this.folderField.getText();
			String newLog = this.logField.getText();
			
			ClientConfig.setProperty(ClientConfig.PORT, newPort);
			ClientConfig.setProperty(ClientConfig.SHARED_FOLDER, newFolder);
			ClientConfig.setProperty(ClientConfig.LOGNAME, newLog);
			ClientConfig.saveProperties();
			
			if (!this.runningFromGui) {
				new SynapseGUI();
			}
			
			super.dispose();
		}
		if (e.getSource() == this.chooseFolder || e.getSource() ==  this.chooseLog) {
			JFileChooser folderChooser = new JFileChooser();
		    folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			folderChooser.setCurrentDirectory(new File(ClientConfig.getRootDir()));
		    
		    String path;
			int returnVal = folderChooser.showDialog(this, "Select");
		    if(returnVal == JFileChooser.APPROVE_OPTION) {			    
			    if (e.getSource() == this.chooseFolder) {
			    	this.folderField.setText(folderChooser.getSelectedFile().getAbsolutePath());
			    }
			    else {
			    	this.logField.setText(folderChooser.getSelectedFile().getAbsolutePath()+File.separator+"client.log");
			    }
		    }
		}
	}
}