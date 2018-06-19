package synapse.server.ui;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.Collection;

import synapse.common.UIException;
import synapse.common.URLProvider;
import synapse.server.ServerNotRunningException;
import synapse.server.protocol.TerminalServices;

/**
 * This class is used to control the server.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerConcreteUIServices implements ServerUIServices {

    /**
     * Object that control the server.
     */
    private ServerManager serverManager;

    /**
     * Object that control the <code>Terminal</code>.
     */
    private TerminalServices terminalServices;

    /**
     * The unique instance of the singleton.
     */
    private static ServerUIServices uniqueInstance;

    /**
     * Creates an instance of the service.
     * The constructor performs a lookup on the services used by the server.
     * 
     * @throws ServerNotRunningException If the server is not running.
     */
    private ServerConcreteUIServices() throws ServerNotRunningException {

        try {
	        this.terminalServices = (TerminalServices) Naming.lookup(URLProvider.TerminalServices());
	        this.serverManager = (ServerManager) Naming.lookup(URLProvider.ServerManager());
        } catch (Exception e) {
            throw new ServerNotRunningException();
        }

    }

    /**
     * Gets the unique instance of the object.
     * 
     * @return The <code>ServerUIServices</code> object.
     * @throws ServerNotRunningException If the server is not running.
     */
    public static ServerUIServices getInstance() throws ServerNotRunningException {
        if (uniqueInstance == null) {
            uniqueInstance = new ServerConcreteUIServices();
        }
        return uniqueInstance;
    }

    /* (non-Javadoc)
     * @see synapse.server.ui.ServerUIServices#stopServer()
     */
    public void stopServer() throws UIException {
        try {
            this.serverManager.shutdown();
        } catch (UnmarshalException e) {

        } catch (RemoteException e) {
            throw new UIException("Could not stop the server");
        }
    }

    /* (non-Javadoc)
     * @see synapse.server.ui.ServerUIServices#getKnownTerminals()
     */
    public Collection getKnownTerminals() throws UIException {
        try {
            return this.terminalServices.getKnownTerminals();
        } catch (RemoteException e) {
            throw new UIException("Could not retrieve known server list");
        }
    }

    /* (non-Javadoc)
     * @see synapse.server.ui.ServerUIServices#connect()
     */
    public void connect(String address) throws UIException {
        try {
            this.terminalServices.connect(address);
        } catch (RemoteException e) {
            throw new UIException("Could not contact the server");
        }
    }

}
