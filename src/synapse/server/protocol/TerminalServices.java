package synapse.server.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * 
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface TerminalServices extends Remote {

    public Collection getKnownTerminals() throws RemoteException;

    public void connect(String address) throws RemoteException;

}
