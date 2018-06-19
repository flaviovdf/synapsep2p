package synapse.client;

import java.rmi.RemoteException;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.EventProcessor;
import synapse.common.Facade;
import synapse.common.FileInfo;
import synapse.common.OperationNotSupportedException;
import synapse.common.Provider;
import synapse.common.SimpleEventProcessor;
import synapse.common.TransferPipe;
import synapse.common.events.SearchFileHashRequestEvent;
import synapse.common.events.SearchFileRequestEvent;

/**
 * The client facade used to execute methods in the <code>TransferCore</code>.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ClientFacade extends Facade implements Consumer, Provider {

    private Client client;
    private TransferCore transferCore;
    private Logger logger;

    public ClientFacade() {
        this(new SimpleEventProcessor());
    }

    /**
     * Used only by tests.
     * 
     * @param eventProcessor
     */
    protected ClientFacade(EventProcessor eventProcessor) {
        super(eventProcessor);
        Runtime.getRuntime().addShutdownHook(new ClientHook());
        this.logger = Logger.getInstance();
    }

    public Client getClient() {
        return this.client;
    }

    /* (non-Javadoc)
     * @see synapse.common.Facade#config()
     */
    public void config() {
        try {
            client = new Client(this);

        } catch (RemoteException e) {
            logger.exception(getClass().getName() + ".config()", e);
        }

        transferCore = new TransferCore();
        transferCore.config(client);
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
        this.eventProcessor.putEvent(new SearchFileRequestEvent(this.transferCore, id, consumer, fileName));
    }

    /* (non-Javadoc)
     * @see synapse.common.Provider#searchFile(java.lang.String, synapse.common.Consumer)
     */
    public void searchFile(String hash, Consumer consumer) throws RemoteException {
        this.eventProcessor.putEvent(new SearchFileHashRequestEvent(this.transferCore, hash, consumer));
        
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
        this.eventProcessor.putEvent(new GetFileRequestEvent(consumer, this.transferCore, hash));
    }


    /* (non-Javadoc)
     * @see synapse.common.Consumer#fileWasFound(synapse.common.Provider, java.lang.String, java.lang.String)
     */
    public void fileWasFound(long id, Provider provider, FileInfo fileInfo) throws RemoteException {
        this.eventProcessor.putEvent(new FileWasFoundNotifyEvent(id, this.transferCore, provider, fileInfo));
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#hereIsFile(synapse.common.TransferPipe)
     */
    public void hereIsFile(TransferPipe pipe) throws RemoteException {
        this.eventProcessor.putEvent(new HereIsFileNotifyEvent(this.transferCore, pipe));        
    }

    /* (non-Javadoc)
     * @see synapse.common.Consumer#ping()
     */
    public void ping() throws RemoteException {
    }

}
