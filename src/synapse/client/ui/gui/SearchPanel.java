package synapse.client.ui.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import synapse.client.manager.HashDoesNotExistException;
import synapse.client.manager.ReplyManager;
import synapse.client.manager.RequestIDDoesNotExistException;
import synapse.common.FileInfo;

/**
 * Search Panel
 */
public class SearchPanel extends JPanel implements ActionListener, RefreshablePanel {
	
	/**
	 * Button used to force an update on a search, remove
	 * when panel will self update.
	 */
	private JButton forceUpdateButton;
	
	/**
	 * Button used to perfom search.
	 */
	private JButton searchButton;
	
	/**
	 * Button used to cancel a result from <code>ReplyManager</code>.
	 */
	private JButton cancelButton;
	
	/**
	 * Panel that will show components used to interact with user.
	 */
	private JPanel westPanel;
	
	/**
	 * Panel that will show search results.
	 */
	private JPanel centerPanel;
	
	/**
	 * Used to select old searches.
	 */
	private JComboBox oldSearchBox;

	/**
	 * Used to receive input for search.
	 */
	private JTextField searchInputField;
	
	/**
	 * Used to pass events to <code>SynapseGUI</code>.
	 */
	private ActionListener parentListener;
	
	/**
	 * Used to receive result information.
	 */
	private ReplyManager replyManager;
	
	/**
	 * Used to identify wich is the current selected id, remove
	 * when panel will self update.
	 */
	private Long idInUse;
	
	/**
	 * Table used to show the results
	 */
	private ResultTable resultTable;
	/**
	 * List used to organize search ids from <code>ReplyManager</code>.
	 */
	private LinkedList resultsIds;

	private static int selectedRow;

	//Constants
	private final int SEEDS = 0;
	private final int FILE_NAME = 1;
	private final int FILE_SIZE = 2;
	private final int FILE_HASH = 3;
	private final int STATUS = 4;
	
	/**
	 * Create a new <code>ConfigPanel</code>.
	 */
	public SearchPanel(ActionListener parentListener) {
		super();
		this.parentListener = parentListener;
		
		super.setLayout(new BorderLayout());
		
		this.resultsIds = new LinkedList();
		this.idInUse = this.idInUse = new Long(-1);
		replyManager = ReplyManager.getInstance();
		
		this.initializeWestLayout();
		this.noSearchLayout();
		
		super.setVisible(true);
	}

	/**
	 * Initizalizes buttons.
	 */
	private void initializeWestLayout() {
		this.searchInputField = new JTextField("enter your search here",16);
		this.searchInputField.addActionListener(this);
		this.searchInputField.setToolTipText("Enter New Search Here");
		
		this.searchButton = new JButton("Search!");
		this.searchButton.setToolTipText("Start new Search");
		this.searchButton.addActionListener(this);
		
		this.cancelButton = new JButton("Cancel Search!");
		this.cancelButton.setToolTipText("Cancels currently selected search result");
		this.cancelButton.addActionListener(this);
		
		/*
		this.forceUpdateButton = new JButton("Force Update");
		this.forceUpdateButton.addActionListener(this);
		this.forceUpdateButton.setToolTipText("Forces Results to be Updated");
		*/
		
		this.oldSearchBox = new JComboBox();
		this.oldSearchBox.setAutoscrolls(true);
		this.oldSearchBox.setToolTipText("List Old SearchResults");
		this.oldSearchBox.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				JComboBox src = (JComboBox)e.getSource();
				if (src.getSelectedItem() != null) {
					idInUse = (Long)src.getSelectedItem();
				}
				createResultTable(idInUse,-1);
			}
		});

		//Initialize Panel
		this.westPanel = new JPanel();
		this.westPanel.setLayout(new GridLayout(16,3, 2, 2));
		this.westPanel.add(new JLabel("New Search:"));
		this.westPanel.add(this.searchInputField);
		this.westPanel.add(this.searchButton);
		this.westPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		this.westPanel.add(new JLabel("Old Results:"));
		this.westPanel.add(this.oldSearchBox);
		this.westPanel.add(this.cancelButton);
		
		//Main Panel
		//super.add(this.forceUpdateButton, BorderLayout.SOUTH);
		super.add(this.westPanel, BorderLayout.WEST);
	}
	
	/**
	 * Initializes the center layout.
	 */
	private void noSearchLayout () {
		this.centerPanel = new JPanel(new BorderLayout());
		JButton pleaseSearchButton = new JButton ("Please initizalize a search.");
		pleaseSearchButton.setEnabled(false);
		this.centerPanel.add(pleaseSearchButton);
		super.add(this.centerPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Creates and shows result table according to the search id.
	 * 
	 * @param id ID of the search to be shown on screen.
	 */
	private void createResultTable(Long id, int selectedRow) {
		if (this.idInUse.longValue() == -1) {
			this.noSearchLayout();
		}
		else {
			Map idMap = replyManager.getSearchResult();
			Map resultMap = (Map) idMap.get(id);
			this.centerPanel.removeAll();
			
			if (resultMap != null) {
				Vector data = new Vector();
				resultTable = new ResultTable(data,selectedRow);
				
				//Lines bellow use to increase beauty of the table in some way.
				TableColumn column = resultTable.getColumn("Seeds");
				column.setMaxWidth(42);
				
				Set hashSet = resultMap.keySet();
				Iterator hashIterator = hashSet.iterator();
				while(hashIterator.hasNext()) {
					String fileHash = (String)hashIterator.next();
					FileInfo fileInfo;
					String seeds;
					try {
						fileInfo = replyManager.getFileInfo(idInUse.longValue(), fileHash);
						seeds = Integer.toString(replyManager.getProviders(idInUse.longValue(), fileHash).size());
						String fileName = fileInfo.getFileName();
						String fileSize = Long.toString(fileInfo.getSize())+"Kb";
						resultTable.addRow(seeds, fileName, fileSize, fileHash);
					} catch (RequestIDDoesNotExistException e) {
						// do nothing
					} catch (HashDoesNotExistException e) {
						// do nothing
					}
				}
				JScrollPane resultsScroller = new JScrollPane(resultTable);
				this.centerPanel.add(resultsScroller, BorderLayout.CENTER);
			}
			else {
			    Vector data = new Vector();
				resultTable = new ResultTable(data);
				
				//Lines bellow use to increase beauty of the table in some way.
				TableColumn column = resultTable.getColumn("Seeds");
				column.setMaxWidth(42);
				
				JScrollPane resultsScroller = new JScrollPane(resultTable);
				this.centerPanel.add(resultsScroller, BorderLayout.CENTER);
				/*JButton noResults = new JButton ("There were no results so far.");
				noResults.setEnabled(false);
				this.centerPanel.add(noResults);
				super.add(this.centerPanel, BorderLayout.CENTER);*/
			}
			
			//Adds to main panel
			super.add(this.centerPanel, BorderLayout.CENTER);
			super.validate();
		}
	}
	
	/**
	 * Initializes components and attributes for new search.
	 */
	private void initializeID () {
		Set idMap = (Set)replyManager.getSearchResult().keySet();
		Iterator idIterator = idMap.iterator(); 
		while (idIterator.hasNext()) {
			Long id = (Long)idIterator.next();
			if (!this.resultsIds.contains(id)) {
				this.resultsIds.add(id);
			}
		}
		this.idInUse = (Long)this.resultsIds.getLast();
		this.oldSearchBox.addItem(idInUse);
		this.oldSearchBox.setSelectedItem(idInUse);
		this.oldSearchBox.validate();
		this.createResultTable(idInUse,-1);
	}
	
	/**
	 * Removes a result.
	 */
	private void removeResult() {
		this.resultsIds.remove(this.idInUse);
		if (this.oldSearchBox.getItemCount() == 1) {
			this.idInUse = new Long(-1);
			this.oldSearchBox.removeAllItems();
			this.noSearchLayout();
		}
		else {
			this.oldSearchBox.removeItem(this.idInUse);
		}
		this.oldSearchBox.validate();
	}
	
	/**
	 * Used so that main window can refresh the panel.
	 */
	public synchronized void refresh() {
		if(this.idInUse.longValue() != -1) {
			this.resultTable.oldSelection();
			this.createResultTable(idInUse,this.resultTable.getSelectedRow());
			this.validate();
		}
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.searchButton || e.getSource() == this.searchInputField) {
			if (this.searchInputField.getText() != null) {
				String searchKey = this.searchInputField.getText().trim();
				this.parentListener.actionPerformed(new ActionEvent(new SearchEventInformation(this.searchInputField.getText(), SearchEventInformation.NEW_SEARCH), 1, "search"));
				this.initializeID();
			}
		}
		if (e.getSource() == this.cancelButton) {
			this.removeResult();
		}

	}
	
	/**
	 * Table used to show result information. 
	 */
	private class ResultTable extends JTable {
		
		/**
		 * Vector used to store information that is used in the Table.
		 */
		private Vector data;
		
		/**
		 * Creates new <code>ResultTable</code> with the given data.
		 * 
		 * @param data
		 */
		public ResultTable(Vector data) {
			super(data, new Vector(Arrays.asList(new String[] {"Seeds", "File Name", "File Size", "Hash"})));
			this.data = data;
			this.setRowSelectionAllowed(true);
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e){
					if (e.getClickCount() == 2){
						JTable table = (JTable)e.getComponent();
						int row = table.getSelectedRow();					
						String hash = (String)table.getValueAt(row,FILE_HASH);
						String name = (String)table.getValueAt(row,FILE_NAME);
						parentListener.actionPerformed(new ActionEvent(new DownloadEventInformation(hash,idInUse,(String)table.getValueAt(row,FILE_NAME)),1,"download"));//Put the hash and the search
						JOptionPane.showMessageDialog(null, "Download for file: "+name+"\n"+
							"Checksum: "+hash+"\n\nIs now on download manager.");
					}
				}
			});
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.repaint();
		}
		
		public ResultTable(Vector data, final int _selectedRow) {
			super(data, new Vector(Arrays.asList(new String[] {"Seeds", "File Name", "File Size", "Hash"})));
			this.data = data;
			selectedRow = _selectedRow;
			this.setRowSelectionAllowed(true);
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e){
					if (e.getClickCount() == 2){
						JTable table = (ResultTable)e.getComponent();
						int row = table.getSelectedRow();					
						String hash = (String)table.getValueAt(row,FILE_HASH);
						String name = (String)table.getValueAt(row,FILE_NAME);
						parentListener.actionPerformed(new ActionEvent(new DownloadEventInformation(hash,idInUse,(String)table.getValueAt(row,FILE_NAME)),1,"download"));//Put the hash and the search
						JOptionPane.showMessageDialog(null, "Download for file: "+name+"\n"+
							"Checksum: "+hash+"\n\nIs now on download manager, please resume it");
					}
					if (e.getClickCount() == 1) {
					    ResultTable table = (ResultTable)e.getComponent();
					    selectedRow = table.getSelectedRow();
					}
				}
			});
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.repaint();
		}

		public void oldSelection() {
			selectedRow = super.getSelectedRow();
		}
		

		/**
		 * Adds a new row cotaining result information.
		 * 
		 * @param seeds
		 * @param fileName
		 * @param fileSize
		 * @param hash
		 */
		public void addRow(String seeds, String fileName, String fileSize, String hash) {
			Vector newRow = new Vector();
			String newRowInfo[] = new String[] {seeds, fileName, fileSize, hash}; 
			newRow.addAll(Arrays.asList(newRowInfo));
			data.add(newRow);
			super.validate();
			super.repaint();
		}
		
		/**
		 * Always returns false so <code>JTable</code> is not editable.
		 */
		public boolean isCellEditable(int row,int col) {
			return false;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.JTable#getSelectedRow()
		 */
		public int getSelectedRow() {
			return selectedRow;
		}
	}
}