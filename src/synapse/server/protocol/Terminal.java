package synapse.server.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import synapse.common.Consumer;

/**
 * This interface provides access from one terminal to another one.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface Terminal extends Remote {

    /**
     * Returns a <code>Collection</code> containing all the terminals associated with.
     * 
     * @return A <code>Collection</code> of <code>Terminal</code>s.
     * @throws RemoteException If registry can't be contacted.
     */
    public Collection getKnownTerminals() throws RemoteException;

    /**
     * @see synapse.common.Provider#searchFile(long, Consumer, String)
     */
    public void searchFromCommunity(Terminal terminal, long id, Consumer consumer, String fileName) throws RemoteException;

    /**
     * Adds a <code>Terminal</code> to be linked with this one.
     * 
     * @param terminal The other terminal.
     * @throws RemoteException If registry can't be contacted.
     */
    public void linkTo(Terminal terminal) throws RemoteException;

    /**
     * Returns a string representation for this Terminal.
     * 
     * @return A string representation.
     */
    public String getName() throws RemoteException;

}