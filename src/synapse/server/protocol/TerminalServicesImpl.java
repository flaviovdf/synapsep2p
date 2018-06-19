package synapse.server.protocol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import synapse.common.URLProvider;

/**
 * This service is used to contact the <code>TerminalImpl</code> about some actions
 * like <code>connect()</code> and <code>getKnownTerminals()</code>.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class TerminalServicesImpl extends UnicastRemoteObject implements TerminalServices {

    /**
     * The terminal under control.
     */
    private TerminalImpl terminal;

    /**
     * Creates a new terminal services that controls the <code>terminal</code>
     * in arguments.
     * 
     * @param terminal The object under control.
     * @throws RemoteException If the service can't be contacted.
     */
    public TerminalServicesImpl(TerminalImpl terminal) throws RemoteException {
        this.terminal = terminal;
    }

    /**
     * Configures the terminal services.
     * This method rebinds this object.
     * 
     * @throws RemoteException If registry can't be contacted.
     * @throws MalformedURLException If the name is not an appropriately formatted URL.
     */
    public void config() throws RemoteException, MalformedURLException {
        Naming.rebind(URLProvider.TerminalServices(), this);
    }

    /* (non-Javadoc)
     * @see synapse.server.protocol.TerminalServices#getKnownTerminals()
     */
    public Collection getKnownTerminals() throws RemoteException {
        return this.terminal.getKnownTerminals();
    }

    /* (non-Javadoc)
     * @see synapse.server.protocol.TerminalServices#connect(java.lang.String)
     */
    public void connect(String address) throws RemoteException {
        try {
            this.terminal.connect(address);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

}
