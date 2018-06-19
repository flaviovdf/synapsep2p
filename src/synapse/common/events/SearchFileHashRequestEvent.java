package synapse.common.events;

import java.rmi.RemoteException;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.Provider;

/**
 * This event is used to perform a new file search informing only the hash.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class SearchFileHashRequestEvent implements ActionEvent {

    /**
     * The provider.
     */
    private Provider provider;

    /**
     * The consumer.
     */
    private Consumer consumer;

    /**
     * The file hash.
     */
    private String hash;

    /**
     * Creates an event that will submit a search to <code>provider</code>
     * telling the parameters <code>hash</code> and <code>consumer</code>.
     * @param provider The provider that the search will be submitted.
     * @param hash The file hash.
     * @param consumer The consumer interested in the file.
     */
    public SearchFileHashRequestEvent(Provider provider, String hash, Consumer consumer) {
        this.provider = provider;
        this.consumer = consumer;
        this.hash = hash;
    }

    /* (non-Javadoc)
     * @see synapse.common.events.ActionEvent#process()
     */
    public void process() {
    	try {
			this.provider.searchFile(this.hash, this.consumer);
		} catch (RemoteException e) {
			Logger.getInstance().error(getClass().getName() + ".process()", "The event could not be processed.");
		}
    }

}
