package synapse.client;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import org.ourgrid.yal.Logger;

import synapse.client.manager.DownloadManager;
import synapse.client.manager.ReplyManager;
import synapse.client.manager.UploadManager;
import synapse.common.Consumer;
import synapse.common.FileInfo;
import synapse.common.OperationNotSupportedException;
import synapse.common.Provider;
import synapse.common.TransferPipe;

/**
 * Manages the transfer conections.
 * 
 * @author <p>Felipe Ribeiro, felipern@lcc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius, flaviov@lcc.ufcg.edu.br</p>
 */
public class TransferCore implements Consumer, Provider {

	/**
	 * Manages the replyManager obtained by the executed query.
	 */
	private ReplyManager replyManager;
	
	/**
	 * Manages the Upload requests that are received.
	 */
	private UploadManager uploadManager;
	
	/**
	 * Manages de Downloads that are being received.
	 */
	private DownloadManager downloadManager;
	
	/**
	 * Source for shared files.
	 */
	private FileSource fileSource;

	/**
	 * The Provider given to other clients.
	 */
	private Provider client;
	
	/**
	 * The logger.
	 */
	private Logger logger;

	/**
	 * Creates a TransferCore.
	 */
	public TransferCore () {
		this(ReplyManager.getInstance(), UploadManager.getInstance(), DownloadManager.getInstance(), new FileSource());

		int numberOfFiles = this.fileSource.loadFiles(ClientConfig.getSharedFolder());
		logger.debug(getClass().getName() + ".TransferCore()", "The FileSource was loaded with " + numberOfFiles + " files.");
	}
	
	/**
	 * Protected constructor used in tests.
	 * 
	 * @param replyManager
	 * @param uploadManager
	 * @param downManager
	 * @param fileSource
	 */
	protected TransferCore (ReplyManager replyManager, UploadManager uploadManager, DownloadManager downManager, FileSource fileSource) {
		this.replyManager = replyManager;
		this.uploadManager = uploadManager;
		this.downloadManager = downManager;
		this.fileSource = fileSource;
		this.logger = Logger.getInstance();
	}

	public void config(Provider client) {
	    this.client = client;
	}

	/* (non-Javadoc)
     * @see synapse.common.Provider#identify(synapse.common.Provider)
     */
    public void identify(Provider provider) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(synapse.common.Consumer, java.lang.String)
     */
    public long searchFile(Consumer consumer, String fileName) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(long, synapse.common.Consumer, java.lang.String)
     */
    public void searchFile(long id, Consumer consumer, String fileName) {
        logger.info(getClass().getName() + ".searchFile()", "The id <" + id + "> asked for the argument: " + fileName + ". Starting the local search...");

    	Collection sources = this.fileSource.searchForResources(fileName);
    	Iterator srcIterator = sources.iterator();
		try {
	    	while (srcIterator.hasNext()) {
	    		FileInfo src = (FileInfo) srcIterator.next();
	    		consumer.fileWasFound(id, this.client, src);
	    	}
	    	logger.info(getClass().getName() + ".searchFile()", "The search was finished and returned " + sources.size() + " files.");
		} catch (RemoteException e) {
		    logger.info(getClass().getName() + ".searchFile()", "The search with id <" + id + "> was canceled because the consumer wasn't connected.");
		}
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(java.lang.String, synapse.common.Consumer)
     */
    public void searchFile(String hash, Consumer consumer) {
        logger.info(getClass().getName() + ".searchFile()", "The consumer " + consumer + " needs more resources with hash " + hash + ". Starting the local search...");

        FileInfo src = this.fileSource.getFileInfo(hash);
        if (src != null) {
            try {
	            TransferPipeImpl pipe = new TransferPipeImpl(src);
	
	            this.uploadManager.deliverFile(consumer, pipe);
	            
	            logger.info(getClass().getName() + ".searchFile()", "The file " + pipe.getFileName() + " was sent to consumer " + consumer);
            } catch (RemoteException e) {
                logger.error(getClass().getName() + ".getFile()", "Something wrong happened with pipe.");
            }
        }
    }

	/* (non-Javadoc)
	 * @see synapse.common.Provider#searchForCommunity(long, synapse.common.Consumer, java.lang.String)
	 */
	public void searchForCommunity(long id, Consumer consumer, String fileName) throws RemoteException {
		throw new OperationNotSupportedException();
	}

    /* (non-Javadoc)
     * @see synapse.common.Provider#getFile(java.lang.String)
     */
    public void getFile(String hash, Consumer consumer) {
        if (fileSource.getFileInfo(hash) != null) {
            try {
		    	TransferPipeImpl pipe = new TransferPipeImpl(fileSource.getFileInfo(hash));
		
	            this.uploadManager.deliverFile(consumer, pipe);
	            
		    	logger.info(getClass().getName() + ".getFile()", "The file " + pipe.getFileName() + " was sent to consumer " + consumer);
            } catch (RemoteException e) {
                logger.error(getClass().getName() + ".getFile()", "Something wrong happened with pipe.");
            }
        }
        else {
            logger.error(getClass().getName() + ".getFile()", "The consumer " + consumer + " asked for an inexistent hash and the answer was canceled.");
        }
    }

	/* (non-Javadoc)
     * @see synapse.common.Consumer#fileWasFound(synapse.common.Provider, java.lang.String, java.lang.String)
     */
    public void fileWasFound(long id, Provider provider, FileInfo fileInfo) {
        logger.info(getClass().getName() + ".fileWasFound()", "The search id <" + id + "> received a file <" + fileInfo.getFileName() + "> as result.");

    	replyManager.addReply(id, provider, fileInfo);
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#hereIsFile(synapse.common.TransferPipe)
     */
    public void hereIsFile(TransferPipe pipe) {
        try {
            this.downloadManager.addTransferPipe(pipe);
            logger.info(getClass().getName() + ".hereIsFile()", "The file " + pipe.getFileName() + " was received.");
        } catch (InvalidTransferPipeException e) {
            logger.error(getClass().getName() + ".hereIsFile()", "The received pipe is invalid and won't be added.");
        } catch (RemoteException e) {
            // do nothing
        }
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#ping()
     */
    public void ping() throws OperationNotSupportedException {
    }

}