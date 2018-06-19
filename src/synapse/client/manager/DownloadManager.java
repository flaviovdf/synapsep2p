package synapse.client.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.ourgrid.yal.Logger;

import synapse.client.ClientConfig;
import synapse.client.InvalidTransferPipeException;
import synapse.common.TransferPipe;

/**
 * A <code>DownloadManager</code> controls and manages <code>Download</code> instances.
 * It has as function:
 *  -manage persistence
 *  -create instances of  <code>Download</code>
 *  -to provide information about Downloads
 *  -to provide a way to control Download  
 *  This object follow the Singleton pattern 
 * @author <p>Thiago Emmanuel, thiago.manel@gmail.com</p>
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 */

public class DownloadManager implements Observer {
		
	private List downloadsArray;
	private final String FILE_NAME = "downloads.dat";
	private static DownloadManager uniqueInstance;
	private Logger logger;
	private ArrayList solicitesHash;
	
	/**
	 * Constructs a new DownloadManager 
	 */
	private DownloadManager() {		
		logger = Logger.getInstance();
		this.downloadsArray = new ArrayList();
		solicitesHash = new ArrayList();
		logger.info(getClass().getName() + ".DownloadManager()", "DownloadManager was created.");
		inicialize();
	}
	
	/**
	 * Returns a instance of <code>DownloadManager</code> object.
	 * The <code>DownloadManaganer</code> class follow the Singleton pattern
	 * @return a instance of <code>DownloadManager</code> object.
	 */
	public static DownloadManager getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DownloadManager();
		}
		
		return uniqueInstance;
	}

	/**
	 * This a test method
	 *
	 */
	public static void reset() {
		uniqueInstance = null;
	}

	/**
	 * The addSolicitation method comes a way of reject unnecessary resources.
	 * For example, see this scene: After a call to getFile </code>TransfeCore</code>'s method, 
	 * probably will receive <code>TransferPipe</code>s at any moment. But if the aplication 
	 * cancel the especified download (by action of the user, perhaps), don't know if another
	 *  <code>TransferPipe</code> receives after download's cancellation must be added 
	 *  in <code>DownloadManager</code>. 
	 * @param hash the hash associate to resource that will transfered
	 */
	public synchronized void addSolicitation(String hash){
		if(!solicitesHash.contains(hash)){
			solicitesHash.add(hash);
			logger.info(getClass().getName() + ".addSolicitation()", "Adding a new Solicitation to hash:" +hash);
		}
	}
	
	/**
	 * 
	 * @param hash
	 */
	private void removeSolicitation(String hash){
		logger.info(getClass().getName() + ".removeSolicitation()", "Removing the Solicitation with the hash:" +hash);
		solicitesHash.remove(hash);
	}

	/**
	 * Get the Download especified with Hash.
	 * @param hash The file hash.
	 * @return Download The <code>Download</code> especified with Hash
	 * @throws HashDoesNotExistException Throws if doesn't exist a <code>Download</code> specified with hash.
	 */		
	protected synchronized AbstractDownload getSelectedDownload(String hash) throws HashDoesNotExistException   {
		//possivel mudanca de visibilidade, passara a ser protected
	    AbstractDownload auxDown;
	    Iterator itera = this.downloadsArray.iterator();
		while (itera.hasNext()) {
			auxDown = (AbstractDownload)itera.next();
			if (auxDown.getHash().equals(hash))
				return auxDown;
		}
		throw new HashDoesNotExistException(hash);
	}

	/**
	 * This a test method. Used to test a cancelDownload method
	 * @param hash The hash of looked  <code>Download</code>
	 * @return True if looked download exist, false otherwise
	 */
	protected synchronized boolean contains(String hash){
		
		try {
			this.getSelectedDownload(hash);
			//se esta linha for executada, nao foi lancada a exception, existe 
			//um download com o hash especificado como parametro 
			return true;
		} catch (HashDoesNotExistException e) {
			return false;
		}		
	}
	
	/**
	 * Adds a new <code>Download</code> to this DownloadManager  
	 * @param download The <code>Download</code> to addition  
	 */
	private void addDownload (AbstractDownload download){
		download.addObserver(this);
		this.downloadsArray.add(download);
	}

	/**
	 * Adds a <code>TransferPipe</code> to a <code>Download</code>, if doesn't exist
	 * a <code>Download</code> with the same hash that of transfer parameter, the 
	 * DownloadManager will create a instance of one, and adds the transfer to him.
	 * @param transfer	 A <code>TransferPipe</code> that will be added
	 * @throws InvalidTransferPipeException It's thrown when the <code>TransferPipe</code> throws a <code>RemoteException</code>
	 */
	public synchronized void addTransferPipe (TransferPipe transfer) throws InvalidTransferPipeException {
		logger.info(getClass().getName() + ".addTransferPipe()", "Adding a new Transfer Pipe.");
		AbstractDownload auxDown;	
		try {
			auxDown = this.getSelectedDownload(transfer.getHash());
			auxDown.addTransferPipe(transfer);
            logger.info(getClass().getName() + ".addTransferPipe()", "The TransferPipe was added. This Download, now, has " + auxDown.getNumberOfTransfers() + " Transfers.");
		} catch (HashDoesNotExistException e1) {
			try {
				if(solicitesHash.contains(transfer.getHash())){
					this.addDownload(new SimpleDownload(transfer));	
				}
			} catch (RemoteException e) {
			    throw new InvalidTransferPipeException();
			}
		} catch (RemoteException e) {
		    throw new InvalidTransferPipeException();
		}
	}
	
	/**
	 * Resume a <code>Download</code> especified with hash
	 * @param hash The hash of <code>Download</code> that it will be resumed
	 * @throws HashDoesNotExistException Throws if doesn't a <code>Download</code> with this hash 
	 */
	public synchronized void resumeDownload(String hash) throws HashDoesNotExistException{
		logger.info(getClass().getName() + ".resumeDownload()", "Resuming the Download: "+ hash +".");
		this.getSelectedDownload(hash).resume();
	}
	
	/**
	 * Cancel a download especified with hash.
	 * @param hash The hash of <code>Download</code> that it will be canceled.
	 * @throws HashDoesNotExistException Throws if doesn't a <code>Download</code> with this hash.
	 */
	public synchronized void cancelDownload(String hash) throws HashDoesNotExistException {
		logger.info(getClass().getName() + ".cancelDownload()", "Changing the Download, "+ hash +" status to CANCEL.");
		this.solicitesHash.remove(hash);
		this.getSelectedDownload(hash).cancel();
	}
		
	/**
	 * Pause a download especified with hash
	 * @param hash The hash of <code>Download</code> that it will be paused 
	 * @throws HashDoesNotExistException Throws if doesn't exist a <code>Download</code> with this hash.
	 */
	public synchronized void pauseDownload (String hash) throws HashDoesNotExistException {
		logger.info(getClass().getName() + ".pauseDownload()", "Changing the Download, "+ hash +" status to PAUSED.");
		this.getSelectedDownload(hash).pause();
	}
	
	/**
	 * Print informations of DownloadManager's downloads, if don't exist a <code>Download</code>.
	 * in this DownloadManager will be return a empty String.
	 * @return A information about DownloadManager's downloads.
	 */
	public synchronized List getAllDownloads (){
		return new ArrayList(this.downloadsArray);
	}
	
	/**
	 * Returns a number of DownloadManager's download
	 * @return Number of DownloadManager's download
	 */
	public synchronized int getNumberOfDownloads(){
		return this.getAllDownloads().size();
	}

	/**
	 * Informs the download speed.
	 * 
	 * @param hash The hash of file.
	 * @return A float number representing the speed.
	 * @throws HashDoesNotExistException If the hash doesn't exist.
	 */
	public float getSpeed(String hash) throws HashDoesNotExistException {
	    return this.getSelectedDownload(hash).getSpeed();
	}

	/**
	 * Pause all downloads 
	 */
	public synchronized void pauseAll(){
		logger.info(getClass().getName() + ".pauseAll()", "Changing all the Download's status to PAUSED.");
	  	Iterator apontador = downloadsArray.listIterator();
	  	while(apontador.hasNext()){
	  		((AbstractDownload)apontador.next()).pause();
	  	}
	 }
					
	/**
	 * Inicialize the DownloadManager in a consistent form 
	 */
	private void inicialize(){
		
		logger.info(getClass().getName() + ".inicialize()", "Looking for Downloads safeds in hd.");
	  	File file = new File(ClientConfig.getRootDir() + File.separator + FILE_NAME);
	  	ObjectInputStream input = null;
	  	Object[] fileInput;
	  	
	  	if (file.exists()){
	  		try {
	  			//cria o input
				input = new ObjectInputStream(new FileInputStream(file));
				
				//le a lista de downloads armazenada no arquivo
				fileInput = (Object[])input.readObject();
				input.close();
				//adiciona os objetos lidos no DownloadManager
				for (int i = 0; i < fileInput.length; i++){
					addDownload((AbstractDownload)fileInput[i]);
					((AbstractDownload)fileInput[i]).prepareInstance();
					addSolicitation(((AbstractDownload)fileInput[i]).getHash());
					
				}
				logger.info(getClass().getName() + ".inicialize()", fileInput.length +" Downloads added successfully.");
				file.delete();
			} catch (FileNotFoundException e) {
				logger.exception(getClass().getName() + ".inicialize()", e);
			} catch (IOException e) {
				logger.exception(getClass().getName() + ".inicialize()", e);
			}catch (ClassNotFoundException e1) {
				logger.exception(getClass().getName() + ".inicialize()", e1);
			}
	  	}
	  	
	  }
		
	/**
	 * Serializes and save the DownloadManager's downloads. The downloads
	 * will be save at only one archive. In the next instance of DownloadManager
	 * the download will be in a consistent state.
	 */
	public void close(){
		logger.info(getClass().getName() + ".close()", "Closing the DownloadManager.");
		
		pauseAll();
	  	
	  	File file = new File(ClientConfig.getRootDir() + File.separator + FILE_NAME);
	
	  	try {
	  		//cria um novo arquivo.
			file.createNewFile();
		} catch (IOException e2) {
			logger.exception(getClass().getName() + ".close()", e2);
		}
		
		//cria o object que seja armazenado
		Object[] fileOutput = null;
		synchronized (downloadsArray) {
		    fileOutput = downloadsArray.toArray();
		}
	  	//COMO FACO PRA NAO PRECISAR INICIALIZAR COMO NULL?
	  	ObjectOutputStream output = null;
	  	
	  	try {
	  		output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(fileOutput);
		} catch (FileNotFoundException e) {
			logger.exception(getClass().getName() + ".close()", e);
		} catch (IOException e) {
			logger.exception(getClass().getName() + ".close()", e);
		}
	
		finally{
			try {
				if(output != null)
					output.close();
			} catch (IOException e1) {
				logger.exception(getClass().getName() + ".close()", e1);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public synchronized void update(Observable o, Object arg) {
		
		AbstractDownload download = (AbstractDownload)o;
		logger.info(getClass().getName() + ".update()", "The method was called by: "+ download.getHash());
		
		switch(download.getStatus()){

	    	case AbstractDownload.DOWNLOADING: 
	        break;
	
	    	case AbstractDownload.PAUSED:
	    		//decisao a ser tomada: serializa o download ou deixa-o vivo	    		
	    	break;
		
	    	case AbstractDownload.COMPLETED:
	    	    this.removeSolicitation(download.getHash());
	    		downloadsArray.remove(download);
	    	break;
		
	    	case AbstractDownload.ERROR:
	    	    this.removeSolicitation(download.getHash());
	    		downloadsArray.remove(download);
	    		break;
		
	    	case AbstractDownload.CANCELED:
	    	    this.removeSolicitation(download.getHash());
	    		downloadsArray.remove(download);
	    	break;
		  
	    	case AbstractDownload.NOTRANSFERS:          
		  	break;
	    	
	    	case AbstractDownload.SEARCHING:			  	
			  	// Mandar o TransferCore procurar mais fontes pelo hash, q vc obtera AQUI pelo parametro do update 
		  	break;
	    	
	    	default:		  	
		}
	}
}