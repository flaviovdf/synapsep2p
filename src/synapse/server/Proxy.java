package synapse.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.OperationNotSupportedException;
import synapse.common.Provider;
import synapse.common.URLProvider;

/**
 * The class that represents the clients in this server. All the requests
 * are made here.
 * 
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class Proxy extends UnicastRemoteObject implements Provider {

    /**
     * Facade to be contacted by this proxy.
     */
    private ServerFacade facade;
    
    /**
     * Used for logging events and messages.
     */
    private Logger logger;
    
    /**
     * Creates a new Proxy.
     * 
     * @param facade
     * @throws RemoteException
     */
    public Proxy(ServerFacade facade) throws RemoteException {
        this.facade = facade;
        this.logger = Logger.getInstance();
    }

    public void shutdown() {

        try {
            Naming.unbind(URLProvider.Proxy());
        } catch (Exception e) {
            logger.exception(getClass().getName() + ".shutdown()", e);
        }
        logger.info(getClass().getName() + ".shutdown()", "Proxy was successfully unbinded.");
    }
    
    /**
     * Generates a random id.
     * 
     * @return The ID.
     */
    private long getNextId() {
        return (long) (Math.random() * Long.MAX_VALUE) + 1;
    }
    
    /* (non-Javadoc)
     * @see synapse.server.Provider#identify(synapse.common.Provider)
     */
    public void identify(Provider provider) throws RemoteException {
    	this.facade.identify(provider);
    }
    
    /* (non-Javadoc)
     * @see synapse.server.Provider#searchFile(synapse.common.Consumer, java.lang.String)
     */
    public long searchFile(Consumer consumer, String fileName) throws RemoteException {
        long id = this.getNextId();
        this.facade.searchFile(id, consumer, fileName);

        return id;
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(long, synapse.common.Consumer, java.lang.String)
     */
    public void searchFile(long id, Consumer consumer, String fileName) throws OperationNotSupportedException {
    	throw new OperationNotSupportedException();
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
		this.facade.searchForCommunity(id, consumer, fileName);
	}

    /* (non-Javadoc)
     * @see synapse.common.Provider#getFile(java.lang.String)
     */
    public void getFile(String hash, Consumer consumer) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

}