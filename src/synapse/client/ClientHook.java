package synapse.client;

import synapse.client.manager.DownloadManager;

/**
 * This thread is responsible to execute a command before
 * the JVM close operation.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 * @author <p>Vinicius Ferraz C. Florentino, vinicius.ferraz@gmail.com</p>
 */
public class ClientHook extends Thread {

    /**
     * Performs the default close operation.
     */
    public void run() {
        DownloadManager.getInstance().close();
    }

}
