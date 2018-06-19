package synapse.client;

import java.rmi.RemoteException;

import synapse.common.Consumer;
import synapse.common.FileInfo;
import synapse.common.Provider;
import synapse.common.events.ActionEvent;

/**
 * 
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class FileWasFoundNotifyEvent implements ActionEvent {

    private long id;
    private Consumer consumer;
    private Provider provider;
    private FileInfo fileInfo;

    public FileWasFoundNotifyEvent(long id, Consumer consumer, Provider provider, FileInfo fileInfo) {
        this.id = id;
        this.consumer = consumer;
        this.provider = provider;
        this.fileInfo = fileInfo;
    }

    /* (non-Javadoc)
     * @see synapse.common.events.ActionEvent#process()
     */
    public void process() {
        try {
            this.consumer.fileWasFound(this.id, this.provider, this.fileInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
