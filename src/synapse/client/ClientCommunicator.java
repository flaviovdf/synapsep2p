package synapse.client;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import synapse.client.manager.DownloadManager;
import synapse.client.manager.HashDoesNotExistException;
import synapse.client.manager.ReplyManager;
import synapse.client.manager.RequestIDDoesNotExistException;
import synapse.client.manager.UploadManager;
import synapse.client.ui.ClientNotConnectedException;
import synapse.common.Consumer;
import synapse.common.Provider;

/**
 * All references form this class are shared between the <code>UITranslator</code> 
 * and the  <code>TransferCore</code>.
 * 
 * It Implements <code>ClientCommunicator</code> but it does not interact directly
 * with the user, but objects that do must interact in some way with the Communicator
 * or implement another form of communicating with the <code>TransferCore</code>.
 * 
 * The Communicator takes care of sending commands to the client, in the current architecture
 * only <code>TransferCore</code> can create a <code>ClientCommunicator</code>.
 *
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class ClientCommunicator {

    /**
     * The server to perform the search and get methods.
     */
	private Provider server;

	/**
	 * The consumer that will receive any answer.
	 */
	private Consumer client;

	/**
	 * Creates a new <code>ClientCommunicator</code>.
	 * 
	 * @param server The provider used to perform a new search.
	 * @param client The consumer interested in files.
	 */
	public ClientCommunicator(Provider server, Consumer client) {
		this.server = server;
		this.client = client;
	}

	/**
	 * Submit a search file to the server.
	 * 
	 * @param value The search argument.
	 * @throws ClientNotConnectedException It's thrown if the client is not connected to server.
	 */
	public void search(String value) throws ClientNotConnectedException {
		try {
			long id = this.server.searchFile(client, value);
			ReplyManager.getInstance().addNewSearch(id);
		} catch (RemoteException e) {
			throw new ClientNotConnectedException();
		}
	}

	/**
	 * Submit a search for more resources.
	 * 
	 * @param param The search argument.
	 * @throws ClientNotConnectedException It's thrown if the client is not connected to server.
	 */
    public void searchMore(String param) throws ClientNotConnectedException {
        try {
            this.server.searchFile(param, client);
		} catch (RemoteException e) {
			throw new ClientNotConnectedException();
		}
    }

    /**
     * Invalidates an upload.
     * 
     * @param hash The hash of file to be invalidated.
     * @throws HashDoesNotExistException If the hash does not exist.
     */
    public void invalidatePipe(String hash) throws HashDoesNotExistException {
        UploadManager.getInstance().invalidate(hash);
    }

    /**
     * Returns the results of a previous search.
     * 
     * @return A map which the key is the search id and the value is an other map B.
     * This map B has hashes as keys and a <code>Set</code> containing <code>Reply</code>s as values.
     */
	public Map listResults() {
		return ReplyManager.getInstance().getSearchResult();
	}

	/**
	 * Returns a list with all <code>TransferPipe</code>s sent to other clients.
	 * 
	 * @return A list of <code>TransferPipe</code>s.
	 */
	public List listRunningUploads() {
		return UploadManager.getInstance().getRunningUploads();
	}

	/**
	 * Returns a list with all <code>TransferPipe</code>s sent to other clients.
	 * 
	 * @return A list of <code>TransferPipe</code>s.
	 */
	public List listWaitQueueUploads() {
		return UploadManager.getInstance().getWaitQueue();
	}

		
	/**
	 * Returns a list of all current downloads.
	 * 
	 * @return A list of downloads.
	 */
	public List listDownloads() {
		return DownloadManager.getInstance().getAllDownloads();
	}

	/**
	 * Sends the <code>getFile()</code> command to all providers in results list.
	 * 
	 * @param id The search id.
	 * @param hash The hash of the file to be downloaded.
	 * @throws RequestIDDoesNotExistException If the ID does not exist.
	 * @throws HashDoesNotExistException If the hash does not exist.
	 */
	public void sendGetToProviders(long id, String hash) throws RequestIDDoesNotExistException, HashDoesNotExistException {
	    DownloadManager.getInstance().addSolicitation(hash); // this line HAS to be here!!

	    List providersList = ReplyManager.getInstance().getProviders(id, hash);
		Iterator iterator = providersList.iterator();
		while (iterator.hasNext()) {
			Provider tempProvider = (Provider)iterator.next();
			try {
				tempProvider.getFile(hash, Client.getClientInstance());
			} catch (RemoteException e) {
				// do nothing
			}
		}
	}

    /**
     * Resumes the specified download.
     * 
     * @param hash The file hash.
     * @throws HashDoesNotExistException If the hash does not exist.
     */
    public void resume(String hash) throws HashDoesNotExistException {
        DownloadManager.getInstance().resumeDownload(hash);
    }

    /**
     * Pauses the specified download.
     * 
     * @param hash The file hash.
     * @throws HashDoesNotExistException If the hash does not exist.
     */
    public void pause(String hash) throws HashDoesNotExistException {
        DownloadManager.getInstance().pauseDownload(hash);
    }

    /**
     * Pause all downloads.
     */
    public void pauseAll() {
        DownloadManager.getInstance().pauseAll();
    }

    /**
     * Cancels the specified download.
     * 
     * @param hash The file hash.
     * @throws HashDoesNotExistException If the hash does not exist.
     */
    public void cancel(String hash) throws HashDoesNotExistException {
        DownloadManager.getInstance().cancelDownload(hash);
    }

}