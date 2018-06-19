package synapse.client.manager;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import synapse.client.TransferPipeImpl;
import synapse.common.Consumer;

/**
 * Class that represents a manager for the <code>TransferPipe</code>s. It's
 * important to keeps track the <code>TransferPipe</code> s and use its to
 * communicate (see information, ivalidate etc) with the pipes.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 */
public class UploadManager {

	private LinkedList runningUploads, waitQueue;
	public static final int  MAX_UPLOADS_RUNNING = 5;
	private static UploadManager uniqueInstance;

	/**
	 * Constructs a new UploadManager that contains an ArrayList of
	 * <code>TransferPipe</code>s.
	 */
	private UploadManager() {
		this.runningUploads = new LinkedList();
		this.waitQueue = new LinkedList();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {

				Iterator it = new LinkedList(runningUploads).iterator();
				while (it.hasNext()) {
				    Upload upload = (Upload) it.next();
					try {
						upload.consumer.ping();
					} catch (RemoteException e) {
						upload.pipe.invalidate();
						
						runningUploads.remove(upload);
						if (!waitQueue.isEmpty()) {
							Upload aux = (Upload) waitQueue.removeFirst();
							deliverFile(aux.consumer, aux.pipe);
						}
					}
				}

			}

		}, 60000, 60000);
	}
	
	/**
	 * The class is a singleton. This method return the unique object os this class.
	 * @return The unique object of this class.
	 */
	public static UploadManager getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new UploadManager();
		}
		return uniqueInstance;
	}
	
	/**
	 * Make the unique object os this class null.
	 */
	public static void reset() {
		uniqueInstance = null;
	}

	/**
	 * Method that adds a new upLoad in this <code>UploadManager</code>. An
	 * <code>Upload</code> contains a fileName and a <code>TransferPipe</code>.
	 * 
	 * @param consumer The consumer that will receive the file.
	 * @param pipe The <code>Upload</code> object.
	 */
	public synchronized void deliverFile(Consumer consumer, TransferPipeImpl pipe) {
	    
		Upload up =  new Upload(consumer, pipe);
		
	    if (this.runningUploads.size() < UploadManager.MAX_UPLOADS_RUNNING) {
	    	try {
				up.consumer.hereIsFile(pipe);
				this.runningUploads.add(up);
			} catch (RemoteException e) {
			}
	    }
		else {
			this.waitQueue.add(up);
		}
	}
	
	/**
	 * Method that returns all the uploads that are running in this upload.
	 * @return A list that represents all the uploads that are running in this Uploadmanager.
	 */
	public synchronized List getRunningUploads() {
		return new LinkedList(this.runningUploads);
	}

	/**
	 * Method that returns all the uploads that are waiting to be processed.
	 * @return A list that represents all the uploads that are waiting to be processed.
	 */
	public synchronized List getWaitQueue() {
		return new LinkedList(this.waitQueue);
	}	
	
	/**
	 * Method that return all the <code>TransferPipe</code>s in this
	 * <code>UpdateManager</code>.
	 * 
	 * @return A list containing all of the pipes in this UpdateManager.
	 */
	public List getAllUploads() {
		List list = new LinkedList(); 

		list.addAll(this.getRunningUploads());
		list.addAll(this.getWaitQueue());

		return list;
	}

	/**
	 * Returns all <code>TransferPipe</code>s in control.
	 * 
	 * @return The list of pipes.
	 */
	public List getAllTransferPipes() {
		List transfers = new LinkedList();
		
		Iterator it = this.getAllUploads().iterator();
		while (it.hasNext()) {
			Upload upload = (Upload) it.next();
			
			transfers.add(upload.pipe);
		}
		
		return transfers;
	}

	/**
	 * Method that invalidate the <code>TransferPipe</code> existing with the
	 * specified hash in this <code>UploadManager</code>.
	 * 
	 * @param hash The hash of the file to be canceled.
	 * @throws HashDoesNotExistException If the specified hash does not exist.
	 */
	public synchronized void invalidate(String hash) throws HashDoesNotExistException {
		Iterator it = this.getAllUploads().iterator();
		Upload up;
		boolean hashExists = false;
		while (it.hasNext()) {
			up = (Upload) it.next();
			try {
				if (up.pipe.getHash().equals(hash)) {
					up.pipe.invalidate();
					this.runningUploads.remove(up);
					if (!this.waitQueue.isEmpty()) {
						Upload aux = (Upload)this.waitQueue.removeFirst();
						this.deliverFile(aux.consumer, aux.pipe);
					}
					hashExists = true;
				}
			} catch (RemoteException e) {}
		}
		
		if (!hashExists) {
			throw new HashDoesNotExistException(hash);
		}
	
	}

}

/**
 * This class encapsulates a <code>Consumer</code> and its <code>TransferPipe</code>.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 */
class Upload {
	
	public TransferPipeImpl pipe;
	public Consumer consumer;
	
	
	public Upload(Consumer consumer, TransferPipeImpl pipe) {
		this.consumer = consumer;
		this.pipe = pipe;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Upload)) {
			return false;
		}	
		Upload aux = (Upload)other;
		return (this.consumer.equals(aux.consumer)) && (this.pipe.equals(aux.pipe));
	}

	public String toString() {
		try {
			return "FileName: " + pipe.getFileName() + " Hash: " + pipe.getHash() + " Size: " + pipe.getSize();
		} catch (RemoteException e) {
			return "(invalid upload)";
		}
	}

}