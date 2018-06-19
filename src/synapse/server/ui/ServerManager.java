package synapse.server.ui;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class provides the useful services to be performed in this server.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface ServerManager extends Remote {

	/**
	 * Stops all the entities managed by this class.
	 * @throws RemoteException If any problem occurs when stopping the entities. 
	 */
    public void shutdown() throws RemoteException;

}