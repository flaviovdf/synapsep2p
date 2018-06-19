package synapse.client;

import java.rmi.RemoteException;

import synapse.common.Consumer;
import synapse.common.TransferPipe;
import synapse.common.events.ActionEvent;

/**
 * 
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class HereIsFileNotifyEvent implements ActionEvent {

    private Consumer consumer;
    private TransferPipe pipe;
    private String fileName;

    public HereIsFileNotifyEvent(Consumer consumer, TransferPipe pipe) {
        this.consumer = consumer;
        this.pipe = pipe;
    }

    /* (non-Javadoc)
     * @see synapse.common.events.ActionEvent#process()
     */
    public void process() {
        try {
            this.consumer.hereIsFile(this.pipe);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
