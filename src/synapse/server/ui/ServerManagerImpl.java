package synapse.server.ui;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import synapse.common.Facade;

/**
 * An implementation of the <code>ServerManager</code>.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerManagerImpl extends UnicastRemoteObject implements ServerManager {

    /**
     * The facade under control by this server.
     */
    private Facade facade;

    /**
     * Creates the server manager.
     * 
     * @throws RemoteException If the service can't be contacted.
     */
    protected ServerManagerImpl(Facade facade) throws RemoteException {
        this.facade = facade;
    }

    /* (non-Javadoc)
     * @see synapse.server.ui.ServerManager#shutdown()
     */
    public void shutdown() throws RemoteException {

        if (facade != null ) {
            facade.shutdown().blockingRemove();
        }

        System.exit(0);
    }

}
