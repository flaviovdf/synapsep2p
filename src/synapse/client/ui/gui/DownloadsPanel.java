package synapse.client.ui.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import synapse.client.manager.AbstractDownload;
import synapse.client.manager.DownloadManager;
import synapse.client.manager.HashDoesNotExistException;

/**
 * Downloads Panel
 */
public class DownloadsPanel extends JPanel implements ActionListener, RefreshablePanel {
	
	/**
	 * Button used to pause a download.
	 */
	private JButton pauseButton;
	
	/**
	 * Button used to resume a paused download.
	 */
	private JButton resumeButton; 
	
	/**
	 * Button used to cancel a download.
	 */
	private JButton killButton;
	
	/**
	 * Button used to find more sources.
	 */
	private JButton moreButton;
	
	
	/**
	 * Table that displays download information.
	 */
	private DownloadsTable downloadsTable;
	
	/**
	 * <code>DownloadManager</code> manages downloads and sends information to the panel.
	 */
	private DownloadManager downloadManager;
	
	/**
	 * Center panel where the download table is shown.
	 */
	private JPanel center;
	
	/**
	 * Listener from parent, used to send parent some events.
	 */
	private ActionListener parentListener;
	
	//Constants
	private final int SEEDS = 0;
	private final int FILE_NAME = 1;
	private final int FILE_SIZE = 2;
	private final int SPEED = 3;
	private final int PROGRESS = 4;
	private final int FILE_HASH = 5;
	
	
	/**
	 * Create a new <code>DownloadsPanel</code>.
	 */
	public DownloadsPanel(ActionListener parentListener) {
		super();
		super.setLayout(new BorderLayout());
		super.setVisible(true);
		this.parentListener = parentListener;
		this.downloadManager = DownloadManager.getInstance();
		
		this.initializeComponents();
	}
	
	/**
	 * Initializes the Swing components.
	 */
	private void initializeComponents() {
		center = new JPanel(new BorderLayout());
		
		
		this.pauseButton = new JButton("Pause");
		this.pauseButton.addActionListener(this);
		this.pauseButton.setToolTipText("Pauses the selected download.");
		
		this.resumeButton = new JButton("Resume");
		this.resumeButton.addActionListener(this);
		this.resumeButton.setToolTipText("Resumes your stopped downloads.");
		
		this.killButton = new JButton("Kill Download");
		this.killButton.addActionListener(this);
		this.killButton.setToolTipText("Kills the selected download.");
		
		this.moreButton = new JButton("More Sources");
		this.moreButton.addActionListener(this);
		this.moreButton.setToolTipText("Find more sources for selected download.");
		
		//Adds the components to the Frame
		JPanel south = new JPanel();
		south.add(this.pauseButton);
		south.add(this.resumeButton);
		south.add(this.killButton);
		super.add(south,BorderLayout.SOUTH);
		super.repaint();
		this.createDownloadTable();
	}
	
	/**
	 * Refresh the information shown.
	 */
	private void createDownloadTable() {
		this.downloadsTable = new DownloadsTable(new Vector());
		JScrollPane scrollDownloads = new JScrollPane(this.downloadsTable);
		this.downloadsTable.oldSelection();
		this.center.removeAll();
		
		List downloadsList = this.downloadManager.getAllDownloads();
		Iterator iterator = downloadsList.iterator();
		while(iterator.hasNext()) {
			AbstractDownload download = (AbstractDownload) iterator.next();
			if(!this.downloadsTable.containsDownload(download)) {
				try {
					float speed = DownloadManager.getInstance().getSpeed(download.getHash());
					this.downloadsTable.addRow(download.getNumberOfTransfers(),download.getFileName(),download.getSize(),speed, download.getDownloadProgress(),download.getHash());
				}
				catch(Exception e){
					
				}
				
			}

		}
		
		center.add(scrollDownloads, BorderLayout.CENTER);
		super.add(center, BorderLayout.CENTER);
		super.validate();		
	}
	
	/**
	 * Used so that main window can refresh the panel.
	 */
	public void refresh() {
		this.createDownloadTable();
		this.downloadsTable.oldSelection();
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (this.downloadsTable.getSelectedRow() == -1) {
			return;
		}
		int row = this.downloadsTable.getSelectedRow();
		String hash = (String)downloadsTable.getValueAt(row,this.FILE_HASH);
		if (e.getSource() == this.killButton) {
			try {
				this.downloadManager.cancelDownload(hash);
			} catch (HashDoesNotExistException e1) {
				// do nothing
			}
			this.createDownloadTable();
		}
		else if (e.getSource() == this.resumeButton) {
			try {
				this.downloadManager.resumeDownload(hash);
			} catch (HashDoesNotExistException e1) {
				// do nothing
			}
			this.createDownloadTable();
		}
		else if (e.getSource() == this.pauseButton) {
			try {
				this.downloadManager.pauseDownload(hash);
			} catch (HashDoesNotExistException e1) {
				// do nothing
			}
			this.createDownloadTable();
		}
		else if (e.getSource() == this.moreButton) {
			this.parentListener.actionPerformed(new ActionEvent(new SearchEventInformation(hash, SearchEventInformation.MORE_SOURCES), 1, "search"));
		}
		
	}
	
	/**
	 * Table that represents the downloads. 
	 */
	private class DownloadsTable extends JTable {
		
		/**
		 * Vector used to store information that is used in the Table.
		 */
		private Vector downloads;
		private int selectedRow;
		
		/**
		 * Create a new Table.
		 * 
		 * @param downloads
		 */
		public DownloadsTable(Vector downloads){
			super(downloads, new Vector(Arrays.asList(new Object[] {"Seeds", "File Name", "File Size", "Speed","Status","Hash"})));
			this.downloads = downloads;
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			TableColumn column = this.getColumn("Seeds");
			column.setMaxWidth(50);
			column.setMinWidth(50);
			column = this.getColumn("File Size");
			column.setMaxWidth(70);
			column.setMinWidth(50);
			column = this.getColumn("Speed");
			column.setMaxWidth(70);
			column.setMinWidth(50);
		}
		
		/**
		 * Adds a new download to the table
		 * 
		 * @param seeds
		 * @param fileName
		 * @param fileSize
		 * @param speed
		 * @param progress
		 * @param hash
		 */
		public void addRow(int seeds, String fileName, long fileSize, float speed, float progress, String hash) {
			Vector newRow = new Vector();
			JProgressBar status = new JProgressBar();
			status.setValue((int) progress);
			String downloadSpeed = speed + " KB/s";
			Object newRowInfo[]= new Object[] {new Integer(seeds).toString(), fileName, fileSize/1024 +" KB", downloadSpeed, status, hash}; 
			newRow.addAll(Arrays.asList(newRowInfo));
			downloads.add(newRow);
			TableColumn column = this.getColumn("Status");
			TableCellRenderer renderer = new CellRenderer();
			column.setCellRenderer(renderer);
			status.setStringPainted(true);
			try {
				downloadManager.resumeDownload(hash);
			} catch (HashDoesNotExistException e1) {
				// do nothing
			}
			super.repaint();
		}
		
		/**
		 * Removes a row from the table.
		 * 
		 * @param download
		 */
		public void delRow(AbstractDownload download) {
			downloads.remove(download);
			super.repaint();
		}
		
		/**
		 * Verifies if a download exists in the table.
		 * 
		 * @param download
		 * @return true if exists, false otherwise.
		 */
		public boolean containsDownload(AbstractDownload download) {
			return downloads.contains(download);
		}
		
		/**
		 * Always returns false so <code>JTable</code> is not editable.
		 */
		public boolean isCellEditable(int row,int col) {
			return false;
		}
		
		public void oldSelection() {
			this.selectedRow = super.getSelectedRow();
		}
		
		public int getSelectedRow() {
			return this.selectedRow;
		}
	}
	
	/**
	 * Renderer to show components in table. 
	 */
	private class CellRenderer implements TableCellRenderer {
		public CellRenderer(){
			
		}
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        return (JProgressBar)value;
	    }
	        
	}
}