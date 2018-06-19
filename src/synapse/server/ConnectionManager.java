package synapse.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.OperationNotSupportedException;
import synapse.common.Provider;
import synapse.common.RequestIDAlreadyExistsException;
import synapse.common.URLProvider;
import synapse.server.protocol.TerminalImpl;
import synapse.server.protocol.TerminalServicesImpl;

/**
 * Class that manages all the peer conected to the server.
 * 
 * @author <p>Joao Arthur Brunet Monteiro, jarthur@dsc.ufcg.edu.br</p>
 * @author <p>Flavio Vinicius Diniz de Figueiredo, flaviov@lcc.ufcg.edu.br</p>
 */
public class ConnectionManager implements Provider {
	
	/**
	 * The client that are identified.
	 */
	private Set identifiedClients;

	/**
	 * The request manager for new requests.
	 */
	private RequestManager requestManager;
	
	/**
	 * The terminal used to ask for files to other servers.
	 */
	private TerminalImpl terminal;

	/**
	 * The logger.
	 */
	private Logger logger;

	/**
	 * Constructs a new ConnectionManager.
	 */
	public ConnectionManager() {
		this(new RequestManager());
	}

	/**
	 * Used only by tests.
	 * 
	 * @param reqManager An external <code>RequestManager</code>.
	 */
	protected ConnectionManager(RequestManager reqManager) {
	    this.identifiedClients = new HashSet();
		this.requestManager = reqManager;
		this.logger = Logger.getInstance();
	}

	/**
	 * Configures the manager.
	 * 
	 * @param provider The provider used by another servers that refers to me.
	 */
	public void config(Provider provider) {

		try {
		    this.terminal = new TerminalImpl(provider);
		    Naming.rebind(URLProvider.Terminal(), this.terminal);
		    logger.debug(getClass().getName() + ".config()", "The Terminal was successfullt binded.");

		    new TerminalServicesImpl(this.terminal).config();
		} catch (Exception e) {
		    logger.exception(getClass().getName() + ".config()", e);
		}

	}

    /* (non-Javadoc)
     * @see synapse.server.Provider#identify(synapse.common.Provider)
     */
	public void identify(Provider provider) {
	    logger.info(getClass().getName() + ".identify()", "A new provider was identified.");

		this.identifiedClients.add(provider);
		this.searchOldRequest(provider);
	}

    /* (non-Javadoc)
     * @see synapse.server.Provider#searchFile(synapse.common.Consumer, java.lang.String)
     */
    public long searchFile(Consumer consumer, String fileName) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

	/* (non-Javadoc)
     * @see synapse.server.Provider#searchFile(long, synapse.common.Consumer, java.lang.String)
     */
    public void searchFile(long id, Consumer consumer, String fileName) {

        try {
	        this.createRequest(id, consumer, fileName);

	        /*
	         * Spreads the search.
	         */
	        // ask for file in other servers
	        if ( this.containsClient((Provider) consumer) ) {
	            this.terminal.searchForCommunity(id, consumer, fileName);
	        }

	        // ask for file in the identified clients
	        int count = 0;
			Iterator providerIterator = new HashSet(this.identifiedClients).iterator();
			while (providerIterator.hasNext()) {
				Provider auxProvider = (Provider) providerIterator.next();

				if (! consumer.equals(auxProvider) ) {
					try {
					    auxProvider.searchFile(id, consumer, fileName);
					    count++;
					}
					catch (RemoteException e) {
					    this.identifiedClients.remove(auxProvider);
					}
				}
			}
			
			logger.info(getClass().getName() + ".searchFile()", "The search with <" + id + "> was performed to " + count + " local providers.");
        } catch (ClientNotIdentifiedException e) {
            logger.error(getClass().getName() + ".searchFile()", "The client " + consumer + " performed a search but wasn't identified.");
        }
	}

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(java.lang.String, synapse.common.Consumer)
     */
    public void searchFile(String hash, Consumer consumer) throws RemoteException {

    	if ( this.identifiedClients.contains(consumer) ) {
	        // ask for file in the identified clients
	        int count = 0;
			Iterator providerIterator = new HashSet(this.identifiedClients).iterator();
			while (providerIterator.hasNext()) {
				Provider auxProvider = (Provider) providerIterator.next();

				if (! consumer.equals(auxProvider) ) {
					try {
					    auxProvider.searchFile(hash, consumer);
					    count++;
					} 
					catch (RemoteException e) {
					    this.identifiedClients.remove(auxProvider);
					}
				}
			}
			
			logger.info(getClass().getName() + ".searchFile()", "The search for <" + hash + "> was performed to " + count + " local providers.");
    	}
    	else {
    		logger.error(getClass().getName() + ".searchFile()", "The client " + consumer + " performed a search but wasn't identified.");
    	}
    }

	/* (non-Javadoc)
	 * @see synapse.common.Provider#searchForCommunity(long, synapse.common.Consumer, java.lang.String)
	 */
	public void searchForCommunity(long id, Consumer consumer, String fileName) throws RemoteException {
        // ask for file in the identified clients
        int count = 0;
		Iterator providerIterator = new HashSet(this.identifiedClients).iterator();
		while (providerIterator.hasNext()) {
			Provider auxProvider = (Provider) providerIterator.next();

			if (! consumer.equals(auxProvider) ) {
				try {
				    auxProvider.searchFile(id, consumer, fileName);
				    count++;
				}
				catch (RemoteException e) {
				    this.identifiedClients.remove(auxProvider);
				}
			}
		}
		
		logger.info(getClass().getName() + ".searchForCommunity()", "The search from community with <" + id + "> was performed to " + count + " local providers.");
	}

    /* (non-Javadoc)
     * @see synapse.common.Provider#getFile(java.lang.String)
     */
    public void getFile(String hash, Consumer consumer) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

	/**
	 * Checks if the consumer is identified and adds the request to the RequestManager.
	 * 
     * @param id Request id.
     * @param consumer Requests consumer.
     * @param fileName Requested File Name.
     * @throws ClientNotIdentifiedException Thrown if the client is not identified
     */
    private void createRequest(long id, Consumer consumer, String fileName) throws ClientNotIdentifiedException {
        if (this.identifiedClients.contains(consumer)) {
			try {
				requestManager.createRequest(id, consumer, fileName);
			} catch (RequestIDAlreadyExistsException e) {
				e.printStackTrace();
			}
		}	
		else {
			throw new ClientNotIdentifiedException();
		}
    }

    /**
	 * Tries to answer old requests with the newly identified peer.
	 * 
	 * @param provider New client that has identified itself.
	 */
	protected void searchOldRequest(Provider provider) {
		Iterator iterator = this.requestManager.getAllRequests().iterator();
		Request tempRequest;
		while (iterator.hasNext()) {
			tempRequest = (Request)iterator.next();
			try {
			    if (tempRequest.getRetries() <= 0) {
			        requestManager.remove(tempRequest.getId());
			    }
			    else {
					provider.searchFile(tempRequest.getId(), tempRequest.getConsumer(), tempRequest.getFileName());
					tempRequest.decreaseRetries();
				}
			} catch (RemoteException e) {
				logger.error(getClass().getName() + ".searchOldRequest()", "The provider " + provider + " could not be contacted.");
			}
		}
	}

	/**
	 * Sees if the client id identified.
	 * 
	 * @param provider The client.
	 * @return True if it still exists.
	 */
    protected boolean containsClient (Provider provider) {
    	return this.identifiedClients.contains(provider);
    }

}