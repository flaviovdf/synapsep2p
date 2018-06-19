package synapse.client.ui.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import synapse.client.manager.HashDoesNotExistException;
import synapse.client.manager.UploadManager;
import synapse.common.TransferPipe;

/**
 * Uploads Panel
 */
public class UploadsPanel extends JPanel implements ActionListener, RefreshablePanel {
	
	/**
	 * Cancels the upload.
	 */
	private JButton cancelButton;
	

	/**
	 * Table used to show uploads information.
	 */
	private UploadsTable uploadsTable;
	
	/**
	 * <code>UploadManager</code> manages all uploads and sends information do the panel.
	 */
	private UploadManager uploadManager;
	
	/**
	 * Center panel where the uploads are shown.
	 */
	private JPanel center;
	
	//Constants
	private final int FILE_NAME = 0;
	private final int FILE_SIZE = 1;
	private final int SPEED = 2;
	private final int FILE_HASH = 3;
	
	
	/**
	 * Create a new <code>UploadsPanel</code>.
	 */
	public UploadsPanel() {
		super();
		super.setLayout(new BorderLayout());
		super.setVisible(true);
		this.uploadManager = UploadManager.getInstance();
		
		this.initializeComponents();
	}
	
	/**
	 * Initializes the Swing components.
	 */
	private void initializeComponents() {
		this.center = new JPanel(new BorderLayout());
		
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.setToolTipText("cancels selected upload");
		this.cancelButton.addActionListener(this);
		
		//Adds the components to the Frame
		JPanel south = new JPanel();
		south.add(this.cancelButton);
		super.add(south,BorderLayout.SOUTH);
		this.createUploadsTable();
	}
	
	/**
	 * Refresh the information shown.
	 */
	private void createUploadsTable() {
		this.center.removeAll();
		this.uploadsTable = new UploadsTable(new Vector());
		JScrollPane scrollUploads = new JScrollPane(this.uploadsTable);
		this.uploadsTable.oldSelection();
		
		Collection uploads = this.uploadManager.getAllTransferPipes();
		Iterator pipesIterator = uploads.iterator();
		while (pipesIterator.hasNext()) {
			TransferPipe pipe = (TransferPipe)pipesIterator.next();
			try {
				this.uploadsTable.addRow(pipe.getFileName(), pipe.getSize(), pipe.getHash());
			} catch (RemoteException e) {
				// do nothing
			}
		}
		
		this.center.add(scrollUploads, BorderLayout.CENTER);
		
		super.add(center, BorderLayout.CENTER);
		super.validate();
	}
	
	/**
	 * Used so that main window can refresh the panel.
	 */
	public void refresh() {
		this.createUploadsTable();
		this.uploadsTable.oldSelection();
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (this.uploadsTable.getSelectedRow() == -1) {
			return;
		}
		else {	
			int row = this.uploadsTable.getSelectedRow();
			String hash = (String)uploadsTable.getValueAt(row,this.FILE_HASH);
			if (e.getSource() == this.cancelButton) {
				try {
					this.uploadManager.invalidate(hash);
					this.uploadsTable.delRow(row);
				} catch (HashDoesNotExistException e1) {
					// do nothing
				}
			}
		}
	}
	
	/**
	 * Table that represents the uploads. 
	 */
	private class UploadsTable extends JTable {
		
		/**
		 * Vector used to store information that is used in the Table.
		 */
		private Vector uploads;
		private int selectedRow;
		
		/**
		 * Create a new Table.
		 * 
		 * @param uploads
		 */
		public UploadsTable(Vector uploads){
			super(uploads, new Vector(Arrays.asList(new Object[] {"File Name", "File Size", "Speed", "Hash"})));
			this.uploads = uploads;
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		
		/**
		 * Adds a new download to the table
		 * 
		 * @param fileName
		 * @param fileSize
		 * @param hash
		 */
		public void addRow(String fileName, long fileSize, String hash) {
			Vector newRow = new Vector();
			String speed = "0 Kb/s";
			Object newRowInfo[]= new Object[] {fileName, fileSize/1024 +" Kb", speed, hash};
			newRow.addAll(Arrays.asList(newRowInfo));
			uploads.add(newRow);
			super.validate();
		}
		
		/**
		 * Removes a row from the table.
		 */
		public void delRow(int row) {
			this.uploads.remove(row);
			super.validate();
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
}