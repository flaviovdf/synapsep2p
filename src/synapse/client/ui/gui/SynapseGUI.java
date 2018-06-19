package synapse.client.ui.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.ourgrid.yal.Logger;
import org.ourgrid.yal.LoggerCreator;

import synapse.client.ClientCommunicator;
import synapse.client.ClientConfig;
import synapse.client.ClientFacade;
import synapse.client.ui.ClientNotConnectedException;
import synapse.common.Provider;
import synapse.common.SynapseLogWriter;

/**
 * Graphical User Interface for Synapse.
 */
public class SynapseGUI extends JFrame implements ActionListener {
	
	//Strings used to identify panels
	private final String SEARCH = "search";
	private final String DOWNLOADS = "downloads";
	private final String UPLOADS = "uploads";
	private final String CONFIG = "config";
	private final String IMAGE = "logo";
	
	//Main frame container
	private Container container;
	
	//used to set and control CardLayout used in the center frame.
	private CardLayout cardLayoutCenter;
	
	//Labels
	private JLabel connectionStatus;
	
	//Buttons
	private JButton searchButton;
	private JButton downloadsButton;
	private JButton uploadsButton;
	private JButton configButton;
	private JButton helpButton;
	private JButton aboutButton;
	private JButton connectDisconnectButton;
	
	//Panels
	private JPanel northButtonPanel;
	private JPanel southButtonPanel;
	private JPanel cardPanel;
	private SearchPanel searchPanel;
	private DownloadsPanel downloadsPanel;
	private UploadsPanel uploadsPanel;
	
	//Attributes used for communicanting with program
	private Provider server = null;
	private Logger logger; 
	private ClientCommunicator communicator;
	private ClientFacade facade;
	
	//Used so that some functions only work when connected
	private boolean connected;
	
	/**
	 * Creates Graphical Interface.
	 */
	public SynapseGUI() {
		//Sets the main frame
		super("Synapse v1.0 - Neuron \t Status: Offline");
		int width=795;
		int height=580;
		super.setSize(width,height);
		super.setIconImage(Toolkit.getDefaultToolkit().getImage(ClientConfig.getRootDir() + File.separator + "icon"+File.separator+"icon.jpg"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screenSize.width/2 - (width/2),
                screenSize.height/2 - (height/2));
		this.container = super.getContentPane();
		this.container.setLayout(new BorderLayout());
		
		this.initializeButtons();
		this.initializeLabels();
		this.initializeMainWindow();
				
		this.logger = LoggerCreator.config(SynapseLogWriter.getLogWriter(ClientConfig.getLogFilename(), true));
		this.connected = false;
		this.setFunctionButtonsStatus(false);
		
		super.setResizable(false);
		super.setVisible(true);
	}
	
	/**
	 * Intializes Labels
	 */
	private void initializeLabels() {
		this.connectionStatus = new JLabel("Disconnected",new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "connected.jpg"), SwingConstants.LEFT);
		this.connectionStatus.setForeground(Color.RED);
		this.connectionStatus.setHorizontalTextPosition(SwingConstants.RIGHT);
	}

	/**
	 * Configures JButtons.
	 */
	private void initializeButtons() {
		this.searchButton = new JButton("Search", new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "search.jpg"));
		this.searchButton.setBackground(Color.WHITE);
		this.searchButton.setToolTipText("Search for files in Synapse Network and lists old Search Results");
		this.searchButton.addActionListener(this);
		
		this.downloadsButton = new JButton("Downloads", new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "download.jpg"));
		this.downloadsButton.setBackground(Color.WHITE);
		this.downloadsButton.setToolTipText("Download status");
		this.downloadsButton.addActionListener(this);
		
		this.uploadsButton = new JButton("Uploads",new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "upload.jpg"));
		this.uploadsButton.setBackground(Color.WHITE);
		this.uploadsButton.setToolTipText("Upload status");
		this.uploadsButton.addActionListener(this);
		
		this.configButton = new JButton("Configure", new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "configure.jpg"));
		this.configButton.setBackground(Color.WHITE);
		this.configButton.setToolTipText("Set up your configuration");
		this.configButton.addActionListener(this);
		
		this.aboutButton = new JButton("About", new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "about.jpg"));
		this.aboutButton.setBackground(Color.WHITE);
		this.aboutButton.setToolTipText("About Synapse");
		this.aboutButton.addActionListener(this);
		
		this.helpButton = new JButton("Help", new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "help.jpg"));
		this.helpButton.setBackground(Color.WHITE);
		this.helpButton.setToolTipText("Help");
		this.helpButton.addActionListener(this);
		
		this.connectDisconnectButton = new JButton("Connect", new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "connect.jpg"));
		this.connectDisconnectButton.setBackground(Color.WHITE);
		this.connectDisconnectButton.setToolTipText("Connect to Synapse Network");
		this.connectDisconnectButton.addActionListener(this);
	}

	/**
	 * Sets if function buttons are active or inactive depending on the
	 * currenct connection status.
	 */
	private void setFunctionButtonsStatus(boolean status) {
		this.uploadsButton.setEnabled(status);
		this.searchButton.setEnabled(status);
		this.downloadsButton.setEnabled(status);
		this.connectionStatus.setForeground((status==true)?Color.GREEN:Color.RED);
		this.connectionStatus.setText((status==true)?"Connected":"Disconnected");
		this.connectDisconnectButton.setText((status==false)?"Connect":"Disconnect");
	}

	/**
	 * Sets the main program window.
	 */
	private void initializeMainWindow() {
		//Initializes Panels
		this.northButtonPanel = new JPanel();
		this.southButtonPanel = new JPanel();
		this.searchPanel = new SearchPanel(this);
		this.downloadsPanel = new DownloadsPanel(this);
		this.uploadsPanel = new UploadsPanel();
		
		//Sets Layouts
		this.northButtonPanel.setLayout(new GridLayout());
		this.southButtonPanel.setLayout(new GridLayout());

		//Adds Components do Button Panel
		this.northButtonPanel.add(this.searchButton);
		this.northButtonPanel.add(this.downloadsButton);
		this.northButtonPanel.add(this.uploadsButton);
		this.northButtonPanel.add(this.configButton);
		this.northButtonPanel.add(this.helpButton);
		this.northButtonPanel.add(this.aboutButton);
		
		//Adds Components to ConnectPanel
		this.southButtonPanel.setBackground(Color.WHITE);
		this.southButtonPanel.add(this.connectionStatus);
		this.southButtonPanel.add(this.connectDisconnectButton);
				
		//Sets the cardlayout
		this.cardLayoutCenter = new CardLayout();
		this.cardPanel = new JPanel();
		this.cardPanel.setLayout(this.cardLayoutCenter);
		this.container.add(cardPanel, BorderLayout.CENTER);
		
		//Adds Panels
		this.container.setBackground(Color.BLACK);
		this.container.add(this.northButtonPanel, BorderLayout.NORTH);
		this.container.add(this.southButtonPanel, BorderLayout.SOUTH);
		
		//Adds components
		this.cardPanel.add(new JLabel(new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "synapse.jpg")), this.IMAGE);
		this.cardPanel.add(this.searchPanel, this.SEARCH);
		this.cardPanel.add(this.downloadsPanel, this.DOWNLOADS);
		this.cardPanel.add(this.uploadsPanel, this.UPLOADS);
	}
	
	/**
	 * Refresh the panel in use.
	 * 
	 * @param panel Panel in use.
	 */
	private void refreshThisPanel(RefreshablePanel panel) {
	    new PanelRefresher(panel);
	}

	/**
	 * Configures and shows the Help Frame.
	 */
	private void helpFrame() {
		String text = "Synapse v1.0 Help\n\n" +
		"Basic Functionalities: \n\n" +
		"Search: Searches for files from other users connected to the Synapse Network.\n" +
				 "also lists results from previous searches.\n\n"+
		"Downloads: Synapse´s Download Manager, from here you can view and control\n" +
					"your downloads.\n\n"+
		"Uploads: Synapse´s Upload Manager, from here you can view and control\n" +
				  "your uploads.\n\n"+
		"Configure: Sets your Shared Folder and Other Preferences.\n\n" +
		"Help: This Help Window.\n\n"+
		"About: About Us.";
		JTextArea textArea = new JTextArea();
		textArea.append(text);
		textArea.setEditable(false);
		JOptionPane.showMessageDialog(null,textArea,"Help",JOptionPane.OK_OPTION,new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "help.jpg"));
	}
	
	/**
	 * Configures and shows the About Frame.
	 */
	private void aboutFrame() {
		String text = "Synapse v1.0\n\n" +
				"This software was developed by Computer Science\n" +
				"students from UFCG - Universidade Federal de\n" +
				"Campina Grande during the Data Structures and\n" +
				"Algorithms course in 2004. It has no financial\n" +
				"purposes, due to the fact that it is just a result\n" +
				"of our studies.\n\n" +
				"The team:\n" +
				"Felipe Ribeiro - felipern@lcc.ufcg.edu.br\n" +
				"Flávio Roberto - flaviors@lcc.ufcg.edu.br\n" +
				"Flavio Vinicius - flaviov@lcc.ufcg.edu.br\n" +
				"João Arthur - joaoabm@lcc.ufcg.edu.br\n" +
				"Thiago Emanuel - thiagoepdc@lcc.ufcg.edu.br\n" +
				"Vinícius Ferraz - viniciusfcf@lcc.ufcg.edu.br \n\n\n"+
				"Thank you for using Synapse.";
		JTextArea textArea = new JTextArea();
		textArea.append(text);
		textArea.setEditable(false);
		JOptionPane.showMessageDialog(null,textArea,"About Synapse v1.0",JOptionPane.OK_OPTION,new ImageIcon(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "icon.jpg"));
	}
	
	/**
	 * Shows configurations options to user.
	 */
	private void configFrame() {
		JOptionPane.showMessageDialog(this, "Since you are running this from the Main GUI changes\n made will only take effect when you reconnect!");
		new ConfigDialog(this);
	}
	
	/**
	 * Connects to the server.
	 */
	private void connectOrDisconnectToServer() {
		if (this.connected) {
			this.facade.shutdown().blockingRemove();
			this.server = null;
			this.connected = false;
			this.cardLayoutCenter.first(this.cardPanel);
			this.setFunctionButtonsStatus(false);
			super.setTitle("Synapse v1.0 - Neuron \t Status: Offline");
		}
		else {
			
			String serverInfo = "";
			boolean valid = false;
			
			do {
				serverInfo = JOptionPane.showInputDialog(this, "Enter Server: ");
				
				try {
					//if user typed cancel
					if (serverInfo == null) {
						return;
					}

					super.setTitle("Synapse v1.0 - Neuron \t Status: Connecting to "+serverInfo);
					
					server = (Provider)Naming.lookup("rmi://"+serverInfo+":"+ClientConfig.getPort()+"/Proxy");
				
					valid = true; //did not throw exception
				} catch (MalformedURLException e) {
					JOptionPane.showMessageDialog(this, "Please check the server address you typed.", "Malformed URL!", JOptionPane.ERROR_MESSAGE);
					super.setTitle("Synapse v1.0 - Neuron \t Status: Error occurred while connecting to "+serverInfo);
				} catch (RemoteException e) {
					JOptionPane.showMessageDialog(this, "The server does not appear to exist, please check the address you typed.", "Remote Exception!", JOptionPane.ERROR_MESSAGE);
					super.setTitle("Synapse v1.0 - Neuron \t Status: Error occurred while connecting to "+serverInfo);
				} catch (NotBoundException e) {
					super.setTitle("Synapse v1.0 - Neuron \t Status: Error occurred while connecting to "+serverInfo);
					JOptionPane.showMessageDialog(this, "There was a problem connecting to the server, please check the address you typed of try again later.", "Not Bound Exception!", JOptionPane.ERROR_MESSAGE);
				}
			} while(!valid);
			
			
			this.connectionStatus.setText("Loading shared folder...");
			this.connectionStatus.setForeground(Color.YELLOW);
			
	        this.facade = new ClientFacade();
	        this.facade.config();
	        this.facade.startEventProcessor();
	        
			try {
				server.identify(facade.getClient());
				logger.info(SynapseGUI.class.getName(), "Identify command was sent.");
			} catch (RemoteException e1) {
				JOptionPane.showMessageDialog(this, "Unable to Identify yourself, please try again.", "Remote Exception!", JOptionPane.ERROR_MESSAGE);
	            logger.info(SynapseGUI.class.getName(), "Identify command could not be sent.");
	            return;
			}
			
			this.communicator = new ClientCommunicator(server, facade.getClient());

			this.connected = true;
			this.setFunctionButtonsStatus(true);
			super.setTitle("Synapse v1.0 - Neuron \t Status: Connected to "+serverInfo);
		}
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {		
		if(e.getSource() instanceof JButton) {
			JButton pressedButton = (JButton)e.getSource();
			if (pressedButton == this.searchButton) {
				this.cardLayoutCenter.show(this.cardPanel, this.SEARCH);
				this.refreshThisPanel(this.searchPanel);
			}
			else if (pressedButton == this.downloadsButton){
				this.cardLayoutCenter.show(this.cardPanel, this.DOWNLOADS);
				this.refreshThisPanel(this.downloadsPanel);
			}
			else if (pressedButton == this.uploadsButton){
				this.cardLayoutCenter.show(this.cardPanel, this.UPLOADS);
				this.refreshThisPanel(this.uploadsPanel);
			}
			else if (pressedButton == this.configButton) {
				this.configFrame();
			}
			else if (pressedButton == this.helpButton) {
				this.helpFrame();
			}
			else if (pressedButton == this.aboutButton) {
				this.aboutFrame();
			}
			else if (pressedButton == this.connectDisconnectButton) {
				this.connectOrDisconnectToServer();
			}
		}
		
		else if(e.getSource() instanceof DownloadEventInformation) {
			DownloadEventInformation download = (DownloadEventInformation) e.getSource();
			try {
				communicator.sendGetToProviders(download.getId().longValue(),download.getHash());
			}
			catch(Exception e1) {
				logger.info(SynapseGUI.class.getName(), "Exception on starting download: "+e);
			}
		}
		
		else if(e.getSource() instanceof SearchEventInformation) {
			SearchEventInformation searchEvent = (SearchEventInformation) e.getSource();
			if (searchEvent.getWhatToDo() == SearchEventInformation.NEW_SEARCH) {
				try {
					communicator.search(searchEvent.searchKey());
				} catch (ClientNotConnectedException e1) {
					logger.info(SynapseGUI.class.getName(), "Exception on search call: "+e);
				}
			}
			else {
				try {
					communicator.searchMore(searchEvent.searchKey());
				} catch (ClientNotConnectedException e1) {
					logger.info(SynapseGUI.class.getName(), "Exception on more sources call: "+e);
				}
			}
		}	
	}
	
	/**
	 * Main 
	 */
	public static void main (String[] args) {
		//Start Splash Screen
		SplashScreen splash = new SplashScreen(ClientConfig.getRootDir() + File.separator + "icon" + File.separator + "splash.jpg",1500);
		
		//Opens GUI
		SynapseGUI window = new SynapseGUI();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private class PanelRefresher extends AbstractAction {
	    
	    private RefreshablePanel panel;

        public PanelRefresher (RefreshablePanel panel) {
	        this.panel = panel;
	        new Timer(1000,this).start();
	    }
	    
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            this.panel.refresh();
        }
	    
	}
}