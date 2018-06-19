package synapse.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Provider interface, Classes that implement this interface are responsible in sending
 * synapse.client requests to the synapse.server.
 *
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public interface Provider extends Remote {
	
	/**
	 * Method that identifies a client. It's necessary to allow
	 * the client to search for files.
	 * 
	 * @param provider The client to be identified.
	 * @throws RemoteException It's thrown when the provider can't be contacted.
	 */
	public void identify(Provider provider) throws RemoteException;
    
	/**
     * Sends the search file request to the provider.
     * 
     * @param consumer The consumer that requested the file.
     * @param fileName The name of the file, or substring of its name.
     * @return The ID that represents this request.
     * @throws RemoteException It's thrown when the provider can't be contacted.
     */
    public long searchFile(Consumer consumer, String fileName) throws RemoteException;

    /**
     * Sends to server the search file request with the ID already defined.
	 *
	 * @param id The ID that represents this request.
	 * @param consumer The consumer that requested the file.
     * @param fileName The name of the file, or substring of its name.
     * @throws RemoteException It's thrown when the provider can't be contacted.
     */
    public void searchFile(long id, Consumer consumer, String fileName) throws RemoteException;
    
    public void searchForCommunity(long id, Consumer consumer, String fileName) throws RemoteException;

    /**
     * Sends the search to server informing the hash and who is submitting the search.
     * This method is useful to find more download sources.
     * 
     * @param consumer The consumer to be answered about the search.
     * @param hash The file hash.
     * @throws RemoteException It's thrown when the provider can't be contacted.
     */
    public void searchFile(String hash, Consumer consumer) throws RemoteException;

    /**
     * Sends the Get File request.
	 *
     * @param hash File identification
     * @throws RemoteException It's thrown when the provider can't be contacted.
     */
    public void getFile(String hash, Consumer consumer) throws RemoteException;
}
