package synapse.server.ui;

import java.util.Collection;

import synapse.common.UIException;

/**
 * An interface to be implemented by the class
 * that knows how to contact the server services.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public interface ServerUIServices {

    /*
     * Server services
     */

    /**
     * Stop the server.
     * 
     * @throws UIException If the server cannot be stopped. 
     */
    public void stopServer() throws UIException;

    /*
     * Terminal services 
     */

    /**
     * @see synapse.server.protocol.Terminal#getKnownTerminals()
     */
    public Collection getKnownTerminals() throws UIException;

    /**
     * This method links this server to another one.
     * 
     * @param address The RMI address of the other server.
     * @throws UIException If the other server cannot be reached.
     */
    public void connect(String address) throws UIException;

}
