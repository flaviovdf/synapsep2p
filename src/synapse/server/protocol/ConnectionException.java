package synapse.server.protocol;

/**
 * This exception has to be thrown when some error happens during the connection.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class ConnectionException extends RuntimeException {

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public ConnectionException() {
        super();
    }
    
    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message The detail message.
     */
    public ConnectionException(String message) {
        super(message);
    }
}
