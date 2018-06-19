package synapse.client;

import java.rmi.RemoteException;

import synapse.common.Consumer;
import synapse.common.Provider;
import synapse.common.events.ActionEvent;

/**
 * 
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class GetFileRequestEvent implements ActionEvent {

    private Provider provider;
    private String hash;
    private Consumer consumer;

    public GetFileRequestEvent(Consumer consumer, Provider provider, String hash) {
        this.provider = provider;
        this.hash = hash;
        this.consumer = consumer;
    }

    /* (non-Javadoc)
     * @see synapse.common.events.ActionEvent#process()
     */
    public void process() {
        try {
            this.provider.getFile(this.hash, this.consumer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
