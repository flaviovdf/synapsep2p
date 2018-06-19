package synapse.server;

/**
 * This exception hava to be thrown when the Synapse Server
 * is not running.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ServerNotRunningException extends Exception {

    /**
     * Creates an exception with the default message
     * "Synapse Server is not running!".
     *
     */
    public ServerNotRunningException() {
        super("Synapse Server is not running!");
    }
}
