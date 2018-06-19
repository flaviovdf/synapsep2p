package synapse.common;

import java.rmi.Remote;
import java.rmi.RemoteException;




/**
 * This remote interface should be implemented by classes that want to receive files.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface Consumer extends Remote {

    /**
     * This method is invoked when a file is found.
     * It is not possible to download the file from this method, it's just to index the search results.
     * 
     * @param id The id of the search.
     * @param provider The file owner.
     * @param fileInfo The file info.
     * @throws RemoteException It's thrown when the consumer can't be contacted.
     */
    public void fileWasFound(long id, Provider provider, FileInfo fileInfo) throws RemoteException;

    /**
     * It's possible to download the file using this method.
     * The <code>TransferPipe</code> is the way used to download the file.
     * 
     * @param pipe A pipe used to download the file. 
     * @throws RemoteException It's thrown when the consumer can't not be contacted.
     */
    public void hereIsFile(TransferPipe pipe) throws RemoteException;

    /**
     * Method that verifies if the consumer is on line.
     * 
     * @throws RemoteException If the the consumer is not on line.
     */
    public void ping() throws RemoteException;

}