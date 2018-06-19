package synapse.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.FileInfo;
import synapse.common.OperationNotSupportedException;
import synapse.common.Provider;
import synapse.common.TransferPipe;

/**
 * 
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class Client extends UnicastRemoteObject implements Consumer, Provider {

    private ClientFacade facade;
    
    private Logger logger;
    
    /**
     * Instance of this class used so that other classes can contact it.
     */
    private static Client instance;
    
    /**
     * 
     * 
     * @throws RemoteException
     */
    protected Client(ClientFacade facade) throws RemoteException {
        this.facade = facade;
        this.logger = Logger.getInstance();
        
        logger.info(getClass().getName() + ".config()", "The Client was successfully created.");
    
        Client.instance = this;
    }
    
    /**
     * Gets instance for the <code>Client</code> so other classes can access it.
     * 
     * @return this
     */
    static Client getClientInstance () {
    	return Client.instance;
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
    public void searchFile(long id, Consumer consumer, String fileName) throws RemoteException {
        this.facade.searchFile(id, consumer, fileName);
    }


    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(java.lang.String, synapse.common.Consumer)
     */
    public void searchFile(String hash, Consumer consumer) throws RemoteException {
        this.facade.searchFile(hash, consumer);
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
    public void getFile(String hash, Consumer consumer) throws RemoteException {
        this.facade.getFile(hash, consumer);
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#fileWasFound(synapse.common.Provider, java.lang.String, java.lang.String)
     */
    public void fileWasFound(long id, Provider provider, FileInfo fileInfo) throws RemoteException {
        this.facade.fileWasFound(id, provider, fileInfo);
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#hereIsFile(synapse.common.TransferPipe)
     */
    public void hereIsFile(TransferPipe pipe) throws RemoteException {
        this.facade.hereIsFile(pipe);
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#ping()
     */
    public void ping() throws RemoteException {
    }

}
