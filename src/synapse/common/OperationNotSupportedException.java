package synapse.common;

import java.rmi.RemoteException;

/**
 * This exception is thrown when some operation is not supported
 * by remote service.
 * 
 * @author <p>Flavio Roberto Santos, flaviors@dsc.ufcg.edu.br</p>
 */
public class OperationNotSupportedException extends RemoteException {

    /**
     * Creates the exception.
     */
    public OperationNotSupportedException() {
        super("Operation not supported");
    }

    /**
     * Creates the exception with another message.
     * 
     * @param msg The message.
     */
    public OperationNotSupportedException(String msg) {
    	super(msg);
    }

}
