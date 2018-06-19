package synapse.server;

import java.rmi.Naming;
import java.rmi.RemoteException;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.EventProcessor;
import synapse.common.EventQueue;
import synapse.common.Facade;
import synapse.common.OperationNotSupportedException;
import synapse.common.Provider;
import synapse.common.SimpleEventProcessor;
import synapse.common.URLProvider;
import synapse.common.events.IdentifyEvent;
import synapse.common.events.SearchFileHashRequestEvent;
import synapse.common.events.SearchFileRequestEvent;

/**
 * The server facade used to execute methods in the <code>ConnectionManager</code>.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerFacade extends Facade implements Provider {

    private Proxy proxy;
    private ConnectionManager connectionManager;
    private Logger logger;

    /**
     * Creates a new ServerFacade.
     */
    public ServerFacade() {
        this(new SimpleEventProcessor());
    }

    /**
     * Used only by tests.
     * 
     * @param eventProcessor
     */
    protected ServerFacade(EventProcessor eventProcessor) {
        super(eventProcessor);
        this.logger = Logger.getInstance();
    }

    public void config() {
        try {
            proxy = new Proxy(this);
            Naming.rebind(URLProvider.Proxy(), proxy);
            logger.info(getClass().getName() + ".config()", "Proxy was successfully binded.");
        } catch (Exception e) {
            logger.exception(getClass().getName() + ".config()", e);
        }
        
        this.connectionManager = new ConnectionManager();
        this.connectionManager.config(proxy);
    }

    public EventQueue shutdown() {
        if (this.proxy != null) {
            this.proxy.shutdown();
        }

        return super.shutdown();
    }

    /* (non-Javadoc)
     * @see synapse.proxy.Provider#identify(synapse.common.Provider)
     */
    public void identify(Provider provider) {
    	this.eventProcessor.putEvent(new IdentifyEvent(this.connectionManager, provider));
    }

    /* (non-Javadoc)
     * @see synapse.server.Provider#searchFile(synapse.common.Consumer, java.lang.String)
     */
    public long searchFile(Consumer consumer, String fileName) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    /* (non-Javadoc)
     * @see synapse.proxy.Provider#searchFile(long, synapse.common.Consumer, java.lang.String)
     */
    public void searchFile(long id, Consumer consumer, String fileName) {
        this.eventProcessor.putEvent(new SearchFileRequestEvent(this.connectionManager, id, consumer, fileName));
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(java.lang.String, synapse.common.Consumer)
     */
    public void searchFile(String hash, Consumer consumer) throws RemoteException {
        this.eventProcessor.putEvent(new SearchFileHashRequestEvent(this.connectionManager, hash, consumer));
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(long, synapse.common.Consumer, java.lang.String)
     */
    public void searchForCommunity(long id, Consumer consumer, String fileName) {
        this.eventProcessor.putEvent(new SearchFileForCommunityRequestEvent(this.connectionManager, id, consumer, fileName));
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#getFile(java.lang.String)
     */
    public void getFile(String hash, Consumer consumer) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

}
