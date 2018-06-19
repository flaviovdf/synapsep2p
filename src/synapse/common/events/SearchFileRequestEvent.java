package synapse.common.events;

import java.rmi.RemoteException;

import org.ourgrid.yal.Logger;

import synapse.common.Consumer;
import synapse.common.Provider;


/**
 * This event is used to perform a new file search.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class SearchFileRequestEvent implements ActionEvent {

    /**
     * The provider.
     */
    private Provider provider;

    /**
     * The search id.
     */
    private long id;

    /**
     * The consumer.
     */
    private Consumer consumer;

    /**
     * The file name.
     */
    private String fileName;

    /**
     * Creates an event that will submit a search to <code>provider</code>
     * telling the parameters <code>id</code>, <code>consumer</code> and <code>fileName</code>.
     * 
     * @param provider The provider that the search will be submitted.
     * @param id The search id.
     * @param consumer The consumer interested in the file.
     * @param fileName The file name.
     */
    public SearchFileRequestEvent(Provider provider, long id, Consumer consumer, String fileName) {
        this.provider = provider;
        this.id = id;
        this.consumer = consumer;
        this.fileName = fileName;
    }

    /* (non-Javadoc)
     * @see @see synapse.common.events.ActionEvent#process()
     */
    public void process() {
        try {
			this.provider.searchFile(this.id, this.consumer, this.fileName);
		} catch (RemoteException e) {
			Logger.getInstance().error(getClass().getName() + ".process()", "The event could not be processed.");
        }
    }

}
